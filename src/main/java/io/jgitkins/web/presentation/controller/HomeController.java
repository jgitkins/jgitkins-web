package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.presentation.support.HomeViewSupport;
import io.jgitkins.web.presentation.support.SecuritySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final HomeViewSupport homeViewSupport;
	private final SecuritySupport securitySupport;

	@GetMapping("/")
	public String root(Authentication authentication, Model model, HttpServletRequest request) {
		if (securitySupport.isAuthenticated(authentication)) {
			homeViewSupport.addAuthenticatedHomeAttributes(model, authentication, request);
			return "dashboard/index";
		}
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "redirect:/oauth2/authorization/google";
	}
}
