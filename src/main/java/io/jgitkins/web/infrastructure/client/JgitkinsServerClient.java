package io.jgitkins.web.infrastructure.client;

import io.jgitkins.web.presentation.common.ApiResponse;
import io.jgitkins.web.application.dto.CommitSummary;
import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.OrganizeSummary;
import io.jgitkins.web.application.dto.RepositorySummary;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JgitkinsServerClient {

	private static final ParameterizedTypeReference<ApiResponse<List<OrganizeSummary>>> ORGANIZE_LIST_TYPE =
			new ParameterizedTypeReference<>() {
			};
	private static final ParameterizedTypeReference<ApiResponse<List<RepositorySummary>>> REPOSITORY_LIST_TYPE =
			new ParameterizedTypeReference<>() {
			};
	private static final ParameterizedTypeReference<ApiResponse<List<CommitSummary>>> COMMIT_LIST_TYPE =
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
}
