package io.jgitkins.web.presentation.support;

import io.jgitkins.web.application.common.SessionKeys;
import jakarta.servlet.http.HttpServletRequest; import jakarta.servlet.http.HttpSession; import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
@RequiredArgsConstructor
public class HomeViewSupport {

	private final DashboardViewSupport dashboardViewSupport;
	private final UserDisplayNameResolver userDisplayNameResolver;

	public void addAuthenticatedHomeAttributes(Model model,
											   Authentication authentication,
											   HttpServletRequest request) {
		model.addAttribute("displayName", userDisplayNameResolver.resolve(authentication));
		HttpSession session = resolveSession(request);
		String username = resolveUsername(session);
		dashboardViewSupport.addDashboardAttributes(model, username);
		model.addAttribute("pendingUsername", isPendingUsername(session));
		addUsernameSetupError(model, session);
	}








	private String resolveUsername(HttpSession session) {
		if (session == null) {
			return null;
		}
		Object username = session.getAttribute(SessionKeys.USERNAME);
		return username instanceof String value ? value : null;
	}

	private HttpSession resolveSession(HttpServletRequest request) {
		return request != null ? request.getSession(false) : null;
	}

	private boolean isPendingUsername(HttpSession session) {
		return session != null && Boolean.TRUE.equals(session.getAttribute(SessionKeys.PENDING_USERNAME));
	}

	private void addUsernameSetupError(Model model, HttpSession session) {
		if (session == null) {
			return;
		}
		Object error = session.getAttribute(SessionKeys.USERNAME_SETUP_ERROR);
		if (error instanceof String errorMessage) {
			model.addAttribute("usernameSetupError", errorMessage);
			session.removeAttribute(SessionKeys.USERNAME_SETUP_ERROR);
		}
	}
}
