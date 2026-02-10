package com.flowdesk.flowdesk.project;

import com.flowdesk.flowdesk.project.dto.CreateProjectRequest;
import com.flowdesk.flowdesk.project.dto.ProjectResponse;
import com.flowdesk.flowdesk.security.ProjectAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projects;
    private final ProjectMemberRepository members;
    private final ProjectAuthorizationService authz;

    public ProjectController(
            ProjectRepository projects,
            ProjectMemberRepository members,
            ProjectAuthorizationService authz
    ) {
        this.projects = projects;
        this.members = members;
        this.authz = authz;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(Authentication auth, @Valid @RequestBody CreateProjectRequest request) {
        Long userId = authz.requireUserId(auth.getName());

        Project project = new Project(request.name().trim(), userId);
        Project saved = projects.save(project);

        members.save(new ProjectMember(saved.getId(), userId));

        return toResponse(saved);
    }

    @GetMapping
    public List<ProjectResponse> listMine(Authentication auth) {
        Long userId = authz.requireUserId(auth.getName());
        return projects.findAllForMember(userId).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ProjectResponse get(Authentication auth, @PathVariable Long id) {
        Long userId = authz.requireUserId(auth.getName());

        Project project = projects.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        authz.requireProjectMember(id, userId);

        return toResponse(project);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(Authentication auth, @PathVariable Long id) {
        Long userId = authz.requireUserId(auth.getName());

        Project project = projects.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        authz.requireProjectMember(id, userId);

        project.softDelete();
        projects.save(project);
    }

    private ProjectResponse toResponse(Project p) {
        return new ProjectResponse(p.getId(), p.getName(), p.getCreatedAt());
    }
}
