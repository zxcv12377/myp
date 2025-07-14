package com.example.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.server.entity.ViewLog;
import com.example.server.entity.ViewLogId;

public interface ViewLogRepository extends JpaRepository<ViewLog, ViewLogId> {
    boolean existsById(ViewLogId id);
}