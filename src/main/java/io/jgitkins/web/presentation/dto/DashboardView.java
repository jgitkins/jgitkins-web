package io.jgitkins.web.presentation.dto;

import io.jgitkins.web.application.dto.OrganizeSummary;

import java.util.List;

public record DashboardView(
		List<OrganizeSummary> organizes,
		List<RepositoryDashboardItem> repositories,
		List<FeedItem> feed,
		String errorMessage
) {
}
