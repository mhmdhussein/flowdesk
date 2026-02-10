package com.flowdesk.flowdesk.security;

import com.flowdesk.flowdesk.project.ProjectMemberRepository;
import com.flowdesk.flowdesk.user.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ProjectAuthorizationService {

    private final UserRepository users;
    private final ProjectMemberRepository members;

    public ProjectAuthorizationService(UserRepository users, ProjectMemberRepository members) {
        this.users = users;
        this.members = members;
    }

    public Long requireUserId(String email) {
        return users.findIdByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Unauthorized"));
    }

    public void requireProjectMember(Long projectId, Long userId) {
        if (!members.existsByProjectIdAndUserId(projectId, userId)) {
            throw new AccessDeniedException("Forbidden");
        }
    }
}
