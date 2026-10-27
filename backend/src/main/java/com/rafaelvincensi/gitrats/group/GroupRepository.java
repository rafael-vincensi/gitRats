package com.rafaelvincensi.gitrats.group;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {

    Optional<Group> findByCode(String code);
}
