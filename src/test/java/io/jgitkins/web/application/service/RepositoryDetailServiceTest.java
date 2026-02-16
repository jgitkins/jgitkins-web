package io.jgitkins.web.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jgitkins.web.application.dto.CommitSummary;
import io.jgitkins.web.application.dto.RepositoryDetailData;
import io.jgitkins.web.application.dto.RepositoryFileEntry;
import io.jgitkins.web.application.dto.RepositoryOverviewResult;
import io.jgitkins.web.application.dto.RepositorySummary;
import io.jgitkins.web.application.port.out.RepositoryPort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RepositoryDetailServiceTest {

    @Mock
    private RepositoryPort repositoryPort;

    @Mock
    private RepositoryTreeCacheSupport repositoryTreeCacheSupport;

    private RepositoryDetailService service;

    @BeforeEach
    void setUp() {
        service = new RepositoryDetailService(repositoryPort, repositoryTreeCacheSupport);
    }

    @Test
    void loadRepositoryTreeByPath_usesCachedTree_whenCacheHit() {
        RepositorySummary summary = repositorySummary();
        RepositoryOverviewResult overview = new RepositoryOverviewResult(summary, List.of(), List.of(), "main");
        List<RepositoryFileEntry> cached = List.of(entry("cached-file"));

        when(repositoryPort.fetchRepositories()).thenReturn(List.of(summary));
        when(repositoryPort.fetchRepositoryOverview(1L, "main")).thenReturn(overview);
        when(repositoryPort.fetchCommits("users/alice", "demo", "main"))
                .thenReturn(List.of(new CommitSummary("c1", "alice", "a@test.com", "msg", LocalDateTime.now())));
        when(repositoryTreeCacheSupport.get("users/alice", "demo", "main", "src", "c1"))
                .thenReturn(Optional.of(cached));

        RepositoryDetailData result = service.loadRepositoryTreeByPath("users/alice", "demo", "main", "src");

        assertThat(result.files()).hasSize(1);
        assertThat(result.files().get(0).name()).isEqualTo("cached-file");
        verify(repositoryPort, never()).fetchRepositoryTree("users/alice", "demo", "main", "src");
    }

    @Test
    void loadRepositoryTreeByPath_fetchesAndCaches_whenCacheMiss() {
        RepositorySummary summary = repositorySummary();
        RepositoryOverviewResult overview = new RepositoryOverviewResult(summary, List.of(), List.of(), "main");
        List<RepositoryFileEntry> loaded = List.of(entry("loaded-file"));

        when(repositoryPort.fetchRepositories()).thenReturn(List.of(summary));
        when(repositoryPort.fetchRepositoryOverview(1L, "main")).thenReturn(overview);
        when(repositoryPort.fetchCommits("users/alice", "demo", "main"))
                .thenReturn(List.of(new CommitSummary("c1", "alice", "a@test.com", "msg", LocalDateTime.now())));
        when(repositoryTreeCacheSupport.get("users/alice", "demo", "main", "src", "c1"))
                .thenReturn(Optional.empty());
        when(repositoryPort.fetchRepositoryTree("users/alice", "demo", "main", "src")).thenReturn(loaded);

        RepositoryDetailData result = service.loadRepositoryTreeByPath("users/alice", "demo", "main", "src");

        assertThat(result.files()).hasSize(1);
        assertThat(result.files().get(0).name()).isEqualTo("loaded-file");
        verify(repositoryPort).fetchRepositoryTree("users/alice", "demo", "main", "src");
        verify(repositoryTreeCacheSupport).put(
                org.mockito.ArgumentMatchers.eq("users/alice"),
                org.mockito.ArgumentMatchers.eq("demo"),
                org.mockito.ArgumentMatchers.eq("main"),
                org.mockito.ArgumentMatchers.eq("src"),
                org.mockito.ArgumentMatchers.eq("c1"),
                org.mockito.ArgumentMatchers.eq(loaded),
                org.mockito.ArgumentMatchers.any()
        );
    }

    private RepositorySummary repositorySummary() {
        return new RepositorySummary(
                1L,
                "USER",
                "demo",
                "users/alice/demo",
                "main",
                "PUBLIC",
                null,
                1L,
                "users/alice/demo.git",
                null,
                null,
                null,
                null
        );
    }

    private RepositoryFileEntry entry(String name) {
        return new RepositoryFileEntry("id", name, name, "blob", "100644", 12L);
    }
}
