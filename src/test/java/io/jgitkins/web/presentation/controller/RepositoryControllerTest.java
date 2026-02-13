package io.jgitkins.web.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jgitkins.web.application.dto.OrganizeFetchResult;
import io.jgitkins.web.application.dto.RepositoryCreateResult;
import io.jgitkins.web.application.port.in.RepositoryCreateUseCase;
import io.jgitkins.web.application.port.in.RepositoryDetailUseCase;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

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
				messageSource
		);
		when(userProfileResolver.resolve(any())).thenReturn(new RepositoryUserProfile("alzar", "a@b.c"));
		when(repositoryCreateUseCase.loadOwnerOptions()).thenReturn(new OrganizeFetchResult(List.of(), null));
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
}
