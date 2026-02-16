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

    @Mock
    private RepositoryFileIndexCacheSupport repositoryFileIndexCacheSupport;

    private RepositoryDetailService service;

    @BeforeEach
    void setUp() {
        service = new RepositoryDetailService(repositoryPort, repositoryTreeCacheSupport, repositoryFileIndexCacheSupport);
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
    void searchRepositoryFilesByPath_usesIndexCacheAndFiltersByKeyword() {
        List<RepositoryFileEntry> cached = List.of(
                new RepositoryFileEntry("1", "README.md", "README.md", "blob", "100644", 10L),
                new RepositoryFileEntry("2", "RepoService.java", "src/RepoService.java", "blob", "100644", 100L),
                new RepositoryFileEntry("3", "Other.java", "src/Other.java", "blob", "100644", 100L)
        );

        when(repositoryPort.fetchCommits("users/alice", "demo", "main"))
                .thenReturn(List.of(new CommitSummary("c1", "alice", "a@test.com", "msg", LocalDateTime.now())));
        when(repositoryFileIndexCacheSupport.get("users/alice", "demo", "main", "c1"))
                .thenReturn(Optional.of(cached));

        List<RepositoryFileEntry> result = service.searchRepositoryFilesByPath("users/alice", "demo", "main", "repo", 20);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).path()).isEqualTo("src/RepoService.java");
        verify(repositoryPort, never()).fetchRepositoryFiles("users/alice", "demo", "main");
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
