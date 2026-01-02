package io.jgitkins.web.infrastructure.security;

import io.jgitkins.web.application.common.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SessionTokenProvider {

	public String getToken() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			return null;
		}
		HttpServletRequest request = attributes.getRequest();
		if (request == null || request.getSession(false) == null) {
			return null;
		}
		Object token = request.getSession(false).getAttribute(SessionKeys.APP_TOKEN);
		return token instanceof String ? (String) token : null;
	}
}
