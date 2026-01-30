package io.jgitkins.web.presentation.support;

import io.jgitkins.web.application.common.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionSupport {

	public HttpSession resolveSession(HttpServletRequest request) {
		return request != null ? request.getSession(false) : null;
	}

	public String resolveUsername(HttpSession session) {
		if (session == null) {
			return null;
		}
		Object username = session.getAttribute(SessionKeys.USERNAME);
		return username instanceof String value ? value : null;
	}

	public boolean isPendingUsername(HttpSession session) {
		return session != null && Boolean.TRUE.equals(session.getAttribute(SessionKeys.PENDING_USERNAME));
	}

	public String popUsernameSetupError(HttpSession session) {
		if (session == null) {
			return null;
		}
		Object error = session.getAttribute(SessionKeys.USERNAME_SETUP_ERROR);
		if (error instanceof String errorMessage) {
			session.removeAttribute(SessionKeys.USERNAME_SETUP_ERROR);
			return errorMessage;
		}
		return null;
	}
}
