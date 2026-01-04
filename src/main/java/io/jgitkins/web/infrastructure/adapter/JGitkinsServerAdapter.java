package io.jgitkins.web.infrastructure.adapter;

import io.jgitkins.web.application.dto.BranchSummary;
import io.jgitkins.web.application.dto.CommitSummary;
import io.jgitkins.web.application.dto.OAuthLoginRequest;
import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.RepositoryCreateRequest;
import io.jgitkins.web.application.dto.RepositoryCreateResult;
import io.jgitkins.web.application.dto.RepositoryFileEntry;
import io.jgitkins.web.application.dto.RepositoryOverviewResult;
import io.jgitkins.web.application.dto.RepositorySummary;
import io.jgitkins.web.application.dto.ServerOAuthLoginResult;
import io.jgitkins.web.application.dto.UserCredentialIssueRequest;
import io.jgitkins.web.application.dto.UserCredentialIssueResult;
import io.jgitkins.web.application.dto.UserCredentialSummary;
import io.jgitkins.web.application.port.out.AppTokenIssuePort;
import io.jgitkins.web.application.port.out.OrganizePort;
import io.jgitkins.web.application.port.out.RepositoryPort;
import io.jgitkins.web.application.port.out.UserCredentialPort;
import io.jgitkins.web.infrastructure.client.JGitkinsServerClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JGitkinsServerAdapter implements OrganizePort, RepositoryPort, AppTokenIssuePort, UserCredentialPort {

	private final JGitkinsServerClient serverClient;

	@Override
	public OrganizeFetchResult fetchOrganizes() {
		return serverClient.fetchOrganizes();
	}

	@Override
	public List<RepositorySummary> fetchRepositories() {
		return serverClient.fetchRepositories();
	}

	@Override
	public RepositorySummary fetchRepository(Long repositoryId) {
		return serverClient.fetchRepository(repositoryId);
	}

	@Override
	public RepositoryOverviewResult fetchRepositoryOverview(Long repositoryId, String branch) {
		return serverClient.fetchRepositoryOverview(repositoryId, branch);
	}

	@Override
	public List<CommitSummary> fetchCommits(String namespace, String repoName, String branch) {
		return serverClient.fetchCommits(namespace, repoName, branch);
	}

	@Override
	public List<BranchSummary> fetchBranches(Long repositoryId) {
		return serverClient.fetchBranches(repositoryId);
	}

	@Override
	public List<RepositoryFileEntry> fetchRepositoryFiles(String namespace, String repoName, String branch) {
		return serverClient.fetchRepositoryFiles(namespace, repoName, branch);
	}

	@Override
	public ServerOAuthLoginResult issueOAuthLoginToken(OAuthLoginRequest request) {
		return serverClient.issueOAuthLoginToken(request);
	}

	@Override
	public RepositoryCreateResult createRepository(RepositoryCreateRequest request) {
		return serverClient.createRepository(request);
	}

	@Override
	public List<UserCredentialSummary> fetchPersonalAccessTokens() {
		return serverClient.fetchPersonalAccessTokens();
	}

	@Override
	public UserCredentialIssueResult issuePersonalAccessToken(UserCredentialIssueRequest request) {
		return serverClient.issuePersonalAccessToken(request);
	}

	@Override
	public void revokePersonalAccessToken(Long credentialId) {
		serverClient.revokePersonalAccessToken(credentialId);
	}
}
