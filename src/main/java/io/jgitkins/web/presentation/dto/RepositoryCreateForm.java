package io.jgitkins.web.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RepositoryCreateForm {

	private String ownerType = "USER";
	private Long organizeId;
	private String repoName;
	private String description;
	private String visibility = "PRIVATE";
	private String mainBranch = "main";
	private boolean readme = true;
	private String message = "Initial commit";
}
