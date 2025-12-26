package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.PointExchangeRecordStatusTimeline;

@Repository
public interface PointExchangeRecordStatusTimelineRepository extends JpaRepository<PointExchangeRecordStatusTimeline, Long> {
}
