package io.jgitkins.web.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ExploreController {

	@GetMapping({"/explore", "/explore/{type}"})
	public String explore(@PathVariable(required = false) String type, Model model) {
		String resolved = resolveExploreType(type);
		model.addAttribute("exploreType", resolved);
		return "explore/index";
	}

	private String resolveExploreType(String type) {
		if (type == null || type.isBlank()) {
			return "repositories";
		}
		return switch (type) {
			case "repositories", "users", "organizations" -> type;
			default -> "repositories";
		};
	}
}
