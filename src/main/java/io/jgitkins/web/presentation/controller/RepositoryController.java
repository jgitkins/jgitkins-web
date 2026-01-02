package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.OrganizeSummary;
import io.jgitkins.web.application.dto.RepositoryCreateRequest;
import io.jgitkins.web.application.dto.RepositoryCreateResult;
import io.jgitkins.web.application.port.in.RepositoryCreateUseCase;
import io.jgitkins.web.presentation.dto.RepositoryCreateForm;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RepositoryController {

	private final RepositoryCreateUseCase repositoryCreateUseCase;

	@GetMapping("/repositories/new")
	public String newRepository(Authentication authentication, Model model) {
		RepositoryCreateForm form = new RepositoryCreateForm();
		UserProfile profile = resolveUserProfile(authentication);
		OrganizeFetchResult organizeResult = repositoryCreateUseCase.loadOwnerOptions();
		populateCreateModel(model, form, profile, organizeResult, null);
		return "repositories/create";
	}

	@PostMapping("/repositories")
	public String createRepository(@ModelAttribute("form") RepositoryCreateForm form,
								 Authentication authentication,
								 Model model) {
		UserProfile profile = resolveUserProfile(authentication);
		OrganizeFetchResult organizeResult = repositoryCreateUseCase.loadOwnerOptions();

		String validationError = validateForm(form);
		if (validationError != null) {
			populateCreateModel(model, form, profile, organizeResult, validationError);
			return "repositories/create";
		}

		String ownerType = normalizeOwnerType(form.getOwnerType());
		Long organizeId = resolveOrganizeId(ownerType, form.getOrganizeId());
		String message = resolveInitialMessage(form);
		String branch = StringUtils.hasText(form.getMainBranch()) ? form.getMainBranch() : "main";

		RepositoryCreateRequest request = new RepositoryCreateRequest(
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

		RepositoryCreateResult result = repositoryCreateUseCase.createRepository(request);
		if (result.errorMessage() != null) {
			populateCreateModel(model, form, profile, organizeResult, result.errorMessage());
			return "repositories/create";
		}

		return "redirect:/dashboard";
	}

	private void populateCreateModel(Model model,
							 RepositoryCreateForm form,
							 UserProfile profile,
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

	private String validateForm(RepositoryCreateForm form) {
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

	private UserProfile resolveUserProfile(Authentication authentication) {
		if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
			Object principal = oauthToken.getPrincipal();
			if (principal instanceof OidcUser oidcUser) {
				String name = StringUtils.hasText(oidcUser.getFullName()) ? oidcUser.getFullName() : oidcUser.getName();
				return new UserProfile(name, oidcUser.getEmail());
			}
			if (principal instanceof OAuth2User oauth2User) {
				String name = oauth2User.getAttribute("name");
				String email = oauth2User.getAttribute("email");
				String fallbackName = StringUtils.hasText(name) ? name : oauth2User.getName();
				return new UserProfile(fallbackName, email);
			}
		}
		return new UserProfile("Personal", null);
	}

	private String resolveOwnerLabel(UserProfile profile) {
		if (StringUtils.hasText(profile.name()) && StringUtils.hasText(profile.email())) {
			return profile.name() + " (" + profile.email() + ")";
		}
		if (StringUtils.hasText(profile.name())) {
			return profile.name();
		}
		return "Personal";
	}

	private String resolveOwnerSlug(RepositoryCreateForm form, UserProfile profile, List<OrganizeSummary> organizes) {
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

	private String resolveUserSlug(UserProfile profile) {
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

	private record UserProfile(String name, String email) {
	}
}
