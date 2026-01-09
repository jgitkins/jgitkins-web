package io.jgitkins.web.application.port.out;

import io.jgitkins.web.application.dto.*;

import java.util.List;

public interface RepositoryPort {

	List<RepositorySummary> fetchRepositories();

	RepositorySummary fetchRepository(Long repositoryId);

	RepositoryOverviewResult fetchRepositoryOverview(Long repositoryId, String branch);

	List<CommitSummary> fetchCommits(String namespace, String repoName, String branch);

	List<BranchSummary> fetchBranches(Long repositoryId);

	List<RepositoryFileEntry> fetchRepositoryFiles(String namespace, String repoName, String branch);

	RepositoryCreateResult createRepository(RepositoryCreateRequest request);
}
