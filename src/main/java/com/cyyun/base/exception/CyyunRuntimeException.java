package com.cyyun.base.exception;

public class CyyunRuntimeException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public CyyunRuntimeException() {
        super();
    }

    public CyyunRuntimeException(String message) {
        super(message);
    }

    public CyyunRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CyyunRuntimeException(Throwable cause) {
        super(cause);
    }
}
