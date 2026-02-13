package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.RepositoryCreateResult;
import io.jgitkins.web.application.dto.RepositoryDetailData;
import io.jgitkins.web.application.port.in.RepositoryCreateUseCase;
import io.jgitkins.web.application.port.in.RepositoryDetailUseCase;
import io.jgitkins.web.presentation.dto.RepositoryCreateForm;
import io.jgitkins.web.presentation.support.RepositoryAccessSupport;
import io.jgitkins.web.presentation.support.RepositoryCreateViewSupport;
import io.jgitkins.web.presentation.support.RepositoryTreePathSupport;
import io.jgitkins.web.presentation.support.RepositoryUserProfile;
import io.jgitkins.web.presentation.support.RepositoryUserProfileResolver;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class RepositoryController {

	private final RepositoryCreateUseCase repositoryCreateUseCase;
	private final RepositoryDetailUseCase repositoryDetailUseCase;
	private final RepositoryUserProfileResolver userProfileResolver;
	private final RepositoryCreateViewSupport createViewSupport;
	private final RepositoryTreePathSupport treePathSupport;
	private final RepositoryAccessSupport accessSupport;
	private final MessageSource messageSource;

	@GetMapping("/repositories/new")
	public String newRepository(Authentication authentication, Model model) {
		RepositoryCreateForm form = new RepositoryCreateForm();
		RepositoryUserProfile profile = userProfileResolver.resolve(authentication);
		OrganizeFetchResult organizeResult = repositoryCreateUseCase.loadOwnerOptions();
		createViewSupport.populateCreateModel(model, form, profile, organizeResult, null);
		return "repositories/new";
	}

	@PostMapping("/repositories")
	public String createRepository(@Valid @ModelAttribute("form") RepositoryCreateForm form,
								 BindingResult bindingResult,
								 Authentication authentication,
								 Model model) {
		RepositoryUserProfile profile = userProfileResolver.resolve(authentication);
		OrganizeFetchResult organizeResult = repositoryCreateUseCase.loadOwnerOptions();

		String validationError = resolveValidationError(bindingResult);
		if (validationError == null) {
			validationError = createViewSupport.validateForm(form);
		}
		if (validationError != null) {
			createViewSupport.populateCreateModel(model, form, profile, organizeResult, validationError);
			return "repositories/new";
		}

		RepositoryCreateResult result = repositoryCreateUseCase.createRepository(
				createViewSupport.toRequest(form, profile)
		);
		if (result.errorMessage() != null) {
			createViewSupport.populateCreateModel(model, form, profile, organizeResult, result.errorMessage());
			return "repositories/new";
		}

		return "redirect:/";
	}

	@GetMapping("/{namespace}/{repoName}")
	public String repositoryDetailPage(@PathVariable("namespace") String namespace,
								   @PathVariable("repoName") String repoName,
								   @RequestParam(name = "branch", required = false) String branch,
								   Authentication authentication,
								   Model model) {
		RepositoryDetailData detail = repositoryDetailUseCase.loadRepositoryDetailByPath(namespace, repoName, branch);
		if (accessSupport.requiresNotFoundForUnauthenticatedPrivate(
				detail,
				userProfileResolver.isAuthenticated(authentication)
		)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		model.addAttribute("namespace", namespace);
		model.addAttribute("repoName", repoName);
		model.addAttribute("currentPath", "");
		model.addAttribute("detail", detail);
		return "repositories/detail";
	}

	@GetMapping({"/{namespace}/{repoName}/tree", "/{namespace}/{repoName}/tree/**"})
	public String repositoryTreePage(@PathVariable("namespace") String namespace,
							 @PathVariable("repoName") String repoName,
							 @RequestParam(name = "branch", required = false) String branch,
							 Authentication authentication,
							 HttpServletRequest request,
							 Model model) {
		String directory = treePathSupport.resolveTreeDirectory(request);
		RepositoryDetailData detail = repositoryDetailUseCase.loadRepositoryTreeByPath(namespace, repoName, branch, directory);
		if (accessSupport.requiresNotFoundForUnauthenticatedPrivate(
				detail,
				userProfileResolver.isAuthenticated(authentication)
		)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		model.addAttribute("namespace", namespace);
		model.addAttribute("repoName", repoName);
		model.addAttribute("currentPath", directory);
		model.addAttribute("detail", detail);
		return "repositories/detail";
	}

	private String resolveValidationError(BindingResult bindingResult) {
		if (bindingResult == null || !bindingResult.hasErrors()) {
			return null;
		}
		if (bindingResult.getFieldError() != null) {
			return bindingResult.getFieldError().getDefaultMessage();
		}
		return messageSource.getMessage(
				"error.request.invalid",
				null,
				"error.request.invalid",
				LocaleContextHolder.getLocale()
		);
	}
}
