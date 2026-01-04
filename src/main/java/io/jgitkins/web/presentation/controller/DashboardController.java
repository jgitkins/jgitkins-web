package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.presentation.dto.DashboardView;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

	private final DashboardViewSupport dashboardViewSupport;

//	@GetMapping("/dashboard")
//	public String dashboard(Model model) {
//		dashboardViewSupport.addDashboardAttributes(model);
//		return "dashboard/index";
//	}

	@GetMapping("/fragments/organizes")
	public String organizeFragment(Model model) {
		DashboardView view = dashboardViewSupport.buildDashboardView();
		model.addAttribute("dashboard", view);
		model.addAttribute("lastUpdated", LocalDateTime.now());
		return "fragments/organizes :: list";
	}

	@GetMapping("/fragments/feed")
	public String feedFragment(Model model) {
		DashboardView view = dashboardViewSupport.buildDashboardView();
		model.addAttribute("dashboard", view);
		return "fragments/feed :: list";
	}
}
