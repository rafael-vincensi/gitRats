package com.rafaelvincensi.gitrats.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String username;
    private String name;

    @Column(unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "created_at")
    private Instant createdAt;

   @Column(name = "avatar_url")
    private String avatarUrl;
    private String bio;
    private String timezone;

    @Column(name = "github_id" , nullable = true)
    private String githubId;

    @Column(nullable = true)
    private String githubUsername;
}
