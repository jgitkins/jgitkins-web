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
}
