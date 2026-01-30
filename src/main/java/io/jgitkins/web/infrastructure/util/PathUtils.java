package io.jgitkins.web.infrastructure.util;

import org.springframework.util.StringUtils;

public final class PathUtils {

	private PathUtils() {
	}

	public static String trimSlashes(String value) {
		if (!StringUtils.hasText(value)) {
			return "";
		}
		String trimmed = StringUtils.trimLeadingCharacter(value, '/');
		return StringUtils.trimTrailingCharacter(trimmed, '/');
	}
}
