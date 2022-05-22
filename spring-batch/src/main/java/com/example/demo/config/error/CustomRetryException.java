package com.example.demo.config.error;

@SuppressWarnings("serial")
public class CustomRetryException extends Exception {
	public CustomRetryException() {
		super();
	}

	public CustomRetryException(String msg) {
		super(msg);
	}
}
