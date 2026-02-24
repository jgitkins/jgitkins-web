package io.jgitkins.web.presentation.support;

import io.jgitkins.web.application.dto.RepositoryCreateContext;
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
			RepositoryCreateContext context,
			RepositoryCreateForm form,
			String formError) {
		model.addAttribute("form", form);
		model.addAttribute("data", context);
		model.addAttribute("formError", formError);
	}

	public String validateForm(RepositoryCreateForm form) {
		if (!StringUtils.hasText(form.getRepoName())) {
			return getMessage("validation.repo.name.required");
		}
		if (!form.getRepoName().matches("^[a-zA-Z0-9._-]+$")) {
			return getMessage("validation.repo.name.pattern");
		}
		return null;
	}

	public RepositoryCreateRequest toRequest(RepositoryCreateForm form, RepositoryUserProfile profile) {
		return new RepositoryCreateRequest(
				form.getRepoName(),
				form.getMainBranch(),
				profile.name(),
				profile.email(),
				form.isReadme(),
				form.getMessage(),
				form.getOwnerType(),
				form.getOrganizeId(),
				form.getVisibility(),
				form.getDescription(),
				null // contextPath
		);
	}

	private String getMessage(String code) {
		return messageSource.getMessage(code, null, code, LocaleContextHolder.getLocale());
	}
}
