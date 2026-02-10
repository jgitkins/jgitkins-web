package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.dto.OrganizeSummary;
import io.jgitkins.web.application.dto.RepositorySummary;
import io.jgitkins.web.application.dto.UserSummary;
import io.jgitkins.web.application.model.RepositoryKey;
import io.jgitkins.web.application.port.out.OrganizePort;
import io.jgitkins.web.application.port.out.RepositoryPort;
import io.jgitkins.web.application.port.out.UserPort;
import io.jgitkins.web.infrastructure.util.PathUtils;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ExploreController {

	private final RepositoryPort repositoryPort;
	private final OrganizePort organizePort;
	private final UserPort userPort;

	@GetMapping({"/explore", "/explore/{type}"})
	public String explore(@PathVariable(value = "type", required = false) String type, Model model) {
		String resolved = resolveExploreType(type);
		model.addAttribute("exploreType", resolved);
		if ("repositories".equals(resolved)) {
			model.addAttribute("repositories", loadPublicRepositories());
			return "explore/index";
		}
		if ("organizations".equals(resolved)) {
			model.addAttribute("organizations", organizePort.fetchOrganizes().organizes());
			return "explore/index";
		}
		if ("users".equals(resolved)) {
			model.addAttribute("users", userPort.fetchUsers());
			return "explore/index";
		}
		return "explore/index";
	}

	private String resolveExploreType(String type) {
		if (type == null || type.isBlank()) {
			return "repositories";
		}
		return switch (type) {
			case "repositories", "users", "organizations" -> type;
			default -> "repositories";
		};
	}

	private List<ExploreRepositoryView> loadPublicRepositories() {
		Map<Long, String> organizeNameMap = organizePort.fetchOrganizes()
				.organizes()
				.stream()
				.collect(Collectors.toMap(OrganizeSummary::id, OrganizeSummary::name, (a, b) -> a));

		return repositoryPort.fetchRepositories()
				.stream()
				.filter(this::isPublicRepository)
				.map(repository -> toExploreView(repository, organizeNameMap))
				.toList();
	}

	private boolean isPublicRepository(RepositorySummary summary) {
		return summary != null
				&& summary.visibility() != null
				&& "PUBLIC".equalsIgnoreCase(summary.visibility());
	}

	private ExploreRepositoryView toExploreView(RepositorySummary summary, Map<Long, String> organizeNameMap) {
		RepositoryKey key = resolveRepositoryKey(summary);
		String namespace = resolveNamespace(summary, key, organizeNameMap);
		String repoName = key == null ? summary.name() : key.repoName();
		String description = summary.description();
		String visibility = summary.visibility() == null ? "" : summary.visibility().toUpperCase(Locale.ROOT);
		return new ExploreRepositoryView(namespace, repoName, description, visibility, summary.createdAt());
	}

	private String resolveNamespace(RepositorySummary summary,
								   RepositoryKey key,
								   Map<Long, String> organizeNameMap) {
		if (key != null) {
			String normalized = PathUtils.lastSegment(key.namespace());
			if (!normalized.isBlank()) {
				return normalized;
			}
		}
		if (summary == null || summary.ownerType() == null) {
			return "unknown";
		}
		if ("ORGANIZATION".equalsIgnoreCase(summary.ownerType()) && summary.ownerId() != null) {
			String organizeName = organizeNameMap.get(summary.ownerId());
			if (organizeName != null && !organizeName.isBlank()) {
				return organizeName;
			}
		}
		return "unknown";
	}

	private RepositoryKey resolveRepositoryKey(RepositorySummary repository) {
		if (repository == null) {
			return null;
		}
		return PathUtils.resolveRepositoryKey(repository.clonePath(), repository.path());
	}

	public record ExploreRepositoryView(
			String namespace,
			String repoName,
			String description,
			String visibility,
			java.time.LocalDateTime createdAt
	) {
	}
}
