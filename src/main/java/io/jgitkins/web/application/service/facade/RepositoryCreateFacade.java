package io.jgitkins.web.application.service.facade;

import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.RepositoryCreateContext;
import io.jgitkins.web.application.dto.RepositoryCreateRequest;
import io.jgitkins.web.application.dto.RepositoryCreateResult;
import io.jgitkins.web.application.dto.OrganizeSummary;
import io.jgitkins.web.application.port.in.RepositoryCreateUseCase;
import io.jgitkins.web.application.port.in.facade.RepositoryCreateFacadeUseCase;
import io.jgitkins.web.presentation.support.RepositoryUserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepositoryCreateFacade implements RepositoryCreateFacadeUseCase {

    private final RepositoryCreateUseCase repositoryCreateUseCase;

    @Override
    public RepositoryCreateContext getInitData(RepositoryUserProfile profile, String ownerType, Long organizeId) {
        OrganizeFetchResult organizeResult = repositoryCreateUseCase.loadOwnerOptions();
        List<OrganizeSummary> organizes = organizeResult.organizes();

        String ownerLabel = resolveOwnerLabel(profile);
        String ownerSlug = resolveOwnerSlug(ownerType, organizeId, profile, organizes);

        return new RepositoryCreateContext(
                organizes,
                organizeResult.errorMessage(),
                profile,
                ownerLabel,
                ownerSlug);
    }

    @Override
    public RepositoryCreateResult createRepository(RepositoryCreateRequest request) {
        return repositoryCreateUseCase.createRepository(request);
    }

    private String resolveOwnerLabel(RepositoryUserProfile profile) {
        if (StringUtils.hasText(profile.name()) && StringUtils.hasText(profile.email())) {
            return profile.name() + " (" + profile.email() + ")";
        }
        if (StringUtils.hasText(profile.name())) {
            return profile.name();
        }
        return "Personal";
    }

    private String resolveOwnerSlug(String ownerType, Long organizeId, RepositoryUserProfile profile,
            List<OrganizeSummary> organizes) {
        String normalizedOwnerType = StringUtils.hasText(ownerType) ? ownerType.trim().toUpperCase() : null;
        if ("ORGANIZATION".equals(normalizedOwnerType) && organizeId != null) {
            for (OrganizeSummary organize : organizes) {
                if (organize != null && organizeId.equals(organize.id())) {
                    return slugify(organize.name());
                }
            }
        }
        return resolveUserSlug(profile);
    }

    private String resolveUserSlug(RepositoryUserProfile profile) {
        if (StringUtils.hasText(profile.email()) && profile.email().contains("@")) {
            return profile.email().substring(0, profile.email().indexOf('@'));
        }
        if (StringUtils.hasText(profile.name())) {
            return slugify(profile.name());
        }
        return "me";
    }

    private String slugify(String value) {
        if (!StringUtils.hasText(value)) {
            return "me";
        }
        return value.trim().replaceAll("\\s+", "-").toLowerCase();
    }
}
