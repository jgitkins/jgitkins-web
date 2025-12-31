package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.port.in.DashboardUseCase;
import io.jgitkins.web.presentation.dto.DashboardView;
import io.jgitkins.web.presentation.mapper.DashboardViewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final DashboardUseCase dashboardUseCase;
	private final DashboardViewMapper dashboardViewMapper;

	@GetMapping("/")
	public String root(Authentication authentication, Model model) {
		if (isAuthenticated(authentication)) {
			return renderDashboard(model);
		}
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "redirect:/oauth2/authorization/google";
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		return renderDashboard(model);
	}

	@GetMapping("/fragments/organizes")
	public String organizeFragment(Model model) {
		DashboardView view = dashboardViewMapper.toDashboardView(dashboardUseCase.buildDashboard());
		model.addAttribute("dashboard", view);
		model.addAttribute("lastUpdated", LocalDateTime.now());
		return "fragments/organizes :: list";
	}

	@GetMapping("/fragments/feed")
	public String feedFragment(Model model) {
		DashboardView view = dashboardViewMapper.toDashboardView(dashboardUseCase.buildDashboard());
		model.addAttribute("dashboard", view);
		return "fragments/feed :: list";
	}

	@GetMapping({"/explore", "/explore/{type}"})
	public String explore(@org.springframework.web.bind.annotation.PathVariable(required = false) String type, Model model) {
		String resolved = resolveExploreType(type);
		model.addAttribute("exploreType", resolved);
		return "explore";
	}

	@GetMapping("/notifications")
	public String notifications() {
		return "notifications";
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

	private String renderDashboard(Model model) {
		DashboardView view = dashboardViewMapper.toDashboardView(dashboardUseCase.buildDashboard());
		model.addAttribute("dashboard", view);
		model.addAttribute("lastUpdated", LocalDateTime.now());
		return "dashboard";
	}

	private boolean isAuthenticated(Authentication authentication) {
		return authentication != null
				&& !(authentication instanceof AnonymousAuthenticationToken)
				&& authentication.isAuthenticated();
	}
}
