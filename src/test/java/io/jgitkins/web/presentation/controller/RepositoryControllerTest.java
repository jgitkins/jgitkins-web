package io.jgitkins.web.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.RepositoryBranchCreateResult;
import io.jgitkins.web.application.dto.RepositoryCreateResult;
import io.jgitkins.web.application.dto.RepositoryFileEntry;
import io.jgitkins.web.application.port.in.RepositoryCreateUseCase;
import io.jgitkins.web.application.port.in.RepositoryDetailUseCase;
import io.jgitkins.web.application.port.in.RepositoryManageUseCase;
import io.jgitkins.web.presentation.dto.RepositoryCreateForm;
import io.jgitkins.web.presentation.support.RepositoryAccessSupport;
import io.jgitkins.web.presentation.support.RepositoryCreateViewSupport;
import io.jgitkins.web.presentation.support.RepositoryTreePathSupport;
import io.jgitkins.web.presentation.support.RepositoryUserProfile;
import io.jgitkins.web.presentation.support.RepositoryUserProfileResolver;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@ExtendWith(MockitoExtension.class)
class RepositoryControllerTest {

	@Mock
	private RepositoryCreateUseCase repositoryCreateUseCase;
	@Mock
	private RepositoryDetailUseCase repositoryDetailUseCase;
	@Mock
	private RepositoryUserProfileResolver userProfileResolver;
	@Mock
	private RepositoryCreateViewSupport createViewSupport;
	@Mock
	private RepositoryTreePathSupport treePathSupport;
	@Mock
	private RepositoryAccessSupport accessSupport;
	@Mock
	private RepositoryManageUseCase repositoryManageUseCase;
	@Mock
	private MessageSource messageSource;

	private RepositoryController controller;

	@BeforeEach
	void setUp() {
		controller = new RepositoryController(
				repositoryCreateUseCase,
				repositoryDetailUseCase,
				userProfileResolver,
				createViewSupport,
				treePathSupport,
				accessSupport,
				repositoryManageUseCase,
				messageSource
		);
		Mockito.lenient().when(userProfileResolver.resolve(any())).thenReturn(new RepositoryUserProfile("alzar", "a@b.c"));
		Mockito.lenient().when(repositoryCreateUseCase.loadOwnerOptions()).thenReturn(new OrganizeFetchResult(List.of(), null));
	}

	@Test
	void createRepository_returnsFormViewWhenBindingHasErrors() {
		RepositoryCreateForm form = new RepositoryCreateForm();
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
		bindingResult.rejectValue("repoName", "NotBlank", "Repository name is required.");
		Model model = new ConcurrentModel();

		String view = controller.createRepository(form, bindingResult, null, model);

		assertEquals("repositories/new", view);
		verify(createViewSupport).populateCreateModel(any(), any(), any(), any(), any());
		verify(repositoryCreateUseCase, never()).createRepository(any());
	}

	@Test
	void createRepository_createsWhenValidationPasses() {
		RepositoryCreateForm form = new RepositoryCreateForm();
		form.setRepoName("demo");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
		Model model = new ConcurrentModel();
		when(createViewSupport.validateForm(form)).thenReturn(null);
		when(createViewSupport.toRequest(any(), any())).thenReturn(null);
		when(repositoryCreateUseCase.createRepository(any())).thenReturn(new RepositoryCreateResult(null, null));

		String view = controller.createRepository(form, bindingResult, null, model);

		assertEquals("redirect:/", view);
		verify(repositoryCreateUseCase).createRepository(any());
	}

	@Test
	void createBranch_redirectsToNewBranchOnSuccess() {
		RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
		when(repositoryManageUseCase.createBranchByPath("team", "demo", "feature/x", "main"))
				.thenReturn(new RepositoryBranchCreateResult(new io.jgitkins.web.application.dto.BranchSummary(1L, "feature/x", false, false, false), null));

		String view = controller.createBranch("team", "demo", "feature/x", "main", "src", "main", redirect);

		assertEquals("redirect:/team/demo/tree/src?branch=feature/x", view);
	}

	@Test
	void uploadFile_redirectsWithErrorWhenUploadFails() {
		RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
		MockMultipartFile file = new MockMultipartFile("file", "README.md", "text/markdown", "# hi".getBytes());
		when(repositoryManageUseCase.uploadFileByPath("team", "demo", "main", "README.md", "add", file))
				.thenReturn(new io.jgitkins.web.application.dto.RepositoryFileUploadResult("failed"));

		String view = controller.uploadFile("team", "demo", "main", "README.md", "add", file, "", redirect);

		assertEquals("redirect:/team/demo?branch=main", view);
		assertEquals("failed", redirect.getFlashAttributes().get("fileError"));
	}

	@Test
	void findFiles_returnsSearchResultList() {
		when(repositoryDetailUseCase.searchRepositoryFilesByPath("team", "demo", "main", "read", 20))
				.thenReturn(List.of(new RepositoryFileEntry("1", "README.md", "README.md", "blob", "100644", 10L)));

		var response = controller.findFiles("team", "demo", "main", "read", 20);

		assertEquals(200, response.getStatusCode().value());
		assertEquals(1, response.getBody().size());
		assertEquals("README.md", response.getBody().get(0).name());
	}
}
