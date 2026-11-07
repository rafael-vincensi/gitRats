package com.rafaelvincensi.gitrats.checkIn;

import com.rafaelvincensi.gitrats.rank.dto.GroupLeaderboardDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {

    List<CheckIn> findByGroupIdOrderByCreatedAtDesc(UUID groupId);
    List<CheckIn> findByGroupIdInOrderByCreatedAtDesc(List<UUID> groupsId);

    @Query("SELECT new com.rafaelvincensi.gitrats.checkIn.GroupLeaderboardDTO(" +
            "c.user.id, c.user.name, c.user.username, c.user.avatarUrl, COUNT(c)) " +
            "FROM CheckIn c " +
            "WHERE c.group.id = :groupId AND c.createdAt >= :since " +
            "GROUP BY c.user.id, c.user.name, c.user.username, c.user.avatarUrl " +
            "ORDER BY COUNT(c) DESC")
    List<GroupLeaderboardDTO> findWeeklyLeaderboard(
            @Param("groupId") UUID groupId,
            @Param("since") LocalDateTime since
    );

}
