package io.jgitkins.web.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController {

	@GetMapping("/settings/profile")
	public String profile() {
		return "settings/profile";
	}

	@GetMapping("/settings/personal-access-tokens")
	public String personalAccessTokens() {
		return "settings/personal-access-tokens/index";
	}

	@GetMapping("/settings/personal-access-tokens/new")
	public String newPersonalAccessToken() {
		return "settings/personal-access-tokens/new";
	}
}
