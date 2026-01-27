package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.dto.NamespaceDetailResult;
import io.jgitkins.web.application.port.in.NamespaceDetailUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class NamespaceDetailController {

	private final NamespaceDetailUseCase namespaceDetailUseCase;

	@GetMapping("/{namespace}")
	public String namespaceDetail(@PathVariable("namespace") String namespace, Model model) {
		NamespaceDetailResult result = namespaceDetailUseCase.loadNamespaceDetail(namespace);
		if (result.organization()) {
			model.addAttribute("detail", result);
			return "namespaces/organization-detail";
		}
		return "redirect:/" + namespace + "/-/repositories";
	}

	@GetMapping("/{namespace}/-/repositories")
	public String userRepositories(@PathVariable("namespace") String namespace, Model model) {
		NamespaceDetailResult result = namespaceDetailUseCase.loadNamespaceDetail(namespace);
		model.addAttribute("detail", result);
		model.addAttribute("activeTab", "repositories");
		return "namespaces/user-detail";
	}

	@GetMapping("/{namespace}/-/stars")
	public String userStars(@PathVariable("namespace") String namespace, Model model) {
		NamespaceDetailResult result = namespaceDetailUseCase.loadNamespaceDetail(namespace);
		model.addAttribute("detail", result);
		model.addAttribute("activeTab", "stars");
		return "namespaces/user-stars";
	}
}
