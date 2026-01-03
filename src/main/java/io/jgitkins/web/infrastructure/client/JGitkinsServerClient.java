package io.jgitkins.web.infrastructure.client;

import io.jgitkins.web.presentation.common.ApiResponse;
import io.jgitkins.web.application.dto.BranchSummary;
import io.jgitkins.web.application.dto.CommitSummary;
import io.jgitkins.web.application.dto.OAuthLoginRequest;
import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.OrganizeSummary;
import io.jgitkins.web.application.dto.RepositoryCreateRequest;
import io.jgitkins.web.application.dto.RepositoryCreateResult;
import io.jgitkins.web.application.dto.RepositoryFileEntry;
import io.jgitkins.web.application.dto.RepositoryOverviewResult;
import io.jgitkins.web.application.dto.RepositorySummary;
import io.jgitkins.web.application.dto.ServerOAuthLoginResult;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JGitkinsServerClient {

	private static final ParameterizedTypeReference<ApiResponse<List<OrganizeSummary>>> ORGANIZE_LIST_TYPE =
			new ParameterizedTypeReference<>() {
			};
	private static final ParameterizedTypeReference<ApiResponse<List<RepositorySummary>>> REPOSITORY_LIST_TYPE =
			new ParameterizedTypeReference<>() {
			};
	private static final ParameterizedTypeReference<ApiResponse<RepositorySummary>> REPOSITORY_TYPE =
			new ParameterizedTypeReference<>() {
			};
	private static final ParameterizedTypeReference<ApiResponse<List<CommitSummary>>> COMMIT_LIST_TYPE =
			new ParameterizedTypeReference<>() {
			};
	private static final ParameterizedTypeReference<ApiResponse<List<BranchSummary>>> BRANCH_LIST_TYPE =
			new ParameterizedTypeReference<>() {
			};
	private static final ParameterizedTypeReference<ApiResponse<List<RepositoryFileEntry>>> FILE_LIST_TYPE =
			new ParameterizedTypeReference<>() {
			};
	private static final ParameterizedTypeReference<ApiResponse<RepositoryOverviewResult>> OVERVIEW_TYPE =
			new ParameterizedTypeReference<>() {
			};
	private static final ParameterizedTypeReference<ApiResponse<ServerOAuthLoginResult>> OAUTH_LOGIN_TYPE =
			new ParameterizedTypeReference<>() {
			};
	private static final ParameterizedTypeReference<ApiResponse<RepositorySummary>> REPOSITORY_CREATE_TYPE =
			new ParameterizedTypeReference<>() {
			};

	private final RestClient restClient;

	public OrganizeFetchResult fetchOrganizes() {
		try {
			ApiResponse<List<OrganizeSummary>> response = restClient.get()
					.uri("/api/organizes")
					.retrieve()
					.body(ORGANIZE_LIST_TYPE);
			if (response == null) {
				return new OrganizeFetchResult(List.of(), "API 응답이 비어 있습니다.");
			}
			if (response.error() != null) {
				return new OrganizeFetchResult(List.of(), response.error().message());
			}
			List<OrganizeSummary> organizes = response.data() == null ? List.of() : response.data();
			return new OrganizeFetchResult(organizes, null);
		} catch (RestClientException ex) {
			return new OrganizeFetchResult(List.of(), "API 서버에 연결할 수 없습니다.");
		}
	}

	public List<RepositorySummary> fetchRepositories() {
		try {
			ApiResponse<List<RepositorySummary>> response = restClient.get()
					.uri("/api/repositories")
					.retrieve()
					.body(REPOSITORY_LIST_TYPE);
			if (response == null || response.error() != null || response.data() == null) {
				return List.of();
			}
			return response.data();
		} catch (RestClientException ex) {
			return List.of();
		}
	}

	public RepositorySummary fetchRepository(Long repositoryId) {
		try {
			ApiResponse<RepositorySummary> response = restClient.get()
					.uri("/api/repositories/{repositoryId}", repositoryId)
					.retrieve()
					.body(REPOSITORY_TYPE);
			if (response == null || response.error() != null) {
				return null;
			}
			return response.data();
		} catch (RestClientException ex) {
			return null;
		}
	}

	public RepositoryOverviewResult fetchRepositoryOverview(Long repositoryId, String branch) {
		try {
			ApiResponse<RepositoryOverviewResult> response = restClient.get()
					.uri(uriBuilder -> uriBuilder
							.path("/api/repositories/{repositoryId}/overview")
							.queryParamIfPresent("branch", branch == null || branch.isBlank()
									? java.util.Optional.empty()
									: java.util.Optional.of(branch))
							.build(repositoryId))
					.retrieve()
					.body(OVERVIEW_TYPE);
			if (response == null || response.error() != null) {
				return null;
			}
			return response.data();
		} catch (RestClientException ex) {
			return null;
		}
	}

	public List<CommitSummary> fetchCommits(String namespace, String repoName, String branch) {
		try {
			ApiResponse<List<CommitSummary>> response = restClient.get()
					.uri("/repositories/{namespace}/{repo}/branches/{branch}/commits", namespace, repoName, branch)
					.retrieve()
					.body(COMMIT_LIST_TYPE);
			if (response == null || response.error() != null || response.data() == null) {
				return List.of();
			}
			return response.data();
		} catch (RestClientException ex) {
			return List.of();
		}
	}

	public List<BranchSummary> fetchBranches(Long repositoryId) {
		try {
			ApiResponse<List<BranchSummary>> response = restClient.get()
					.uri("/api/repositories/{repositoryId}/branches", repositoryId)
					.retrieve()
					.body(BRANCH_LIST_TYPE);
			if (response == null || response.error() != null || response.data() == null) {
				return List.of();
			}
			return response.data();
		} catch (RestClientException ex) {
			return List.of();
		}
	}

	public List<RepositoryFileEntry> fetchRepositoryFiles(String namespace, String repoName, String branch) {
		try {
			ApiResponse<List<RepositoryFileEntry>> response = restClient.get()
					.uri("/repositories/{namespace}/{repo}/files?ref={branch}", namespace, repoName, branch)
					.retrieve()
					.body(FILE_LIST_TYPE);
			if (response == null || response.error() != null || response.data() == null) {
				return List.of();
			}
			return response.data();
		} catch (RestClientException ex) {
			return List.of();
		}
	}

	public ServerOAuthLoginResult issueOAuthLoginToken(OAuthLoginRequest request) {
		ApiResponse<ServerOAuthLoginResult> response = restClient.post()
				.uri("/api/auth/oauth/login")
				.body(request)
				.retrieve()
				.body(OAUTH_LOGIN_TYPE);
		if (response == null || response.error() != null || response.data() == null) {
			throw new RestClientException("OAuth login failed");
		}
		return response.data();
	}

	public RepositoryCreateResult createRepository(RepositoryCreateRequest request) {
		try {
			ApiResponse<RepositorySummary> response = restClient.post()
					.uri("/api/repositories")
					.body(request)
					.retrieve()
					.body(REPOSITORY_CREATE_TYPE);
			if (response == null) {
				return new RepositoryCreateResult(null, "API 응답이 비어 있습니다.");
			}
			if (response.error() != null) {
				return new RepositoryCreateResult(null, response.error().message());
			}
			if (response.data() == null) {
				return new RepositoryCreateResult(null, "저장소 생성 응답이 비어 있습니다.");
			}
			return new RepositoryCreateResult(response.data(), null);
		} catch (RestClientException ex) {
			return new RepositoryCreateResult(null, "API 서버에 연결할 수 없습니다.");
		}
	}
}
