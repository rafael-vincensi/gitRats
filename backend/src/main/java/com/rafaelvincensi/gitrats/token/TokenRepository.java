package com.rafaelvincensi.gitrats.token;

import com.rafaelvincensi.gitrats.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface TokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findByToken(String token);
    Optional<Token> findByUser(User user);
    void deleteByUser(User user);
}
