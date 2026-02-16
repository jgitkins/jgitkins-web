package io.jgitkins.web.application.port.in;

import io.jgitkins.web.application.dto.RepositoryDetailData;
import io.jgitkins.web.application.dto.RepositoryFileEntry;
import java.util.List;

public interface RepositoryDetailUseCase {

	RepositoryDetailData loadRepositoryDetail(Long repositoryId, String branch);

	RepositoryDetailData loadRepositoryDetailByPath(String namespace, String repoName, String branch);

	RepositoryDetailData loadRepositoryTreeByPath(String namespace, String repoName, String branch, String directory);

	List<RepositoryFileEntry> searchRepositoryFilesByPath(String namespace, String repoName, String branch, String query, int limit);
}
