package com.fdmgroup.documentuploader.aop.logging;

import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Performs all operations related to logging.
 * 
 * @author Noah Anderson
 *
 */
@Component
@Aspect
public class LoggingAspect {

	private static final String ENTERING_METHOD = "Entering method: ";
	private static final String EXITING_METHOD = "Exiting method: ";

	/**
	 * Logs the entering of a method.
	 * 
	 * @param point contains information related to the JoinPoint's location in
	 *              the code base
	 */
	@Before("execution(* com.fdmgroup.documentuploader.controller.*.*(..))")
	public void logEnterControllerMethod(JoinPoint point) {
		logToInfo(point, ENTERING_METHOD);
	}

	/**
	 * Logs the exiting of a method.
	 * 
	 * @param point contains information related to the JoinPoint's location in
	 *              the code base
	 */
	@After("execution(* com.fdmgroup.documentuploader.controller.*.*(..))")
	public void logExitControllerMethod(JoinPoint point) {
		logToInfo(point, EXITING_METHOD);
	}

	/**
	 * Logs the value in the parameter {@code string} to info with the class and
	 * method name obtained through the {@link JoinPoint} instance.
	 * 
	 * @param point contains information related to the JoinPoint's location in
	 *               the code base
	 * @param string the value to be logged
	 */
	private void logToInfo(JoinPoint point, String string) {
		Logger logger = getLogger(point);
		Signature signature = getJoinPointSignature(point);
		String methodName = getMethodNameFromSignature(signature);
		logger.info(string + methodName);
	}

	/**
	 * Creates a logger with the originating class of the {@link JoinPoint}
	 * instance.
	 * 
	 * @param point used to obtain the class of the JoinPoint in the code base
	 * @return a new logger
	 */
	private Logger getLogger(JoinPoint point) {
		Class<?> clazz = getJoinPointClass(point);
		return LoggerFactory.getLogger(clazz);
	}

	/**
	 * Gets the originating class of the {@link JoinPoint} instance.
	 * 
	 * @param point used to obtain the class of the JoinPoint in the code base
	 * @return the class within which the {@link JoinPoint} instance was created
	 */
	private Class<?> getJoinPointClass(JoinPoint point) {
		Signature signature = getJoinPointSignature(point);
		return signature.getDeclaringType();
	}

	/**
	 * Gets the {@link Signature} related to the {@link JoinPoint} instance given.
	 * 
	 * @param point used to obtain the signature
	 * @return the signature of {@code point}
	 */
	private Signature getJoinPointSignature(JoinPoint point) {
		return point.getSignature();
	}

	/**
	 * Gets the name of the method within the code base from which the
	 * {@link Signature} object originated.
	 * 
	 * @param signature provides access to information related to the point of
	 *                  origination of the {@link JoinPoint}
	 * @return the method name which created the {@code signature} instance
	 */
	private String getMethodNameFromSignature(Signature signature) {
		return signature.getName();
	}
}
