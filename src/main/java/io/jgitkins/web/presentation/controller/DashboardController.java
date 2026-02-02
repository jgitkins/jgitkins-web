package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.presentation.dto.DashboardView;
import java.time.LocalDateTime;

import io.jgitkins.web.presentation.support.DashboardViewSupport;
import io.jgitkins.web.presentation.support.SessionSupport;
import jakarta.servlet.http.HttpServletRequest;
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
	private final SessionSupport sessionSupport;

	@GetMapping("/organizes")
	public String organizeFragment(Model model, HttpServletRequest request) {
		DashboardView view = dashboardViewSupport.buildDashboardView(resolveUsername(request));
		model.addAttribute("dashboard", view);
		model.addAttribute("lastUpdated", LocalDateTime.now());
		return "fragments/organizes :: list";
	}

	@GetMapping("/feed")
	public String feedFragment(Model model, HttpServletRequest request) {
		DashboardView view = dashboardViewSupport.buildDashboardView(resolveUsername(request));
		model.addAttribute("dashboard", view);
		return "fragments/feed :: list";
	}

	private String resolveUsername(HttpServletRequest request) {
		return sessionSupport.resolveUsername(sessionSupport.resolveSession(request));
	}
}
