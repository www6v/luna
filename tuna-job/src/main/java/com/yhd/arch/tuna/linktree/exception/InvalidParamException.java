package com.yhd.arch.tuna.linktree.exception;

/**
 * Created by root on 12/16/16.
 */
public class InvalidParamException extends Exception {
	private static final long serialVersionUID = 22584697336399243L;

	public InvalidParamException() {
	}

	public InvalidParamException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidParamException(String message) {
		super(message);
	}

	public InvalidParamException(Throwable cause) {
		super(cause);
	}
}
