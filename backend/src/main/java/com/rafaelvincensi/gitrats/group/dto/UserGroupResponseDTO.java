package com.rafaelvincensi.gitrats.group.dto;

import com.rafaelvincensi.gitrats.group.GroupMembership;
import com.rafaelvincensi.gitrats.group.GroupRole;

import java.util.UUID;

public record UserGroupResponseDTO(
        UUID id,
        String name,
        String code,
        String avatarUrl,
        GroupRole role,
        Long memberCount
){
    public static UserGroupResponseDTO fromEntity(GroupMembership membership, Long memberCount){
        return new UserGroupResponseDTO(
                membership.getGroup().getId(),
                membership.getGroup().getName(),
                membership.getGroup().getCode(),
                membership.getGroup().getAvatarUrl(),
                membership.getRole(),
                memberCount
        );
    }
}
