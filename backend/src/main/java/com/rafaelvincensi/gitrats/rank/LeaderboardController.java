package com.rafaelvincensi.gitrats.rank;

import com.rafaelvincensi.gitrats.rank.dto.GroupLeaderboardDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/groups")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/{groupId}/leaderboard/checkins")
    public ResponseEntity<List<GroupLeaderboardDTO>> getCheckInLeaderboard(@PathVariable UUID groupId){
        List<GroupLeaderboardDTO> leaderboard = leaderboardService.getGroupCheckInLeaderboard(groupId);

        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/{groupId}/leaderboard/commits")
    public ResponseEntity<List<GroupLeaderboardDTO>> getCommitLeaderboard(@PathVariable UUID groupId){
        List<GroupLeaderboardDTO> leaderboard = leaderboardService.getGroupCommitLeaderboard(groupId);

        return ResponseEntity.ok(leaderboard);
    }

}
