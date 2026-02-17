package io.jgitkins.web.application.service;

import io.jgitkins.web.application.dto.BranchSummary;
import io.jgitkins.web.application.dto.CommitSummary;
import io.jgitkins.web.application.dto.RepositoryDetailData;
import io.jgitkins.web.application.dto.RepositoryFileEntry;
import io.jgitkins.web.application.dto.RepositoryFileIndexEntry;
import io.jgitkins.web.application.dto.RepositoryOverviewResult;
import io.jgitkins.web.application.dto.RepositorySummary;
import io.jgitkins.web.application.model.RepositoryKey;
import io.jgitkins.web.application.port.in.RepositoryDetailUseCase;
import io.jgitkins.web.application.port.out.RepositoryPort;
import io.jgitkins.web.infrastructure.util.PathUtils;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RepositoryDetailService implements RepositoryDetailUseCase {

	private static final Duration TREE_CACHE_TTL = Duration.ofMinutes(5);
	private static final Duration FILE_INDEX_CACHE_TTL = Duration.ofMinutes(5);

	private final RepositoryPort repositoryPort;
	private final RepositoryTreeCacheSupport repositoryTreeCacheSupport;
	private final RepositoryFileIndexCacheSupport repositoryFileIndexCacheSupport;

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

	@Override
	public RepositoryDetailData loadRepositoryTreeByPath(String namespace, String repoName, String branch, String directory) {
		RepositorySummary repository = repositoryPort.fetchRepositories().stream()
				.filter(item -> matchesRepository(item, namespace, repoName))
				.findFirst()
				.orElse(null);
		if (repository == null || repository.id() == null) {
			return new RepositoryDetailData(null, List.of(), List.of(), null, null, null, null,
					"Repository not found.");
		}
		RepositoryOverviewResult overview = repositoryPort.fetchRepositoryOverview(repository.id(), branch);
		RepositoryDetailData baseDetail = buildDetail(overview);
		if (baseDetail.repository() == null) {
			return baseDetail;
		}
		String selectedBranch = baseDetail.selectedBranch();
		String normalizedDirectory = StringUtils.hasText(directory) ? directory.trim() : "";
		String headCommit = resolveHeadCommit(namespace, repoName, selectedBranch);
		List<RepositoryFileEntry> files = repositoryTreeCacheSupport
				.get(namespace, repoName, selectedBranch, normalizedDirectory, headCommit)
				.orElseGet(() -> {
					List<RepositoryFileEntry> loaded = repositoryPort.fetchRepositoryTree(namespace, repoName, selectedBranch, normalizedDirectory);
					repositoryTreeCacheSupport.put(namespace, repoName, selectedBranch, normalizedDirectory, headCommit, loaded, TREE_CACHE_TTL);
					return loaded;
				});
		return new RepositoryDetailData(
				baseDetail.repository(),
				baseDetail.branches(),
				files,
				baseDetail.namespace(),
				baseDetail.ownerSlug(),
				baseDetail.repoName(),
				selectedBranch,
				baseDetail.errorMessage()
		);
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

		String ownerSlug = PathUtils.lastSegment(key.namespace());
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

	@Override
	public List<RepositoryFileIndexEntry> searchRepositoryFilesByPath(String namespace, String repoName, String branch, String query, int limit) {
		String selectedBranch = StringUtils.hasText(branch) ? branch.trim() : "main";
		String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
		int safeLimit = Math.max(1, Math.min(limit, 100));
		String headCommit = resolveHeadCommit(namespace, repoName, selectedBranch);

		List<RepositoryFileIndexEntry> index = repositoryFileIndexCacheSupport
				.get(namespace, repoName, selectedBranch, headCommit)
				.orElseGet(() -> {
					List<RepositoryFileIndexEntry> loaded = repositoryPort.fetchRepositoryFileIndex(namespace, repoName, selectedBranch);
					repositoryFileIndexCacheSupport.put(namespace, repoName, selectedBranch, headCommit, loaded, FILE_INDEX_CACHE_TTL);
					return loaded;
				});

		return index.stream()
				.filter(entry -> entry != null && entry.path() != null)
				.filter(entry -> "tree".equalsIgnoreCase(entry.type()) || "blob".equalsIgnoreCase(entry.type()) || entry.type() == null)
				.filter(entry -> {
					if (!StringUtils.hasText(normalizedQuery)) {
						return true;
					}
					String path = entry.path() == null ? "" : entry.path().toLowerCase();
					String name = entry.name() == null ? "" : entry.name().toLowerCase();
					return path.contains(normalizedQuery) || name.contains(normalizedQuery);
				})
				.sorted(Comparator.comparing(RepositoryFileIndexEntry::path, Comparator.nullsLast(String::compareToIgnoreCase)))
				.limit(safeLimit)
				.toList();
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
				|| PathUtils.lastSegment(key.namespace()).equalsIgnoreCase(normalized);
	}

	private String resolveHeadCommit(String namespace, String repoName, String selectedBranch) {
		if (!StringUtils.hasText(selectedBranch)) {
			return "no-branch";
		}
		return repositoryPort.fetchCommits(namespace, repoName, selectedBranch).stream()
				.findFirst()
				.map(CommitSummary::id)
				.orElse("no-head");
	}

	private RepositoryKey resolveRepositoryKey(RepositorySummary repository) {
		if (repository == null) {
			return null;
		}
		return PathUtils.resolveRepositoryKey(repository.clonePath(), repository.path());
	}
}
