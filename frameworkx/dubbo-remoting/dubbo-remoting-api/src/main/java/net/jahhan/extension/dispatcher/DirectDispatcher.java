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
package net.jahhan.extension.dispatcher;

import javax.inject.Singleton;

import com.alibaba.dubbo.common.URL;

import net.jahhan.common.extension.annotation.Extension;
import net.jahhan.spi.ChannelHandler;
import net.jahhan.spi.Dispatcher;

/**
 * 不派发线程池。
 * 
 * @author chao.liuc
 */
@Extension("direct")
@Singleton
public class DirectDispatcher implements Dispatcher {
    
    public static final String NAME = "direct";

    public ChannelHandler dispatch(ChannelHandler handler, URL url) {
        return handler;
    }

}