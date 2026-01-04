package io.jgitkins.web.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final DashboardViewSupport dashboardViewSupport;

	@GetMapping("/")
	public String root(Authentication authentication, Model model) {
		if (isAuthenticated(authentication)) {
			model.addAttribute("displayName", resolveDisplayName(authentication));
			dashboardViewSupport.addDashboardAttributes(model);
			return "dashboard/index";
		}
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "redirect:/oauth2/authorization/google";
	}

	private boolean isAuthenticated(Authentication authentication) {
		return authentication != null
				&& !(authentication instanceof AnonymousAuthenticationToken)
				&& authentication.isAuthenticated();
	}

	private String resolveDisplayName(Authentication authentication) {
		if (authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oauthToken) {
			Object principal = oauthToken.getPrincipal();
			if (principal instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {
				String name = oidcUser.getFullName();
				return org.springframework.util.StringUtils.hasText(name) ? name : oidcUser.getName();
			}
			if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
				String name = oauth2User.getAttribute("name");
				return org.springframework.util.StringUtils.hasText(name) ? name : oauth2User.getName();
			}
		}
		return authentication != null ? authentication.getName() : "there";
	}
}
