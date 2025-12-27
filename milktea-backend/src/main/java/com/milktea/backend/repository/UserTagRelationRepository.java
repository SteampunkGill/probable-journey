package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.UserTagRelation;

import java.util.List;

@Repository
public interface UserTagRelationRepository extends JpaRepository<UserTagRelation, UserTagRelation.UserTagRelationId> {
    List<UserTagRelation> findByUserId(Long userId);
}
