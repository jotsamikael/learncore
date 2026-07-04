package com.dodibo.learncore.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    Optional<Token> findByTokenAndPurpose(String token, TokenPurpose purpose);

    @Modifying
    @Transactional
    void deleteByUser_IdAndPurpose(Long userId, TokenPurpose purpose);
}
