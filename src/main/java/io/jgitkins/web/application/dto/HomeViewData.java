package io.jgitkins.web.application.dto;

import io.jgitkins.web.presentation.dto.DashboardView;
import java.time.LocalDateTime;

public record HomeViewData(
        DashboardView dashboard,
        String displayName,
        boolean pendingUsername,
        String usernameSetupError,
        LocalDateTime lastUpdated) {
}
