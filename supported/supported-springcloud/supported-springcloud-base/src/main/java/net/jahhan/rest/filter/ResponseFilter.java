package net.jahhan.rest.filter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Date;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.apache.catalina.connector.Response;

import net.jahhan.common.extension.constant.BaseConfiguration;
import net.jahhan.common.extension.constant.JahhanErrorCode;
import net.jahhan.common.extension.exception.HttpExceptionMessage;
import net.jahhan.common.extension.utils.LocalIpUtils;

@Provider
public class ResponseFilter implements ContainerResponseFilter {
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

		if (Response.SC_NOT_FOUND == responseContext.getStatus()) {
			String escapedMsg = "接口不存在";
			HttpExceptionMessage exceptionMessage = new HttpExceptionMessage();
			exceptionMessage.setHttpStatus(Response.SC_NOT_FOUND);
			exceptionMessage.setCode(JahhanErrorCode.UNKNOW_SERVICE_EXCEPTION);
			exceptionMessage.setMessage(escapedMsg);
			exceptionMessage.setService(BaseConfiguration.SERVICE);
			exceptionMessage.setHost(LocalIpUtils.getFirstIp());
			exceptionMessage.setThreadId(Thread.currentThread().getId());
			exceptionMessage.setThreadName(Thread.currentThread().getName());
			exceptionMessage.setTime(new Date());
			responseContext.setEntity(exceptionMessage, new Annotation[] {}, MediaType.valueOf("application/json"));
		} else if (Response.SC_BAD_REQUEST == responseContext.getStatus()) {
			Object entity = responseContext.getEntity();
			if (entity != null && entity instanceof String) {
				if (entity.equals("java.io.EOFException: No content to map to Object due to end of input")) {
					HttpExceptionMessage exceptionMessage = new HttpExceptionMessage();
					exceptionMessage.setHttpStatus(Response.SC_NOT_FOUND);
					exceptionMessage.setCode(JahhanErrorCode.VALIATION_EXCEPTION);
					exceptionMessage.setMessage("错误的请求内容");
					exceptionMessage.setService(BaseConfiguration.SERVICE);
					exceptionMessage.setHost(LocalIpUtils.getFirstIp());
					exceptionMessage.setThreadId(Thread.currentThread().getId());
					exceptionMessage.setThreadName(Thread.currentThread().getName());
					exceptionMessage.setTime(new Date());
					responseContext.setEntity(exceptionMessage, new Annotation[] {},
							MediaType.valueOf("application/json"));
				} else if (entity.toString().startsWith("com.fasterxml.jackson.core.JsonParseException:")) {
					HttpExceptionMessage exceptionMessage = new HttpExceptionMessage();
					exceptionMessage.setHttpStatus(Response.SC_BAD_REQUEST);
					exceptionMessage.setCode(JahhanErrorCode.PARAMETER_ERROR);
					exceptionMessage.setMessage("请求参数JSON转化错误");
					exceptionMessage.setService(BaseConfiguration.SERVICE);
					exceptionMessage.setHost(LocalIpUtils.getFirstIp());
					exceptionMessage.setThreadId(Thread.currentThread().getId());
					exceptionMessage.setThreadName(Thread.currentThread().getName());
					exceptionMessage.setTime(new Date());
					responseContext.setEntity(exceptionMessage, new Annotation[] {},
							MediaType.valueOf("application/json"));
				}
			}

		}
	}
}
