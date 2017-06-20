package com.javaxxw.common.exception;


/**
 * @desc 
 * @author Administrator
 * @since 2017/6/15
 * @version 1.0
 */
@SuppressWarnings("serial")
public class DataParseException extends BaseException {

	public DataParseException() {
	}

	public DataParseException(Throwable ex) {
		super(ex);
	}

	public DataParseException(String message) {
		super(message);
	}

	public DataParseException(String message, Throwable ex) {
		super(message, ex);
	}

}
