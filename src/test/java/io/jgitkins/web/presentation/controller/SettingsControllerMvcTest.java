package io.jgitkins.web.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import io.jgitkins.web.application.port.in.PersonalAccessTokenIssueUseCase;
import io.jgitkins.web.application.port.in.PersonalAccessTokenQueryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class SettingsControllerMvcTest {

	@Mock
	private PersonalAccessTokenQueryUseCase queryUseCase;
	@Mock
	private PersonalAccessTokenIssueUseCase issueUseCase;
	@Mock
	private MessageSource messageSource;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		SettingsController controller = new SettingsController(queryUseCase, issueUseCase, messageSource);
		when(messageSource.getMessage(eq("validation.pat.fields.required"), isNull(), eq("validation.pat.fields.required"), any()))
				.thenReturn("All fields are required.");
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setValidator(validator)
				.build();
	}

	@Test
	void createPersonalAccessToken_returnsValidationErrorViewWhenRequiredFieldsMissing() throws Exception {
		mockMvc.perform(post("/settings/personal-access-tokens")
						.param("name", "")
						.param("description", "desc")
						.param("expiration", ""))
				.andExpect(status().isOk())
				.andExpect(view().name("settings/personal-access-tokens/new"))
				.andExpect(model().attribute("error", "All fields are required."));
	}
}
