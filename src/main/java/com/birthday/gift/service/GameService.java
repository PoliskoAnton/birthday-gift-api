package com.birthday.gift.service;

import com.birthday.gift.model.GameSession;
import com.birthday.gift.model.GameType;
import com.birthday.gift.model.RewardStatus;
import com.birthday.gift.repository.GameSessionRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    private final GameSessionRepository repository;
    private final ApplicationContext applicationContext;

    public GameService(GameSessionRepository repository, ApplicationContext applicationContext) {
        this.repository = repository;
        this.applicationContext = applicationContext;
    }

    // Lazy load to avoid circular dependency
    private void notifyTelegramBot(String sessionId) {
        try {
            var bot = applicationContext.getBean("birthdayBot");
            var method = bot.getClass().getMethod("notifyNewPendingReward", String.class);
            method.invoke(bot, sessionId);
        } catch (Exception e) {
            // Bot might not be configured, log and continue
            System.out.println("Could not notify Telegram bot: " + e.getMessage());
        }
    }

    /**
     * Get or create a game session for the given session ID.
     */
    @Transactional
    public GameSession getOrCreateSession(String sessionId) {
        return repository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    GameSession session = new GameSession(sessionId);
                    return repository.save(session);
                });
    }

    /**
     * Get session progress by session ID.
     */
    public GameSession getProgress(String sessionId) {
        return repository.findBySessionId(sessionId)
                .orElse(null);
    }

    /**
     * Mark a game as completed for the given session.
     */
    @Transactional
    public GameSession completeGame(String sessionId, GameType gameType) {
        GameSession session = getOrCreateSession(sessionId);

        switch (gameType) {
            case TIC_TAC_TOE:
                session.setTicTacToeCompleted(true);
                break;
            case ROCK_PAPER_SCISSORS:
                if (!session.isTicTacToeCompleted()) {
                    throw new IllegalStateException("Must complete Tic-Tac-Toe first");
                }
                session.setRockPaperScissorsCompleted(true);
                break;
            case FIND_THE_GIFT:
                if (!session.isRockPaperScissorsCompleted()) {
                    throw new IllegalStateException("Must complete Rock Paper Scissors first");
                }
                session.setFindTheGiftCompleted(true);
                break;
        }

        // Check if all games are completed and update reward status
        if (session.areAllGamesCompleted() && session.getRewardStatus() == RewardStatus.LOCKED) {
            session.setRewardStatus(RewardStatus.PENDING_CONFIRMATION);
            // Notify admin via Telegram
            notifyTelegramBot(sessionId);
        }

        return repository.save(session);
    }

    /**
     * Get the reward status for a session.
     */
    public RewardStatus getRewardStatus(String sessionId) {
        GameSession session = repository.findBySessionId(sessionId).orElse(null);
        return session != null ? session.getRewardStatus() : RewardStatus.LOCKED;
    }

    /**
     * Confirm the reward (called by admin/Telegram bot).
     */
    @Transactional
    public GameSession confirmReward(String sessionId) {
        GameSession session = repository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        if (session.getRewardStatus() != RewardStatus.PENDING_CONFIRMATION) {
            throw new IllegalStateException("Reward is not in PENDING_CONFIRMATION status");
        }

        session.setRewardStatus(RewardStatus.CONFIRMED);
        session.setRewardConfirmedAt(LocalDateTime.now());

        return repository.save(session);
    }

    /**
     * Get all sessions pending confirmation (for admin dashboard).
     */
    public List<GameSession> getPendingSessions() {
        return repository.findByRewardStatus(RewardStatus.PENDING_CONFIRMATION);
    }

    /**
     * Generate a new unique session ID.
     */
    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
