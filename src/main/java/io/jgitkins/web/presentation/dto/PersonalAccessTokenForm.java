package io.jgitkins.web.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalAccessTokenForm {
	private String name;
	private String description;
	private String expiration;
}
