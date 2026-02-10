package com.flowdesk.flowdesk.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
    select p from Project p
    where p.deletedAt is null
      and exists (
        select 1 from ProjectMember pm
        where pm.projectId = p.id and pm.userId = :userId
      )
    order by p.createdAt desc
""")
    List<Project> findAllForMember(Long userId);

}
