package io.jgitkins.web.presentation.support;

import io.jgitkins.web.application.dto.RepositoryCreateInitData;
import io.jgitkins.web.application.dto.RepositoryCreateRequest;
import io.jgitkins.web.presentation.dto.RepositoryCreateForm;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

@Component
@RequiredArgsConstructor
public class RepositoryCreateViewSupport {

    private final MessageSource messageSource;

    public void populateModel(Model model,
                              RepositoryCreateInitData initData,
                              RepositoryCreateForm form,
                              String formError) {
        model.addAttribute("form", form);
        model.addAttribute("data", initData);
        model.addAttribute("formError", formError);
    }

    public String validateForm(RepositoryCreateForm form) {
        if (!StringUtils.hasText(form.getRepoName())) {
            return getMessage("validation.repository.name.required");
        }
        String ownerType = normalizeOwnerType(form.getOwnerType());
        if (!StringUtils.hasText(ownerType)) {
            return getMessage("validation.repository.owner.required");
        }
        if ("ORGANIZATION".equals(ownerType) && form.getOrganizeId() == null) {
            return getMessage("validation.repository.organization.required");
        }
        return null;
    }

    public RepositoryCreateRequest toRequest(RepositoryCreateForm form, RepositoryUserProfile profile) {
        String ownerType = normalizeOwnerType(form.getOwnerType());
        Long organizeId = resolveOrganizeId(ownerType, form.getOrganizeId());
        String message = resolveInitialMessage(form);
        String branch = StringUtils.hasText(form.getMainBranch()) ? form.getMainBranch() : "main";
        return new RepositoryCreateRequest(
                form.getRepoName(),
                branch,
                profile.name(),
                profile.email(),
                form.isReadme(),
                message,
                ownerType,
                organizeId,
                form.getVisibility(),
                form.getDescription(),
                null);
    }

    private String normalizeOwnerType(String ownerType) {
        return StringUtils.hasText(ownerType) ? ownerType.trim().toUpperCase() : null;
    }

    private Long resolveOrganizeId(String ownerType, Long organizeId) {
        if (!"ORGANIZATION".equals(ownerType)) {
            return null;
        }
        return organizeId;
    }

    private String resolveInitialMessage(RepositoryCreateForm form) {
        if (!form.isReadme()) {
            return null;
        }
        return StringUtils.hasText(form.getMessage()) ? form.getMessage() : "Initial commit";
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, code, LocaleContextHolder.getLocale());
    }
}
