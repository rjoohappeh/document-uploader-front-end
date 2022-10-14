package com.fdmgroup.documentuploader.aop.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory that creates Logger objects.
 * 
 * @author Noah Anderson
 *
 */
public class LoggerFactory {

	private LoggerFactory() {

	}

	/**
	 * Creates a {@link Logger} object based on the class type of {@code clazz}.
	 * 
	 * @param clazz the class which will have a Logger created for it.
	 * @return the Logger instance created
	 */
	public static Logger getLogger(Class<?> clazz) {
		return LogManager.getLogger(clazz);
	}
	
}
