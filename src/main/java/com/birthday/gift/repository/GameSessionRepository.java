package com.birthday.gift.repository;

import com.birthday.gift.model.GameSession;
import com.birthday.gift.model.RewardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    Optional<GameSession> findBySessionId(String sessionId);

    List<GameSession> findByRewardStatus(RewardStatus status);

    boolean existsBySessionId(String sessionId);
}
