package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.port.in.DashboardUseCase;
import io.jgitkins.web.presentation.dto.DashboardView;
import io.jgitkins.web.presentation.mapper.DashboardViewMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
@RequiredArgsConstructor
public class DashboardViewSupport {

	private final DashboardUseCase dashboardUseCase;
	private final DashboardViewMapper dashboardViewMapper;

	public DashboardView buildDashboardView() {
		return dashboardViewMapper.toDashboardView(dashboardUseCase.buildDashboard());
	}

	public void addDashboardAttributes(Model model) {
		DashboardView view = buildDashboardView();
		model.addAttribute("dashboard", view);
		model.addAttribute("lastUpdated", LocalDateTime.now());
	}
}
