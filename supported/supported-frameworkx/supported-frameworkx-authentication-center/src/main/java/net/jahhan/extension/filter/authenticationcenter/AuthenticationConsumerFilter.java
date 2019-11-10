package net.jahhan.extension.filter.authenticationcenter;

import javax.inject.Singleton;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.frameworkx.annotation.Activate;

import net.jahhan.common.extension.annotation.Extension;
import net.jahhan.common.extension.exception.JahhanException;
import net.jahhan.spi.Filter;
import net.jahhan.variable.AuthenticationVariable;

@Activate(group = Constants.CONSUMER, order = -9000)
@Extension("authenticationConsumer")
@Singleton
public class AuthenticationConsumerFilter implements Filter {

	public Result invoke(Invoker<?> invoker, Invocation invocation) throws JahhanException {
		URL url = invoker.getUrl();
		String protocol = url.getProtocol();
		String directUrl = url.getParameter("directUrl");
		if (protocol.equals("rest") && directUrl.equals("true")) {
			((AuthenticationVariable) AuthenticationVariable.getThreadVariable("authentication")).setCrypt(false);
		}
		Result invoke = invoker.invoke(invocation);
		return invoke;
	}

}