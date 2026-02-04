package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.common.SessionKeys;
import io.jgitkins.web.application.dto.UsernameUpdateResult;
import io.jgitkins.web.application.port.out.UserPort;
import io.jgitkins.web.presentation.dto.UsernameSetupForm;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UsernameSetupController {

	private final UserPort userPort;

	@GetMapping("/onboarding/username")
	public String usernameForm(Model model) {
		model.addAttribute("form", new UsernameSetupForm());
		return "onboarding/username";
	}

	@PostMapping("/onboarding/username")
	public String submitUsername(@ModelAttribute("form") UsernameSetupForm form,
								 Model model,
								 HttpServletRequest request) {
		if (form == null || !StringUtils.hasText(form.getUsername())) {
			storeError(request, "Username is required.");
			return "redirect:/";
		}
		UsernameUpdateResult result = userPort.updateUsername(form.getUsername());
		if (result.errorMessage() != null) {
			storeError(request, result.errorMessage());
			return "redirect:/";
		}
		storeUsername(request, form.getUsername());
		clearPending(request);
		return "redirect:/";
	}

	private void storeError(HttpServletRequest request, String message) {
		if (request.getSession(true) != null) {
			request.getSession(true).setAttribute(SessionKeys.USERNAME_SETUP_ERROR, message);
		}
	}

	private void clearPending(HttpServletRequest request) {
		if (request.getSession(false) != null) {
			request.getSession(false).removeAttribute(SessionKeys.PENDING);
		}
	}

	private void storeUsername(HttpServletRequest request, String username) {
		if (request.getSession(true) != null) {
			request.getSession(true).setAttribute(SessionKeys.USERNAME, username);
		}
	}
}
