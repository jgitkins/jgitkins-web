//package io.jgitkins.web.infrastructure.adapter;
//
//import io.jgitkins.web.application.dto.CommitSummary;
//import io.jgitkins.web.application.dto.OAuthLoginRequest;
//import io.jgitkins.web.application.dto.OrganizeFetchResult;
//import io.jgitkins.web.application.dto.RepositoryCreateRequest;
//import io.jgitkins.web.application.dto.RepositoryCreateResult;
//import io.jgitkins.web.application.dto.RepositorySummary;
//import io.jgitkins.web.application.dto.ServerOAuthLoginResult;
//import io.jgitkins.web.application.port.out.AppTokenIssuePort;
//import io.jgitkins.web.application.port.out.OrganizePort;
//import io.jgitkins.web.application.port.out.RepositoryPort;
//import io.jgitkins.web.infrastructure.client.JGitkinsServerClient;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class JgitkinsAdapter implements OrganizePort, RepositoryPort, AppTokenIssuePort {
//
//	private final JGitkinsServerClient serverClient;
//
//	@Override
//	public OrganizeFetchResult fetchOrganizes() {
//		return serverClient.fetchOrganizes();
//	}
//
//	@Override
//	public List<RepositorySummary> fetchRepositories() {
//		return serverClient.fetchRepositories();
//	}
//
//	@Override
//	public List<CommitSummary> fetchCommits(String namespace, String repoName, String branch) {
//		return serverClient.fetchCommits(namespace, repoName, branch);
//	}
//
//	@Override
//	public ServerOAuthLoginResult issueOAuthLoginToken(OAuthLoginRequest request) {
//		return serverClient.issueOAuthLoginToken(request);
//	}
//
//	@Override
//	public RepositoryCreateResult createRepository(RepositoryCreateRequest request) {
//		return serverClient.createRepository(request);
//	}
//}
