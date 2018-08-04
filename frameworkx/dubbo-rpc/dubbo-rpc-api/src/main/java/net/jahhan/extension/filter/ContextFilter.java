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
package net.jahhan.extension.filter;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.frameworkx.annotation.Activate;

import net.jahhan.common.extension.annotation.Extension;
import net.jahhan.common.extension.exception.JahhanException;
import net.jahhan.spi.Filter;

/**
 * ContextInvokerFilter
 * 
 * @author william.liangf
 */
@Activate(group = Constants.PROVIDER, order = Integer.MIN_VALUE)
@Extension("context")
@Singleton
public class ContextFilter implements Filter {

	public Result invoke(Invoker<?> invoker, Invocation invocation) throws JahhanException {
		Map<String, String> attachments = invocation.getAttachments();
		if (attachments != null) {
			attachments = new HashMap<String, String>(attachments);
			attachments.remove(Constants.PATH_KEY);
			attachments.remove(Constants.GROUP_KEY);
			attachments.remove(Constants.VERSION_KEY);
			attachments.remove(Constants.DUBBO_VERSION_KEY);
			attachments.remove(Constants.TOKEN_KEY);
			attachments.remove(Constants.TIMEOUT_KEY);
			attachments.remove(Constants.ASYNC_KEY);//清空消费端的异步参数
		}
		RpcContext.getContext().setInvoker(invoker).setInvocation(invocation)
				// .setAttachments(attachments) // modified by lishen
				.setLocalAddress(invoker.getUrl().getHost(), invoker.getUrl().getPort());

		// modified by lishen
		if (attachments != null) {
			if (RpcContext.getContext().getAttachments() != null) {
				RpcContext.getContext().getAttachments().putAll(attachments);
			} else {
				RpcContext.getContext().setAttachments(attachments);
			}
		}

		if (invocation instanceof RpcInvocation) {
			((RpcInvocation) invocation).setInvoker(invoker);
		}
		try {
			return invoker.invoke(invocation);
		} finally {
			RpcContext.removeContext();
		}
	}
}