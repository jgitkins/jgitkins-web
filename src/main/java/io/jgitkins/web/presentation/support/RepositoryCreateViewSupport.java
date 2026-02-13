package io.jgitkins.web.presentation.support;

import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.OrganizeSummary;
import io.jgitkins.web.application.dto.RepositoryCreateRequest;
import io.jgitkins.web.presentation.dto.RepositoryCreateForm;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

@Component
public class RepositoryCreateViewSupport {

	public void populateCreateModel(Model model,
								 RepositoryCreateForm form,
								 RepositoryUserProfile profile,
								 OrganizeFetchResult organizeResult,
								 String formError) {
		String ownerSlug = resolveOwnerSlug(form, profile, organizeResult.organizes());
		model.addAttribute("form", form);
		model.addAttribute("organizes", organizeResult.organizes());
		model.addAttribute("organizeError", organizeResult.errorMessage());
		model.addAttribute("ownerLabel", resolveOwnerLabel(profile));
		model.addAttribute("ownerSlug", ownerSlug);
		model.addAttribute("formError", formError);
	}

	public String validateForm(RepositoryCreateForm form) {
		if (!StringUtils.hasText(form.getRepoName())) {
			return "Repository name is required.";
		}
		String ownerType = normalizeOwnerType(form.getOwnerType());
		if (!StringUtils.hasText(ownerType)) {
			return "Owner selection is required.";
		}
		if ("ORGANIZATION".equals(ownerType) && form.getOrganizeId() == null) {
			return "Organization selection is required.";
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
				null
		);
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

	private String resolveOwnerLabel(RepositoryUserProfile profile) {
		if (StringUtils.hasText(profile.name()) && StringUtils.hasText(profile.email())) {
			return profile.name() + " (" + profile.email() + ")";
		}
		if (StringUtils.hasText(profile.name())) {
			return profile.name();
		}
		return "Personal";
	}

	private String resolveOwnerSlug(RepositoryCreateForm form,
								 RepositoryUserProfile profile,
								 List<OrganizeSummary> organizes) {
		String ownerType = normalizeOwnerType(form.getOwnerType());
		if ("ORGANIZATION".equals(ownerType) && form.getOrganizeId() != null) {
			for (OrganizeSummary organize : organizes) {
				if (organize != null && form.getOrganizeId().equals(organize.id())) {
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
