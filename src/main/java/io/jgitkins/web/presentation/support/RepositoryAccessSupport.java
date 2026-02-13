package io.jgitkins.web.presentation.support;

import io.jgitkins.web.application.dto.RepositoryDetailData;
import io.jgitkins.web.application.dto.RepositorySummary;
import org.springframework.stereotype.Component;

@Component
public class RepositoryAccessSupport {

	public boolean requiresNotFoundForUnauthenticatedPrivate(RepositoryDetailData detail, boolean authenticated) {
		return detail != null
				&& detail.repository() != null
				&& !isPublicRepository(detail.repository())
				&& !authenticated;
	}

	private boolean isPublicRepository(RepositorySummary repository) {
		return repository.visibility() != null
				&& "PUBLIC".equalsIgnoreCase(repository.visibility());
	}
}
