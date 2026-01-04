package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.dto.UserCredentialIssueRequest;
import io.jgitkins.web.application.port.in.PersonalAccessTokenIssueUseCase;
import io.jgitkins.web.application.port.in.PersonalAccessTokenQueryUseCase;
import io.jgitkins.web.presentation.dto.PersonalAccessTokenForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SettingsController {

	private final PersonalAccessTokenQueryUseCase personalAccessTokenQueryUseCase;
	private final PersonalAccessTokenIssueUseCase personalAccessTokenIssueUseCase;

	@GetMapping("/settings/profile")
	public String profile() {
		return "settings/profile";
	}

	@GetMapping("/settings")
	public String settingsRoot() {
		return "redirect:/settings/profile";
	}

	@GetMapping("/settings/personal-access-tokens")
	public String personalAccessTokens(Model model) {
		model.addAttribute("tokens", personalAccessTokenQueryUseCase.fetchPersonalAccessTokens());
		return "settings/personal-access-tokens/index";
	}

	@GetMapping("/settings/personal-access-tokens/new")
	public String newPersonalAccessToken() {
		return "settings/personal-access-tokens/new";
	}

	@PostMapping("/settings/personal-access-tokens")
	public String createPersonalAccessToken(@ModelAttribute PersonalAccessTokenForm form,
											Model model,
											RedirectAttributes redirectAttributes) {
		if (!StringUtils.hasText(form.getName()) || !StringUtils.hasText(form.getDescription())
				|| !StringUtils.hasText(form.getExpiration())) {
			model.addAttribute("error", "All fields are required.");
			return "settings/personal-access-tokens/new";
		}

		try {
			var issued = personalAccessTokenIssueUseCase.issueToken(
					new UserCredentialIssueRequest(form.getName(), form.getDescription())
			);
			redirectAttributes.addFlashAttribute("issuedToken", issued.token());
			return "redirect:/settings/personal-access-tokens";
		} catch (RuntimeException ex) {
			model.addAttribute("error", "Failed to create token. Please try again.");
			return "settings/personal-access-tokens/new";
		}
	}

	@PostMapping("/settings/personal-access-tokens/{credentialId}/delete")
	public String deletePersonalAccessToken(@PathVariable("credentialId") Long credentialId,
											Model model) {
		try {
			personalAccessTokenIssueUseCase.revokeToken(credentialId);
		} catch (RuntimeException ex) {
			model.addAttribute("error", "Failed to delete token. Please try again.");
			model.addAttribute("tokens", personalAccessTokenQueryUseCase.fetchPersonalAccessTokens());
			return "settings/personal-access-tokens/index";
		}
		return "redirect:/settings/personal-access-tokens";
	}
}
