/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.remoting.transport.grizzly;

import java.util.concurrent.TimeUnit;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.strategies.SameThreadIOStrategy;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.transport.AbstractClient;

import lombok.extern.slf4j.Slf4j;
import net.jahhan.spi.ChannelHandler;

/**
 * GrizzlyClient
 * 
 * @author william.liangf
 */
@Slf4j
public class GrizzlyClient extends AbstractClient {
    
    private TCPNIOTransport transport;

    private volatile Connection<?> connection; // volatile, please copy reference to use

    public GrizzlyClient(URL url, ChannelHandler handler) throws RemotingException {
        super(url, handler);
    }

    @Override
    protected void doOpen() throws Throwable {
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        filterChainBuilder.add(new TransportFilter());
        filterChainBuilder.add(new GrizzlyCodecAdapter(getCodec(), getUrl(), this));
        filterChainBuilder.add(new GrizzlyHandler(getUrl(), this));
        TCPNIOTransportBuilder builder = TCPNIOTransportBuilder.newInstance();
        ThreadPoolConfig config = builder.getWorkerThreadPoolConfig(); 
        config.setPoolName(CLIENT_THREAD_POOL_NAME)
                .setQueueLimit(-1)
                .setCorePoolSize(0)
                .setMaxPoolSize(Integer.MAX_VALUE)
                .setKeepAliveTime(60L, TimeUnit.SECONDS);
        builder.setTcpNoDelay(true).setKeepAlive(true)
                .setConnectionTimeout(getTimeout())
                .setIOStrategy(SameThreadIOStrategy.getInstance());
        transport = builder.build();
        transport.setProcessor(filterChainBuilder.build());
        transport.start();
    }

    

    @Override
    protected void doConnect() throws Throwable {
        connection = transport.connect(getConnectAddress())
                        .get(getUrl().getPositiveParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS);
    }

    @Override
    protected void doDisConnect() throws Throwable {
        try {
            GrizzlyChannel.removeChannelIfDisconnectd(connection);
        } catch (Throwable t) {
            log.warn(t.getMessage());
        }
    }

    @Override
    protected void doClose() throws Throwable {
        try {
            transport.stop();
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
    
    @Override
    protected Channel getChannel() {
        Connection<?> c = connection;
        if (c == null || ! c.isOpen())
            return null;
        return GrizzlyChannel.getOrAddChannel(c, getUrl(), this);
    }

}