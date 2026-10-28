package com.rafaelvincensi.gitrats.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, UUID> {

        boolean existsByGroupIdAndUserId(UUID groupId, UUID userId);
        List<GroupMembership> findByUserId(UUID userId);
        long countByGroupId(UUID groupID);

}
