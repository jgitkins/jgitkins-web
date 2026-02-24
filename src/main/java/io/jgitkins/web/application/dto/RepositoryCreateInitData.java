package io.jgitkins.web.application.dto;

import io.jgitkins.web.presentation.support.RepositoryUserProfile;

import java.util.List;

public record RepositoryCreateInitData(
        List<OrganizeSummary> organizes,
        String organizeError,
        RepositoryUserProfile profile,
        String ownerLabel,
        String ownerSlug) {
}
