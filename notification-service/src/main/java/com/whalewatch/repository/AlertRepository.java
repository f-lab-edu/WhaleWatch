package com.whalewatch.repository;

import com.whalewatch.domain.AlertSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<AlertSetting, Integer> {
    List<AlertSetting> findByCoin(String coin);
}
