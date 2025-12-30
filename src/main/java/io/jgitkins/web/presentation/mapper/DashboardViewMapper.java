package io.jgitkins.web.presentation.mapper;

import io.jgitkins.web.application.dto.CommitSummary;
import io.jgitkins.web.application.dto.DashboardData;
import io.jgitkins.web.application.dto.RepositoryCommits;
import io.jgitkins.web.infrastructure.config.JgitkinsWebProperties;
import io.jgitkins.web.presentation.dto.DashboardView;
import io.jgitkins.web.presentation.dto.FeedItem;
import io.jgitkins.web.presentation.dto.RepositoryDashboardItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DashboardViewMapper {

	private final JgitkinsWebProperties webProperties;

	public DashboardView toDashboardView(DashboardData data) {
		List<RepositoryDashboardItem> items = new ArrayList<>();
		List<FeedItem> feed = new ArrayList<>();
		for (RepositoryCommits repository : data.repositories()) {
			items.add(new RepositoryDashboardItem(
					repository.namespace(),
					repository.repoName(),
					repository.repository(),
					repository.commits(),
					0,
					0,
					buildLink(webProperties.prUrlTemplate(), repository),
					buildLink(webProperties.issueUrlTemplate(), repository)
			));
			for (CommitSummary commit : repository.commits()) {
				if (commit.commitTime() != null) {
					feed.add(new FeedItem(
							repository.namespace(),
							repository.repoName(),
							commit.shortMessage(),
							commit.authorName(),
							commit.commitTime()
					));
				}
			}
		}
		feed.sort(Comparator.comparing(FeedItem::commitTime).reversed());
		if (feed.size() > 10) {
			feed = feed.subList(0, 10);
		}
		return new DashboardView(
				data.organizes(),
				items,
				feed,
				data.errorMessage()
		);
	}

	private String buildLink(String template, RepositoryCommits repository) {
		if (!StringUtils.hasText(template)) {
			return null;
		}
		return template
				.replace("{namespace}", repository.namespace())
				.replace("{repo}", repository.repoName());
	}
}
