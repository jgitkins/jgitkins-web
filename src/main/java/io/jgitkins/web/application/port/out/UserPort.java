package io.jgitkins.web.application.port.out;

import io.jgitkins.web.application.dto.UserSummary;
import java.util.List;

public interface UserPort {
	List<UserSummary> fetchUsers();
}
