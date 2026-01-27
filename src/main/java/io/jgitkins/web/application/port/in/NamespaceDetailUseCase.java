package io.jgitkins.web.application.port.in;

import io.jgitkins.web.application.dto.NamespaceDetailResult;

public interface NamespaceDetailUseCase {

	NamespaceDetailResult loadNamespaceDetail(String namespace);
}
