package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.AboutUs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AboutUsRepository extends JpaRepository<AboutUs, Long> {
    Optional<AboutUs> findFirstByOrderByIdAsc();
}