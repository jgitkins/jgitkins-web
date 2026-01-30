package io.jgitkins.web.presentation.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
@RequiredArgsConstructor
public class HomeViewSupport {

	private final DashboardViewSupport dashboardViewSupport;
	private final UserDisplayNameResolver userDisplayNameResolver;
	private final SessionSupport sessionSupport;

	public void addAuthenticatedHomeAttributes(Model model,
											   Authentication authentication,
											   HttpServletRequest request) {

		HttpSession session = sessionSupport.resolveSession(request);
		String username = sessionSupport.resolveUsername(session);
		dashboardViewSupport.addDashboardAttributes(model, username);
		String usernameSetupError = sessionSupport.popUsernameSetupError(session);
		if (usernameSetupError != null) {
			model.addAttribute("usernameSetupError", usernameSetupError);
		}


		model.addAttribute("pendingUsername", sessionSupport.isPendingUsername(session));
		model.addAttribute("displayName", userDisplayNameResolver.resolve(authentication));
	}
}
