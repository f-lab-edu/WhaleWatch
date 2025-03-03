package com.whalewatch.repository;

import com.whalewatch.domain.UserAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAlertRepository extends JpaRepository<UserAlert, Integer> {
    List<UserAlert> findByUserId(Integer userId);
}
