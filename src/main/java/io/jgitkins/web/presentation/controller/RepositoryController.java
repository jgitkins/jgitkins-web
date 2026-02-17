package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.RepositoryBranchCreateResult;
import io.jgitkins.web.application.dto.RepositoryCreateResult;
import io.jgitkins.web.application.dto.RepositoryDetailData;
import io.jgitkins.web.application.dto.RepositoryFileEntry;
import io.jgitkins.web.application.dto.RepositoryFileIndexEntry;
import io.jgitkins.web.application.port.in.RepositoryCreateUseCase;
import io.jgitkins.web.application.port.in.RepositoryDetailUseCase;
import io.jgitkins.web.application.port.in.RepositoryManageUseCase;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RepositoryController {

	private final RepositoryCreateUseCase repositoryCreateUseCase;
	private final RepositoryDetailUseCase repositoryDetailUseCase;
	private final RepositoryUserProfileResolver userProfileResolver;
	private final RepositoryCreateViewSupport createViewSupport;
	private final RepositoryTreePathSupport treePathSupport;
	private final RepositoryAccessSupport accessSupport;
	private final RepositoryManageUseCase repositoryManageUseCase;
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

	@GetMapping("/{namespace}/{repoName}/find-files")
	@ResponseBody
	public ResponseEntity<List<RepositoryFileIndexEntry>> findFiles(@PathVariable("namespace") String namespace,
											   @PathVariable("repoName") String repoName,
											   @RequestParam(name = "branch", required = false) String branch,
											   @RequestParam(name = "q", required = false, defaultValue = "") String query,
											   @RequestParam(name = "limit", required = false, defaultValue = "20") int limit) {
		List<RepositoryFileIndexEntry> files = repositoryDetailUseCase.searchRepositoryFilesByPath(namespace, repoName, branch, query, limit);
		return ResponseEntity.ok(files);
	}

	@PostMapping("/{namespace}/{repoName}/branches")
	public String createBranch(@PathVariable("namespace") String namespace,
							   @PathVariable("repoName") String repoName,
							   @RequestParam("branchName") String branchName,
							   @RequestParam(name = "sourceBranch", required = false) String sourceBranch,
							   @RequestParam(name = "currentPath", required = false) String currentPath,
							   @RequestParam(name = "currentBranch", required = false) String currentBranch,
							   RedirectAttributes redirectAttributes) {
		String baseBranch = org.springframework.util.StringUtils.hasText(sourceBranch) ? sourceBranch : currentBranch;
		RepositoryBranchCreateResult result = repositoryManageUseCase.createBranchByPath(namespace, repoName, branchName, baseBranch);
		if (result.errorMessage() != null) {
			redirectAttributes.addFlashAttribute("branchError", result.errorMessage());
			return "redirect:" + buildRepositoryRedirect(namespace, repoName, currentPath, currentBranch);
		}

		String selectedBranch = result.branch() != null && org.springframework.util.StringUtils.hasText(result.branch().name())
				? result.branch().name()
				: branchName;
		redirectAttributes.addFlashAttribute("branchSuccess", "브랜치가 생성되었습니다.");
		return "redirect:" + buildRepositoryRedirect(namespace, repoName, currentPath, selectedBranch);
	}

	@PostMapping("/{namespace}/{repoName}/files")
	public String uploadFile(@PathVariable("namespace") String namespace,
							 @PathVariable("repoName") String repoName,
							 @RequestParam("branch") String branch,
							 @RequestParam("path") String path,
							 @RequestParam("message") String message,
							 @RequestParam("file") MultipartFile file,
							 @RequestParam(name = "currentPath", required = false) String currentPath,
							 RedirectAttributes redirectAttributes) {
		var result = repositoryManageUseCase.uploadFileByPath(namespace, repoName, branch, path, message, file);
		if (result.errorMessage() != null) {
			redirectAttributes.addFlashAttribute("fileError", result.errorMessage());
			return "redirect:" + buildRepositoryRedirect(namespace, repoName, currentPath, branch);
		}

		redirectAttributes.addFlashAttribute("fileSuccess", "파일이 업로드되었습니다.");
		return "redirect:" + buildRepositoryRedirect(namespace, repoName, currentPath, branch);
	}

	@PostMapping("/{namespace}/{repoName}/directories")
	public String createDirectory(@PathVariable("namespace") String namespace,
								  @PathVariable("repoName") String repoName,
								  @RequestParam("branch") String branch,
								  @RequestParam("directoryPath") String directoryPath,
								  @RequestParam("message") String message,
								  @RequestParam(name = "currentPath", required = false) String currentPath,
								  RedirectAttributes redirectAttributes) {
		var result = repositoryManageUseCase.createDirectoryByPath(namespace, repoName, branch, directoryPath, message);
		if (result.errorMessage() != null) {
			redirectAttributes.addFlashAttribute("directoryError", result.errorMessage());
			return "redirect:" + buildRepositoryRedirect(namespace, repoName, currentPath, branch);
		}

		redirectAttributes.addFlashAttribute("directorySuccess", "디렉터리가 생성되었습니다.");
		return "redirect:" + buildRepositoryRedirect(namespace, repoName, currentPath, branch);
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

	private String buildRepositoryRedirect(String namespace, String repoName, String currentPath, String branch) {
		StringBuilder builder = new StringBuilder();
		if (org.springframework.util.StringUtils.hasText(currentPath)) {
			builder.append("/").append(namespace).append("/").append(repoName).append("/tree/").append(currentPath.trim());
		} else {
			builder.append("/").append(namespace).append("/").append(repoName);
		}
		if (org.springframework.util.StringUtils.hasText(branch)) {
			builder.append("?branch=").append(branch.trim());
		}
		return builder.toString();
	}
}
