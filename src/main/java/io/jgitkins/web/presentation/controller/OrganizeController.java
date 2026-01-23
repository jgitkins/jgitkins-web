package io.jgitkins.web.presentation.controller;

import io.jgitkins.web.application.dto.OrganizeCreateRequest;
import io.jgitkins.web.application.dto.OrganizeCreateResult;
import io.jgitkins.web.application.port.in.OrganizeCreateUseCase;
import io.jgitkins.web.presentation.dto.OrganizeCreateForm;
import io.jgitkins.web.presentation.support.SessionUserSupport;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class OrganizeController {

	private static final Pattern ORGANIZE_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");

	private final OrganizeCreateUseCase organizeCreateUseCase;
	private final SessionUserSupport sessionUserSupport;

	@GetMapping("/organizes/new")
	public String newOrganize(Model model) {
		OrganizeCreateForm form = new OrganizeCreateForm();
		model.addAttribute("form", form);
		return "organizes/new";
	}

	@PostMapping("/organizes")
	public String createOrganize(@ModelAttribute("form") OrganizeCreateForm form, Model model) {
		String validationError = validateForm(form);
		if (validationError != null) {
			model.addAttribute("formError", validationError);
			return "organizes/new";
		}

		Optional<Long> ownerId = sessionUserSupport.resolveUserId();
		if (ownerId.isEmpty()) {
			model.addAttribute("formError", "로그인이 필요합니다. 다시 로그인해 주세요.");
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

	private String validateForm(OrganizeCreateForm form) {
		if (form == null) {
			return "요청이 올바르지 않습니다.";
		}
		if (!StringUtils.hasText(form.getName())) {
			return "조직 이름을 입력해 주세요.";
		}
		if (!ORGANIZE_NAME_PATTERN.matcher(form.getName().trim()).matches()) {
			return "조직 이름은 영문, 숫자, 하이픈(-), 언더스코어(_)만 사용할 수 있습니다.";
		}
		return null;
	}
}
