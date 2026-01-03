package io.jgitkins.web.presentation.dto;

import io.jgitkins.web.application.dto.CommitSummary;
import io.jgitkins.web.application.dto.RepositorySummary;

import java.util.List;

public record RepositoryDashboardItem(
		String namespace,
		String ownerSlug,
		String repoName,
		RepositorySummary repository,
		List<CommitSummary> recentCommits,
		int pullRequestCount,
		int issueCount,
		String pullRequestLink,
		String issueLink
) {
}
