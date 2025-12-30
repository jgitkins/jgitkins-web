package io.jgitkins.web.application.port.out;

import io.jgitkins.web.application.dto.CommitSummary;
import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.RepositorySummary;

import java.util.List;

public interface JgitkinsServerPort {

	OrganizeFetchResult fetchOrganizes();

	List<RepositorySummary> fetchRepositories();

	List<CommitSummary> fetchCommits(String namespace, String repoName, String branch);
}
