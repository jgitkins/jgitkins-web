package io.jgitkins.web.application.service;

import io.jgitkins.web.application.dto.NamespaceDetailResult;
import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.OrganizeMemberSummary;
import io.jgitkins.web.application.dto.OrganizeSummary;
import io.jgitkins.web.application.dto.RepositorySummary;
import io.jgitkins.web.application.port.in.NamespaceDetailUseCase;
import io.jgitkins.web.application.port.out.OrganizePort;
import io.jgitkins.web.application.port.out.RepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class NamespaceDetailService implements NamespaceDetailUseCase {

	private final OrganizePort organizePort;
	private final RepositoryPort repositoryPort;

	@Override
	public NamespaceDetailResult loadNamespaceDetail(String namespace) {
		if (!StringUtils.hasText(namespace)) {
			return new NamespaceDetailResult(false, null, null, List.of(), List.of(), "이름이 필요합니다.");
		}

		OrganizeFetchResult organizeResult = organizePort.fetchOrganizes();
		if (organizeResult.errorMessage() != null) {
			return new NamespaceDetailResult(false, namespace, null, List.of(), List.of(), organizeResult.errorMessage());
		}

		List<RepositorySummary> repositories = repositoryPort.fetchRepositories();
		Optional<OrganizeSummary> organize = organizeResult.organizes().stream()
				.filter(item -> item != null && namespace.equalsIgnoreCase(item.name()))
				.findFirst();
		if (organize.isPresent()) {
			List<RepositorySummary> organizeRepos = repositories.stream()
					.filter(repo -> repo != null)
					.filter(repo -> "ORGANIZATION".equalsIgnoreCase(repo.ownerType()))
					.filter(repo -> repo.ownerId() != null && repo.ownerId().equals(organize.get().id()))
					.collect(Collectors.toList());
			List<OrganizeMemberSummary> members = organizePort.fetchOrganizeMembers(organize.get().id());
			return new NamespaceDetailResult(
					true,
					organize.get().name(),
					organize.get().description(),
					organizeRepos,
					members,
					null
			);
		}

		List<RepositorySummary> userRepos = repositories.stream()
				.filter(repo -> repo != null)
				.filter(repo -> namespace.equalsIgnoreCase(resolveNamespaceSlug(resolveNamespace(repo))))
				.collect(Collectors.toList());

		return new NamespaceDetailResult(
				false,
				namespace,
				"Personal namespace",
				userRepos,
				List.of(),
				null
		);
	}

	private String resolveNamespace(RepositorySummary repository) {
		if (repository == null) {
			return null;
		}
		String clonePath = repository.clonePath();
		if (StringUtils.hasText(clonePath)) {
			String trimmed = trimSlashes(clonePath);
			if (trimmed.endsWith(".git")) {
				trimmed = trimmed.substring(0, trimmed.length() - 4);
			}
			String[] parts = trimmed.split("/");
			if (parts.length >= 2) {
				return String.join("/", java.util.Arrays.copyOf(parts, parts.length - 1));
			}
		}
		if (StringUtils.hasText(repository.path())) {
			String[] parts = repository.path().split("/");
			if (parts.length >= 2) {
				return String.join("/", java.util.Arrays.copyOf(parts, parts.length - 1));
			}
		}
		return null;
	}

	private String resolveNamespaceSlug(String namespace) {
		if (!StringUtils.hasText(namespace)) {
			return null;
		}
		String trimmed = namespace.replaceAll("/+$", "");
		int index = trimmed.lastIndexOf('/');
		if (index < 0) {
			return trimmed;
		}
		return trimmed.substring(index + 1);
	}

	private String trimSlashes(String value) {
		if (!StringUtils.hasText(value)) {
			return "";
		}
		return value.replaceAll("^/+", "").replaceAll("/+$", "");
	}
}
