package io.jgitkins.web.application.service;

import io.jgitkins.web.application.dto.BranchSummary;
import io.jgitkins.web.application.dto.RepositoryDetailData;
import io.jgitkins.web.application.dto.RepositoryFileEntry;
import io.jgitkins.web.application.dto.RepositoryOverviewResult;
import io.jgitkins.web.application.dto.RepositorySummary;
import io.jgitkins.web.application.port.in.RepositoryDetailUseCase;
import io.jgitkins.web.application.port.out.RepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RepositoryDetailService implements RepositoryDetailUseCase {

	private final RepositoryPort repositoryPort;

	@Override
	public RepositoryDetailData loadRepositoryDetail(Long repositoryId, String branch) {
		RepositoryOverviewResult overview = repositoryPort.fetchRepositoryOverview(repositoryId, branch);
		return buildDetail(overview);
	}

	@Override
	public RepositoryDetailData loadRepositoryDetailByPath(String namespace, String repoName, String branch) {
		RepositorySummary repository = repositoryPort.fetchRepositories().stream()
				.filter(item -> matchesRepository(item, namespace, repoName))
				.findFirst()
				.orElse(null);
		if (repository == null || repository.id() == null) {
			return new RepositoryDetailData(null, List.of(), List.of(), null, null, null, null,
					"Repository not found.");
		}
		RepositoryOverviewResult overview = repositoryPort.fetchRepositoryOverview(repository.id(), branch);
		return buildDetail(overview);
	}

	private RepositoryDetailData buildDetail(RepositoryOverviewResult overview) {
		if (overview == null || overview.repository() == null) {
			return new RepositoryDetailData(null, List.of(), List.of(), null, null, null, null,
					"Repository not found.");
		}
		RepositorySummary repository = overview.repository();
		RepositoryKey key = resolveRepositoryKey(repository);
		if (key == null) {
			return new RepositoryDetailData(repository, List.of(), List.of(), null, null, null, null,
					"Repository path is missing.");
		}

		String ownerSlug = lastSegment(key.namespace());
		String selectedBranch = StringUtils.hasText(overview.selectedBranch()) ? overview.selectedBranch() : "main";
		List<BranchSummary> branches = overview.branches() == null ? List.of() : overview.branches();
		List<RepositoryFileEntry> files = overview.tree() == null ? List.of() : overview.tree();

		return new RepositoryDetailData(
				repository,
				branches,
				files,
				key.namespace(),
				ownerSlug,
				key.repoName(),
				selectedBranch,
				null
		);
	}

	private boolean matchesRepository(RepositorySummary repository, String namespace, String repoName) {
		if (repository == null || !StringUtils.hasText(repoName)) {
			return false;
		}
		RepositoryKey key = resolveRepositoryKey(repository);
		if (key == null) {
			return false;
		}
		if (!key.repoName().equalsIgnoreCase(repoName)) {
			return false;
		}
		if (namespace == null || namespace.isBlank()) {
			return true;
		}
		String normalized = namespace.trim();
		return key.namespace().equalsIgnoreCase(normalized)
				|| lastSegment(key.namespace()).equalsIgnoreCase(normalized);
	}

	private String lastSegment(String value) {
		if (!StringUtils.hasText(value)) {
			return "";
		}
		String trimmed = value.replaceAll("/+$", "");
		int index = trimmed.lastIndexOf('/');
		if (index < 0) {
			return trimmed;
		}
		return trimmed.substring(index + 1);
	}

	private RepositoryKey resolveRepositoryKey(RepositorySummary repository) {
		RepositoryKey key = parsePath(repository.clonePath());
		if (key != null) {
			return key;
		}
		return parsePath(repository.path());
	}

	private RepositoryKey parsePath(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		String trimmed = trimSlashes(value);
		if (trimmed.endsWith(".git")) {
			trimmed = trimmed.substring(0, trimmed.length() - 4);
		}
		String[] parts = trimmed.split("/");
		if (parts.length < 2) {
			return null;
		}
		String repoName = parts[parts.length - 1];
		String namespace = String.join("/", java.util.Arrays.copyOf(parts, parts.length - 1));
		return new RepositoryKey(namespace, repoName);
	}

	private String trimSlashes(String value) {
		return value.replaceAll("^/+", "").replaceAll("/+$", "");
	}

	private record RepositoryKey(String namespace, String repoName) {
	}
}
