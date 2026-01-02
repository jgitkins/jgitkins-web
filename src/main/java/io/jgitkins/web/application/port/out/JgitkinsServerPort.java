package io.jgitkins.web.application.port.out;

import io.jgitkins.web.application.dto.CommitSummary;
import io.jgitkins.web.application.dto.OAuthLoginRequest;
import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.RepositoryCreateRequest;
import io.jgitkins.web.application.dto.RepositoryCreateResult;
import io.jgitkins.web.application.dto.RepositorySummary;
import io.jgitkins.web.application.dto.ServerOAuthLoginResult;

import java.util.List;

public interface JgitkinsServerPort {

	OrganizeFetchResult fetchOrganizes();

	List<RepositorySummary> fetchRepositories();

	List<CommitSummary> fetchCommits(String namespace, String repoName, String branch);

	ServerOAuthLoginResult issueOAuthLoginToken(OAuthLoginRequest request);

	RepositoryCreateResult createRepository(RepositoryCreateRequest request);
}
