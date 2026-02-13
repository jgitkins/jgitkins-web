package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.dto.OrganizeCreateRequest;
import io.jgitkins.web.application.dto.OrganizeCreateResult;
import io.jgitkins.web.application.port.in.OrganizeCreateUseCase;
import io.jgitkins.web.presentation.dto.OrganizeCreateForm;
import io.jgitkins.web.presentation.support.SessionUserSupport;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class OrganizeController {

	private final OrganizeCreateUseCase organizeCreateUseCase;
	private final SessionUserSupport sessionUserSupport;
	private final MessageSource messageSource;

	@GetMapping("/organizes/new")
	public String newOrganize(Model model) {
		OrganizeCreateForm form = new OrganizeCreateForm();
		model.addAttribute("form", form);
		return "organizes/new";
	}

	@PostMapping("/organizes")
	public String createOrganize(@Valid @ModelAttribute("form") OrganizeCreateForm form,
								 BindingResult bindingResult,
								 Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("formError", bindingResult.getFieldError() != null
					? bindingResult.getFieldError().getDefaultMessage()
					: "요청이 올바르지 않습니다.");
			return "organizes/new";
		}

		Optional<Long> ownerId = sessionUserSupport.resolveUserId();
		if (ownerId.isEmpty()) {
			model.addAttribute("formError", getMessage("error.auth.login_required"));
			return "organizes/new";
		}

		OrganizeCreateRequest request = new OrganizeCreateRequest(
				form.getName(),
				ownerId.get(),
				form.getDescription()
		);

		OrganizeCreateResult result = organizeCreateUseCase.createOrganize(request);
		if (result.errorMessage() != null) {
			model.addAttribute("formError", result.errorMessage());
			return "organizes/new";
		}

		return "redirect:/";
	}

	private String getMessage(String code) {
		return messageSource.getMessage(code, null, code, LocaleContextHolder.getLocale());
	}
}
