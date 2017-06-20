package com.javaxxw.common.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;


/**
 * @desc 
 * @author Administrator
 * @since 2017/6/15
 * @version 1.0
 */
@SuppressWarnings("serial")
public abstract class BaseException extends RuntimeException {
	public BaseException() {
	}

	public BaseException(Throwable ex) {
		super(ex);
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(String message, Throwable ex) {
		super(message, ex);
	}

}
