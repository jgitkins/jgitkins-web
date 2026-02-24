package io.jgitkins.web.application.port.in.facade;

import io.jgitkins.web.application.dto.HomeViewData;
import org.springframework.security.core.Authentication;
import jakarta.servlet.http.HttpServletRequest;

public interface HomeFacadeUseCase {

    HomeViewData getHomeViewData(Authentication authentication, HttpServletRequest request);

}
