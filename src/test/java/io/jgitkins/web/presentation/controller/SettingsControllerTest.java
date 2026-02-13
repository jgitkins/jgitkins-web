package io.jgitkins.web.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jgitkins.web.application.dto.UserCredentialIssueRequest;
import io.jgitkins.web.application.dto.UserCredentialIssueResult;
import io.jgitkins.web.application.port.in.PersonalAccessTokenIssueUseCase;
import io.jgitkins.web.application.port.in.PersonalAccessTokenQueryUseCase;
import io.jgitkins.web.presentation.dto.PersonalAccessTokenForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@ExtendWith(MockitoExtension.class)
class SettingsControllerTest {

	@Mock
	private PersonalAccessTokenQueryUseCase queryUseCase;
	@Mock
	private PersonalAccessTokenIssueUseCase issueUseCase;
	@Mock
	private MessageSource messageSource;

	private SettingsController controller;

	@BeforeEach
	void setUp() {
		controller = new SettingsController(queryUseCase, issueUseCase, messageSource);
		Mockito.lenient().when(messageSource.getMessage(eq("validation.pat.fields.required"), isNull(), eq("validation.pat.fields.required"), any()))
				.thenReturn("All fields are required.");
		Mockito.lenient().when(messageSource.getMessage(eq("error.token.create_failed"), isNull(), eq("error.token.create_failed"), any()))
				.thenReturn("Failed to create token. Please try again.");
	}

	@Test
	void createPersonalAccessToken_passesExpirationToIssueRequest() {
		PersonalAccessTokenForm form = new PersonalAccessTokenForm();
		form.setName("ci-token");
		form.setDescription("for ci");
		form.setExpiration("2026-12-31");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
		Model model = new ConcurrentModel();
		RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

		when(issueUseCase.issueToken(any())).thenReturn(new UserCredentialIssueResult(1L, "jkpat_token"));

		String view = controller.createPersonalAccessToken(form, bindingResult, model, redirect);

		ArgumentCaptor<UserCredentialIssueRequest> captor = ArgumentCaptor.forClass(UserCredentialIssueRequest.class);
		verify(issueUseCase).issueToken(captor.capture());
		UserCredentialIssueRequest request = captor.getValue();
		assertEquals("ci-token", request.name());
		assertEquals("for ci", request.description());
		assertEquals("2026-12-31", request.expiration());
		assertEquals("redirect:/settings/personal-access-tokens", view);
		assertEquals("jkpat_token", redirect.getFlashAttributes().get("issuedToken"));
	}

	@Test
	void createPersonalAccessToken_rejectsWhenExpirationMissing() {
		PersonalAccessTokenForm form = new PersonalAccessTokenForm();
		form.setName("ci-token");
		form.setDescription("for ci");
		form.setExpiration(" ");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
		bindingResult.rejectValue("expiration", "NotBlank", "All fields are required.");
		Model model = new ConcurrentModel();
		RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

		String view = controller.createPersonalAccessToken(form, bindingResult, model, redirect);

		verify(issueUseCase, never()).issueToken(any());
		assertEquals("settings/personal-access-tokens/new", view);
		assertSame("All fields are required.", model.getAttribute("error"));
	}
}
