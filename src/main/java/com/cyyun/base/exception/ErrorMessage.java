package com.cyyun.base.exception;

/**
 * <h3>错误消息</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class ErrorMessage extends Exception {
	private static final long serialVersionUID = 1L;

	public ErrorMessage() {
		super();
	}

	public ErrorMessage(String message, Throwable cause) {
		super(message, cause);
	}

	public ErrorMessage(String message) {
		super(message);
	}

	public ErrorMessage(Throwable cause) {
		super(cause);
	}

}
