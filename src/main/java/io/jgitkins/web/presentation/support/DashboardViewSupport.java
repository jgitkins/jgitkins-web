package io.jgitkins.web.presentation.support;

import io.jgitkins.web.application.dto.DashboardData;
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

	public DashboardView buildDashboardView(String username) {
        DashboardData data = dashboardUseCase.buildDashboardForUser(username);
		return dashboardViewMapper.toDashboardView(data);
	}

	public void addDashboardAttributes(Model model, String username) {
		DashboardView view = buildDashboardView(username);
		model.addAttribute("dashboard", view);
		model.addAttribute("lastUpdated", LocalDateTime.now());
	}
}
