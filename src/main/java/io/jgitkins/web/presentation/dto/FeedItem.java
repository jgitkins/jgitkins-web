package io.jgitkins.web.presentation.dto;

import java.time.LocalDateTime;

public record FeedItem(
		String namespace,
		String repoName,
		String commitMessage,
		String authorName,
		LocalDateTime commitTime
) {
}
