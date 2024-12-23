package com.whalewatch.repository;

import com.whalewatch.domain.AlertSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<AlertSetting, Integer> {
}
