package com.rafaelvincensi.gitrats.rank;

import com.rafaelvincensi.gitrats.checkIn.CheckInRepository;
import com.rafaelvincensi.gitrats.rank.dto.GroupLeaderboardDTO;
import com.rafaelvincensi.gitrats.github.GithubService;
import com.rafaelvincensi.gitrats.group.GroupMembership;
import com.rafaelvincensi.gitrats.group.GroupMembershipRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LeaderboardService {

    private final CheckInRepository checkInRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final GithubService githubService;

    public LeaderboardService(CheckInRepository checkInRepository, GroupMembershipRepository groupMembershipRepository, GithubService githubService) {
        this.checkInRepository = checkInRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.githubService = githubService;
    }

    public List<GroupLeaderboardDTO> getGroupCheckInLeaderboard(UUID groupId){
        LocalDateTime pastWeek = LocalDateTime.now().minusDays(7);

        return checkInRepository.findWeeklyLeaderboard(groupId, pastWeek);
    }

    public List<GroupLeaderboardDTO> getGroupCommitLeaderboard(UUID groupId){

        List<GroupMembership> memberships = groupMembershipRepository.findByGroupId(groupId);

        return memberships.stream()
                .map(membership -> {

                    Integer score = githubService.getWeeklyCommits(membership.getUser().getGithubUsername(), membership.getUser().getGithubAccessToken());

                    return new GroupLeaderboardDTO(
                            membership.getUser().getId(),
                            membership.getUser().getName(),
                            membership.getUser().getUsername(),
                            membership.getUser().getAvatarUrl(),
                            score.longValue());
                })
                .sorted((a,b) -> Long.compare(b.score(),a.score()))
                .toList();
    }

}
