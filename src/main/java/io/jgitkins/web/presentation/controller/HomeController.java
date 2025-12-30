package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.port.in.DashboardUseCase;
import io.jgitkins.web.presentation.dto.DashboardView;
import io.jgitkins.web.presentation.mapper.DashboardViewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final DashboardUseCase dashboardUseCase;
	private final DashboardViewMapper dashboardViewMapper;

	@GetMapping("/")
	public String root() {
		return "redirect:/dashboard";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		DashboardView view = dashboardViewMapper.toDashboardView(dashboardUseCase.buildDashboard());
		model.addAttribute("dashboard", view);
		model.addAttribute("lastUpdated", LocalDateTime.now());
		return "dashboard";
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

	@GetMapping("/explore")
	public String explore() {
		return "explore";
	}
}
