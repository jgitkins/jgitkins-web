package io.jgitkins.web.application.port.in;

import io.jgitkins.web.application.dto.RepositoryDetailData;

public interface RepositoryDetailUseCase {

	RepositoryDetailData loadRepositoryDetail(Long repositoryId, String branch);

	RepositoryDetailData loadRepositoryDetailByPath(String namespace, String repoName, String branch);
}
