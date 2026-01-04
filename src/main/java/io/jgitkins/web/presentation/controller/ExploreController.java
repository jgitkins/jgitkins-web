package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.dto.RepositorySummary;
import io.jgitkins.web.application.dto.OrganizeSummary;
import io.jgitkins.web.application.port.out.OrganizePort;
import io.jgitkins.web.application.port.out.RepositoryPort;
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

	@GetMapping({"/explore", "/explore/{type}"})
	public String explore(@PathVariable(value = "type", required = false) String type, Model model) {
		String resolved = resolveExploreType(type);
		model.addAttribute("exploreType", resolved);
		if ("repositories".equals(resolved)) {
			model.addAttribute("repositories", loadPublicRepositories());
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
		return new ExploreRepositoryView(namespace, repoName, description, visibility);
	}

	private String resolveNamespace(RepositorySummary summary,
								   RepositoryKey key,
								   Map<Long, String> organizeNameMap) {
		if (key != null) {
			String normalized = lastSegment(key.namespace());
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
		RepositoryKey key = parsePath(repository.clonePath());
		if (key != null) {
			return key;
		}
		return parsePath(repository.path());
	}

	private RepositoryKey parsePath(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		String trimmed = value.replaceAll("^/+", "").replaceAll("/+$", "");
		if (trimmed.endsWith(".git")) {
			trimmed = trimmed.substring(0, trimmed.length() - 4);
		}
		String[] parts = trimmed.split("/");
		if (parts.length < 2) {
			return null;
		}
		String repoName = parts[parts.length - 1];
		String namespace = String.join("/", java.util.Arrays.copyOf(parts, parts.length - 1));
		return new RepositoryKey(namespace, repoName);
	}

	private String lastSegment(String value) {
		if (value == null || value.isBlank()) {
			return "";
		}
		String trimmed = value.replaceAll("/+$", "");
		int index = trimmed.lastIndexOf('/');
		if (index < 0) {
			return trimmed;
		}
		return trimmed.substring(index + 1);
	}

	private record RepositoryKey(String namespace, String repoName) {
	}

	public record ExploreRepositoryView(
			String namespace,
			String repoName,
			String description,
			String visibility
	) {
	}
}
