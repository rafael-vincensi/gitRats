package com.rafaelvincensi.gitrats.CheckIn;

import com.rafaelvincensi.gitrats.CheckIn.dto.CreateCheckInDTO;
import com.rafaelvincensi.gitrats.exception.NotFoundException;
import com.rafaelvincensi.gitrats.group.Group;
import com.rafaelvincensi.gitrats.group.GroupMembership;
import com.rafaelvincensi.gitrats.group.GroupMembershipRepository;
import com.rafaelvincensi.gitrats.group.GroupRepository;
import com.rafaelvincensi.gitrats.user.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;

    public CheckInService(CheckInRepository checkInRepository, GroupRepository groupRepository, GroupMembershipRepository groupMembershipRepository) {
        this.checkInRepository = checkInRepository;
        this.groupRepository = groupRepository;
        this.groupMembershipRepository = groupMembershipRepository;
    }

    public CheckIn createCheckIn(User user, CreateCheckInDTO request){
        boolean isMember = groupMembershipRepository.existsByGroupIdAndUserId(request.groupId(), user.getId());

        if (!isMember){
            throw new AccessDeniedException("You can only post check-in in your groups!");
        }

        Group group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new NotFoundException("Group not found!"));

        CheckIn newCheckIn = new CheckIn();
        newCheckIn.setTitle(request.title());
        newCheckIn.setDescription(request.description());
        newCheckIn.setImageUrl(request.imageUrl());
        newCheckIn.setGroup(group);
        newCheckIn.setCreatedAt(LocalDateTime.now());
        newCheckIn.setUser(user);

        return checkInRepository.save(newCheckIn);
    }

    public List<CheckIn> checkInsByGroup(UUID groupId){
    Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found!"));

        return checkInRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
    }
    public List<CheckIn> allCheckIns(User user){

        List<GroupMembership> memberships = groupMembershipRepository.findByUserId(user.getId());
        List<UUID> groupsId = memberships.stream()
                .map(m -> m.getGroup().getId()).toList();

        if (groupsId.isEmpty()){
            return List.of();
        }

        return checkInRepository.findByGroupIdInOrderByCreatedAtDesc(groupsId);
    }

    public void deleteCheckIn(User user, UUID checkInId){
        CheckIn checkin = checkInRepository.findById(checkInId)
                .orElseThrow(() -> new NotFoundException("Check-in not found!"));

        if (!user.getId().equals(checkin.getUser().getId())){
            throw new AccessDeniedException("You can only delete your own check-ins!");
        }

        checkInRepository.deleteById(checkInId);
    }
}
