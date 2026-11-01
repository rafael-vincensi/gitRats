package com.rafaelvincensi.gitrats.CheckIn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {

    List<CheckIn> findByGroupIdOrderByCreatedAtDesc(UUID groupId);

}
