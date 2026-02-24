package io.jgitkins.web.application.port.in.facade;

import io.jgitkins.web.application.dto.RepositoryCreateInitData;
import io.jgitkins.web.application.dto.RepositoryCreateRequest;
import io.jgitkins.web.application.dto.RepositoryCreateResult;
import io.jgitkins.web.presentation.support.RepositoryUserProfile;

public interface RepositoryCreateFacadeUseCase {

    RepositoryCreateInitData getInitData(RepositoryUserProfile profile, String ownerType, Long organizeId);

    RepositoryCreateResult createRepository(RepositoryCreateRequest request);
}
