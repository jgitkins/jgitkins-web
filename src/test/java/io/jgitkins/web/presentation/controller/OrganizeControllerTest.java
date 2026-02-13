package io.jgitkins.web.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jgitkins.web.application.dto.OrganizeCreateResult;
import io.jgitkins.web.application.dto.OrganizeSummary;
import io.jgitkins.web.application.port.in.OrganizeCreateUseCase;
import io.jgitkins.web.presentation.dto.OrganizeCreateForm;
import io.jgitkins.web.presentation.support.SessionUserSupport;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
class OrganizeControllerTest {

	@Mock
	private OrganizeCreateUseCase organizeCreateUseCase;
	@Mock
	private SessionUserSupport sessionUserSupport;
	@Mock
	private MessageSource messageSource;

	private OrganizeController controller;

	@BeforeEach
	void setUp() {
		controller = new OrganizeController(organizeCreateUseCase, sessionUserSupport, messageSource);
		Mockito.lenient().when(messageSource.getMessage(eq("error.auth.login_required"), isNull(), eq("error.auth.login_required"), any()))
				.thenReturn("로그인이 필요합니다. 다시 로그인해 주세요.");
	}

	@Test
	void createOrganize_returnsFormErrorWhenValidationFails() {
		OrganizeCreateForm form = new OrganizeCreateForm();
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
		bindingResult.rejectValue("name", "NotBlank", "조직 이름을 입력해 주세요.");
		Model model = new ConcurrentModel();

		String view = controller.createOrganize(form, bindingResult, model);

		assertEquals("organizes/new", view);
		assertEquals("조직 이름을 입력해 주세요.", model.getAttribute("formError"));
		verify(organizeCreateUseCase, never()).createOrganize(any());
	}

	@Test
	void createOrganize_returnsLoginErrorWhenOwnerMissing() {
		OrganizeCreateForm form = new OrganizeCreateForm();
		form.setName("my-org");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
		Model model = new ConcurrentModel();
		when(sessionUserSupport.resolveUserId()).thenReturn(Optional.empty());

		String view = controller.createOrganize(form, bindingResult, model);

		assertEquals("organizes/new", view);
		assertEquals("로그인이 필요합니다. 다시 로그인해 주세요.", model.getAttribute("formError"));
		verify(organizeCreateUseCase, never()).createOrganize(any());
	}

	@Test
	void createOrganize_createsOrganizationWhenInputsValid() {
		OrganizeCreateForm form = new OrganizeCreateForm();
		form.setName("my-org");
		form.setDescription("desc");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
		Model model = new ConcurrentModel();
		when(sessionUserSupport.resolveUserId()).thenReturn(Optional.of(7L));
		when(organizeCreateUseCase.createOrganize(any()))
				.thenReturn(new OrganizeCreateResult(new OrganizeSummary(1L, "my-org", "desc", 7L, null, null), null));

		String view = controller.createOrganize(form, bindingResult, model);

		assertEquals("redirect:/", view);
		verify(organizeCreateUseCase).createOrganize(any());
	}
}
