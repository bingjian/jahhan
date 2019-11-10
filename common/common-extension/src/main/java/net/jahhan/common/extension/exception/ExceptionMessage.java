package net.jahhan.common.extension.exception;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class ExceptionMessage implements Serializable{
	private static final long serialVersionUID = -4302161171854854646L;
	private long threadId;
	private String threadName;
	private String requestId;
	private String chainId;
	private String host;
	private String service;
	private String code;
	private String message;
	private Date time;
	private ExceptionMessage cause;
}
