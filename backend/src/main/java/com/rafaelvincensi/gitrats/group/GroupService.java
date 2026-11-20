package com.rafaelvincensi.gitrats.group;

import com.rafaelvincensi.gitrats.config.cloudinary.UploadService;
import com.rafaelvincensi.gitrats.exception.ConflictException;
import com.rafaelvincensi.gitrats.exception.FileUploadException;
import com.rafaelvincensi.gitrats.exception.GroupMaxCapacityException;
import com.rafaelvincensi.gitrats.exception.NotFoundException;
import com.rafaelvincensi.gitrats.group.dto.CreateGroupDTO;
import com.rafaelvincensi.gitrats.group.dto.UpdateGroupDTO;
import com.rafaelvincensi.gitrats.group.dto.UserGroupResponseDTO;
import com.rafaelvincensi.gitrats.rank.LeaderboardService;
import com.rafaelvincensi.gitrats.rank.dto.GroupLeaderboardDTO;
import com.rafaelvincensi.gitrats.user.User;
import com.rafaelvincensi.gitrats.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final UserService userService;
    private final UploadService uploadService;
    private final LeaderboardService leaderboardService;

    public GroupService(GroupRepository groupRepository, GroupMembershipRepository groupMembershipRepository, UserService userService, UploadService uploadService, LeaderboardService leaderboardService) {
        this.groupRepository = groupRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.userService = userService;
        this.uploadService = uploadService;
        this.leaderboardService = leaderboardService;
    }

    @Transactional
    public Group createGroup(CreateGroupDTO request, User userFounder){

        String invitationCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Group group = new Group();
        group.setName(request.name());
        group.setDescription(request.description());
        group.setCode(invitationCode);
        group.setCreatedAt(LocalDateTime.now());

        Group savedGroup = groupRepository.save(group);

        GroupMembership membership = new GroupMembership();

        membership.setUser(userFounder);
        membership.setGroup(savedGroup);
        membership.setRole(GroupRole.FOUNDER);

        groupMembershipRepository.save(membership);

        return savedGroup;
    }

    public GroupMembership joinGroup(String code, User user){

        Group group = groupRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Group not found!"));

        boolean inGroup = groupMembershipRepository.existsByGroupIdAndUserId(group.getId(), user.getId());

        if (inGroup){
            throw new ConflictException("You already a member of this group!");
        }

        long groupMembersCount = groupMembershipRepository.countByGroupId(group.getId());

        if (groupMembersCount >= 20){
            throw new GroupMaxCapacityException("Group already has 20 members!");
        }

        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setRole(GroupRole.MEMBER);
        membership.setUser(user);
        membership.setJoinedAt(LocalDateTime.now());

        return groupMembershipRepository.save(membership);
    }

    public Group updateGroup(UUID groupId, User user, UpdateGroupDTO request, MultipartFile file){
        var group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found!"));

        boolean isFounder = groupMembershipRepository.existsByGroupIdAndUserIdAndRole(group.getId(), user.getId(), GroupRole.FOUNDER);

        if (!isFounder) {
            throw new AccessDeniedException("Only the group founder can perform this action!");
        }

        if (file != null && !file.isEmpty()){
            try {
            byte[] imageBytes = file.getBytes();
            String newImageGroup = uploadService.uploadFile(imageBytes, "groups");
            group.setAvatarUrl(newImageGroup);
        } catch (IOException e){
                throw new FileUploadException("Failed to read image bytes: " + e.getMessage());
            }
        }

        if (request.name() != null && !request.name().isBlank()) group.setName(request.name());
        if (request.description() != null && !request.description().isBlank()) group.setDescription(request.description());

        return groupRepository.save(group);
    }

    public void exitGroup(User user, UUID groupId){
        var membership = groupMembershipRepository.findByGroupIdAndUserId(groupId, user.getId())
                .orElseThrow(() -> new NotFoundException("You are not a member of this group!"));

        if (membership.getRole() == GroupRole.FOUNDER){
            throw new AccessDeniedException("The founder cannot exit the group! Delete group instead.");
        }

        groupMembershipRepository.delete(membership);
    }

    public List<UserGroupResponseDTO> listMyGroups(User user){
        var memberships = groupMembershipRepository.findByUserId(user.getId());

        return memberships.stream()
                .map(membership -> {
                    UUID groupId = membership.getGroup().getId();
                    Long totalMembers = groupMembershipRepository.countByGroupId(groupId);

                    long totalCheckIns = leaderboardService.getGroupCheckInLeaderboard(groupId).stream()
                            .mapToLong(GroupLeaderboardDTO::score)
                            .sum();

                    long totalCommits = leaderboardService.getGroupCommitLeaderboard(groupId).stream()
                            .mapToLong(GroupLeaderboardDTO::score)
                            .sum();

                    return UserGroupResponseDTO.fromEntity(membership, totalMembers, totalCheckIns, totalCommits);
                })
                .toList();
    }

    @Transactional
    public void deleteGroup(UUID userId, UUID groupId){
        var user = userService.findById(userId);
        var group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found!"));

        boolean isFounder = groupMembershipRepository.existsByGroupIdAndUserIdAndRole(groupId, userId, GroupRole.FOUNDER);

        if (!isFounder){
            throw new AccessDeniedException("Only the Founder can delete the group!");
        }

        groupMembershipRepository.deleteByGroupId(groupId);
        groupRepository.deleteById(groupId);
    }
}
