package io.jgitkins.web.application.service;

import io.jgitkins.web.application.dto.CommitSummary;
import io.jgitkins.web.application.dto.DashboardData;
import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.RepositoryCommits;
import io.jgitkins.web.application.dto.RepositorySummary;
import io.jgitkins.web.application.port.in.DashboardUseCase;
import io.jgitkins.web.application.port.out.OrganizePort;
import io.jgitkins.web.application.port.out.RepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService implements DashboardUseCase {

	private final OrganizePort organizePort;
	private final RepositoryPort repositoryPort;

	@Override
	public DashboardData buildDashboard() {
		OrganizeFetchResult organizeResult = organizePort.fetchOrganizes();
		List<RepositorySummary> repositories = repositoryPort.fetchRepositories();
		List<RepositoryCommits> items = new ArrayList<>();
		for (RepositorySummary repository : repositories) {
			RepositoryKey key = resolveRepositoryKey(repository);
			if (key == null) {
				continue;
			}
			String branch = StringUtils.hasText(repository.defaultBranch()) ? repository.defaultBranch() : "main";
			List<CommitSummary> commits = repositoryPort.fetchCommits(key.namespace(), key.repoName(), branch).stream()
					.limit(10)
					.toList();
			items.add(new RepositoryCommits(
					key.namespace(),
					key.repoName(),
					repository,
					commits
			));
		}

		return new DashboardData(
				organizeResult.organizes(),
				items,
				organizeResult.errorMessage()
		);
	}

	private RepositoryKey resolveRepositoryKey(RepositorySummary repository) {
		if (repository == null) {
			return null;
		}
		String clonePath = repository.clonePath();
		if (StringUtils.hasText(clonePath)) {
			String trimmed = trimSlashes(clonePath);
			if (trimmed.endsWith(".git")) {
				trimmed = trimmed.substring(0, trimmed.length() - 4);
			}
			String[] parts = trimmed.split("/");
			if (parts.length >= 2) {
				String repoName = parts[parts.length - 1];
				String namespace = String.join("/", java.util.Arrays.copyOf(parts, parts.length - 1));
				return new RepositoryKey(namespace, repoName);
			}
		}
		if (StringUtils.hasText(repository.path())) {
			String[] parts = repository.path().split("/");
			if (parts.length >= 2) {
				String repoName = parts[parts.length - 1];
				String namespace = String.join("/", java.util.Arrays.copyOf(parts, parts.length - 1));
				return new RepositoryKey(namespace, repoName);
			}
		}
		if (StringUtils.hasText(repository.name())) {
			return new RepositoryKey("unknown", repository.name());
		}
		return null;
	}

	private String trimSlashes(String value) {
		if (!StringUtils.hasText(value)) {
			return "";
		}
		return value.replaceAll("^/+", "").replaceAll("/+$", "");
	}

	private record RepositoryKey(String namespace, String repoName) {
	}
}
