package com.fdmgroup.documentuploader.exception.handler;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.RequestUris;
import com.fdmgroup.documentuploader.enums.ApiUri;
import com.fdmgroup.documentuploader.enums.AttributeName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Handles all custom exceptions thrown from controller request handlers across
 * the entire application.
 * 
 * @author Noah Anderson
 * @see com.fdmgroup.documentuploader.exception
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

	private static final String URL_PREFIX = "http://";
	private static final String REDIRECT = "redirect:";
	/**
	 * Used to retrieve messages from messages.properties.
	 */
	private final MessageSource messageSource;
	private final RequestUris requestUris;

	@Autowired
	public GlobalControllerExceptionHandler(MessageSource messageSource, ApplicationProperties applicationProperties) {
		super();
		this.messageSource = messageSource;
		this.requestUris = applicationProperties.getRequestUris();
	}

	/**
	 * Custom Exception Handler method which handles all Exceptions thrown from
	 * Controllers.
	 * 
	 * @param redirectAttributes encapsulates the Flash Scope and is used to carry
	 *                           attributes through the POST-REDIRECT-GET HTTP
	 *                           pattern
	 * @param e                  the thrown exception
	 * @return the appropriate view template
	 * @throws Exception if the exception is not a custom exception so the Spring
	 *                   framework can handle it
	 */
	@ExceptionHandler(Exception.class)
	public String handleThrownExceptions(RedirectAttributes redirectAttributes, Exception e, HttpServletRequest request)
			throws Exception {
		if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
			throw e;
		}
		if (e.getMessage() != null) {
			redirectAttributes.addFlashAttribute(AttributeName.MESSAGE.getValue(), e.getMessage());
		}

		// get request uri
		String origin = URL_PREFIX + request.getHeader("host");
		if (origin != null) {
			String referer = request.getHeader("referer");
			if (referer != null) {
				String uri = referer.substring(origin.length());
				if (e instanceof WebClientResponseException) {
					WebClientResponseException responseException = (WebClientResponseException) e;
					return handleWebClientResponseException(responseException, redirectAttributes, uri);
				}
				return REDIRECT + uri;
			}
		}
		return REDIRECT + requestUris.getLogin();
	}

	/**
	 * Handles thrown {@link WebClientResponseException} instances and returns the
	 * appropriate view to include in the response.
	 * 
	 * @param responseException  the thrown exception
	 * @param redirectAttributes encapsulates the Flash Scope and is used to carry
	 *                           attributes through the POST-REDIRECT-GET HTTP
	 *                           pattern
	 * @return the appropriate view based on information encapsulated in the
	 *         {@code responseException}
	 */
	private String handleWebClientResponseException(WebClientResponseException responseException,
			RedirectAttributes redirectAttributes, String uri) {
		HttpRequest httpRequest = responseException.getRequest();
		if (httpRequest != null) {
			String requestUri = httpRequest.getURI().toString();

			if (requestUri.contains(ApiUri.ACCOUNTS.getUri()) && requestUri.endsWith(ApiUri.DOCUMENTS.getUri())) {
				redirectAttributes.addFlashAttribute(AttributeName.MESSAGE.getValue(),
						messageSource.getMessage("document.not-added", null, Locale.getDefault()));
			}
			if (uri.contains("changePassword")) {
				redirectAttributes.addFlashAttribute(AttributeName.MESSAGE.getValue(),
						messageSource.getMessage("token.is-invalid", null, Locale.getDefault()));
			}
		}
		return REDIRECT + uri;
	}

}
