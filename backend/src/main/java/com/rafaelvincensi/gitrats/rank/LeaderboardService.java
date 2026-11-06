package com.rafaelvincensi.gitrats.rank;

import com.rafaelvincensi.gitrats.checkIn.CheckInRepository;
import com.rafaelvincensi.gitrats.checkIn.GroupLeaderboardDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LeaderboardService {

    private final CheckInRepository checkInRepository;

    public LeaderboardService(CheckInRepository checkInRepository) {
        this.checkInRepository = checkInRepository;
    }

    public List<GroupLeaderboardDTO> getGroupCheckInLeaderboard(UUID groupId){
        LocalDateTime pastWeek = LocalDateTime.now().minusDays(7);

        return checkInRepository.findWeeklyLeaderboard(groupId, pastWeek);
    }

}
