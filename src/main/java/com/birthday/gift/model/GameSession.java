package com.birthday.gift.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a user's game session and progress.
 * Tracks which games have been completed and the reward status.
 */
@Entity
@Table(name = "game_sessions")
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @Column(nullable = false)
    private boolean ticTacToeCompleted = false;

    @Column(nullable = false)
    private boolean rockPaperScissorsCompleted = false;

    @Column(nullable = false)
    private boolean findTheGiftCompleted = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardStatus rewardStatus = RewardStatus.LOCKED;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime rewardConfirmedAt;

    // Constructors
    public GameSession() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public GameSession(String sessionId) {
        this();
        this.sessionId = sessionId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isTicTacToeCompleted() {
        return ticTacToeCompleted;
    }

    public void setTicTacToeCompleted(boolean ticTacToeCompleted) {
        this.ticTacToeCompleted = ticTacToeCompleted;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isRockPaperScissorsCompleted() {
        return rockPaperScissorsCompleted;
    }

    public void setRockPaperScissorsCompleted(boolean rockPaperScissorsCompleted) {
        this.rockPaperScissorsCompleted = rockPaperScissorsCompleted;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isFindTheGiftCompleted() {
        return findTheGiftCompleted;
    }

    public void setFindTheGiftCompleted(boolean findTheGiftCompleted) {
        this.findTheGiftCompleted = findTheGiftCompleted;
        this.updatedAt = LocalDateTime.now();
    }

    public RewardStatus getRewardStatus() {
        return rewardStatus;
    }

    public void setRewardStatus(RewardStatus rewardStatus) {
        this.rewardStatus = rewardStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getRewardConfirmedAt() {
        return rewardConfirmedAt;
    }

    public void setRewardConfirmedAt(LocalDateTime rewardConfirmedAt) {
        this.rewardConfirmedAt = rewardConfirmedAt;
    }

    /**
     * Check if all games are completed.
     */
    public boolean areAllGamesCompleted() {
        return ticTacToeCompleted && rockPaperScissorsCompleted && findTheGiftCompleted;
    }
}
