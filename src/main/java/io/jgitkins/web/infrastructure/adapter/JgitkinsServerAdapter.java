package io.jgitkins.web.infrastructure.adapter;

import io.jgitkins.web.application.dto.CommitSummary;
import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.RepositorySummary;
import io.jgitkins.web.application.port.out.JgitkinsServerPort;
import io.jgitkins.web.infrastructure.client.JgitkinsServerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JgitkinsServerAdapter implements JgitkinsServerPort {

	private final JgitkinsServerClient serverClient;

	@Override
	public OrganizeFetchResult fetchOrganizes() {
		return serverClient.fetchOrganizes();
	}

	@Override
	public List<RepositorySummary> fetchRepositories() {
		return serverClient.fetchRepositories();
	}

	@Override
	public List<CommitSummary> fetchCommits(String namespace, String repoName, String branch) {
		return serverClient.fetchCommits(namespace, repoName, branch);
	}
}
