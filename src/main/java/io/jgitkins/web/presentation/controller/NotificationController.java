package io.jgitkins.web.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class NotificationController {

	@GetMapping("/notifications")
	public String notifications() {
		return "notifications/index";
	}
}
