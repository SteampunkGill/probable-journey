package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.PageConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageConfigRepository extends JpaRepository<PageConfig, Long> {
    List<PageConfig> findByPage(String page);
    List<PageConfig> findByPageAndModule(String page, String module);
}