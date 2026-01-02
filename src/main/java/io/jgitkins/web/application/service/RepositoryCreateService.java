package io.jgitkins.web.application.service;

import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.RepositoryCreateRequest;
import io.jgitkins.web.application.dto.RepositoryCreateResult;
import io.jgitkins.web.application.port.in.RepositoryCreateUseCase;
import io.jgitkins.web.application.port.out.JgitkinsServerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepositoryCreateService implements RepositoryCreateUseCase {

	private final JgitkinsServerPort serverPort;

	@Override
	public OrganizeFetchResult loadOwnerOptions() {
		return serverPort.fetchOrganizes();
	}

	@Override
	public RepositoryCreateResult createRepository(RepositoryCreateRequest request) {
		return serverPort.createRepository(request);
	}
}
