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
package net.jahhan.spi;

import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.cluster.Directory;
import com.frameworkx.annotation.Adaptive;

import net.jahhan.common.extension.annotation.SPI;
import net.jahhan.common.extension.exception.JahhanException;
import net.jahhan.extension.cluster.FailoverCluster;

/**
 * Cluster. (SPI, Singleton, ThreadSafe)
 * 
 * <a href="http://en.wikipedia.org/wiki/Computer_cluster">Cluster</a>
 * <a href="http://en.wikipedia.org/wiki/Fault-tolerant_system">Fault-Tolerant</a>
 * 
 * @author william.liangf
 */
@SPI(FailoverCluster.NAME)
public interface Cluster {

    /**
     * Merge the directory invokers to a virtual invoker.
     * 
     * @param <T>
     * @param directory
     * @return cluster invoker
     * @throws JahhanException
     */
    @Adaptive
    <T> Invoker<T> join(Directory<T> directory) throws JahhanException;

}