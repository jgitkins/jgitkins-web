package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.presentation.dto.DashboardView;
import java.time.LocalDateTime;

import io.jgitkins.web.presentation.support.DashboardViewSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/fragments")
@RequiredArgsConstructor
public class DashboardController {

	private final DashboardViewSupport dashboardViewSupport;

//	@GetMapping("/dashboard")
//	public String dashboard(Model model) {
//		dashboardViewSupport.addDashboardAttributes(model);
//		return "dashboard/index";
//	}

	@GetMapping("/organizes")
	public String organizeFragment(Model model) {
		DashboardView view = dashboardViewSupport.buildDashboardView();
		model.addAttribute("dashboard", view);
		model.addAttribute("lastUpdated", LocalDateTime.now());
		return "fragments/organizes :: list";
	}

	@GetMapping("/feed")
	public String feedFragment(Model model) {
		DashboardView view = dashboardViewSupport.buildDashboardView();
		model.addAttribute("dashboard", view);
		return "fragments/feed :: list";
	}
}
