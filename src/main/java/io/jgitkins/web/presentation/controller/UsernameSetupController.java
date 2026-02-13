package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.dto.UsernameUpdateResult;
import io.jgitkins.web.application.port.out.UserPort;
import io.jgitkins.web.presentation.dto.UsernameSetupForm;
import io.jgitkins.web.presentation.support.SessionSupport;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
	private final SessionSupport sessionSupport;
	private final MessageSource messageSource;

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
			storeError(request, getMessage("error.username.required"));
			return "redirect:/";
		}
		UsernameUpdateResult result = userPort.updateUsername(form.getUsername());
		if (result.errorMessage() != null) {
			storeError(request, result.errorMessage());
			return "redirect:/";
		}
		storeUsername(request, form.getUsername());
		activateUser(request);
		return "redirect:/";
	}

	private void storeError(HttpServletRequest request, String message) {
		sessionSupport.storeUsernameSetupError(request, message);
	}

	private void activateUser(HttpServletRequest request) {
		sessionSupport.activateUser(request);
	}

	private void storeUsername(HttpServletRequest request, String username) {
		sessionSupport.storeUsername(request, username);
	}

	private String getMessage(String code) {
		return messageSource.getMessage(code, null, code, LocaleContextHolder.getLocale());
	}
}
