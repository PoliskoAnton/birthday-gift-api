package com.birthday.gift.controller.dto;

import com.birthday.gift.model.GameSession;
import com.birthday.gift.model.RewardStatus;

/**
 * DTO for returning game progress to the frontend.
 */
public class ProgressResponse {

    private String sessionId;
    private boolean ticTacToeCompleted;
    private boolean rockPaperScissorsCompleted;
    private boolean findTheGiftCompleted;
    private boolean allGamesCompleted;
    private String rewardStatus;

    public ProgressResponse() {}

    public static ProgressResponse fromSession(GameSession session) {
        ProgressResponse response = new ProgressResponse();
        response.setSessionId(session.getSessionId());
        response.setTicTacToeCompleted(session.isTicTacToeCompleted());
        response.setRockPaperScissorsCompleted(session.isRockPaperScissorsCompleted());
        response.setFindTheGiftCompleted(session.isFindTheGiftCompleted());
        response.setAllGamesCompleted(session.areAllGamesCompleted());
        response.setRewardStatus(session.getRewardStatus().name());
        return response;
    }

    public static ProgressResponse empty(String sessionId) {
        ProgressResponse response = new ProgressResponse();
        response.setSessionId(sessionId);
        response.setTicTacToeCompleted(false);
        response.setRockPaperScissorsCompleted(false);
        response.setFindTheGiftCompleted(false);
        response.setAllGamesCompleted(false);
        response.setRewardStatus(RewardStatus.LOCKED.name());
        return response;
    }

    // Getters and Setters
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
    }

    public boolean isRockPaperScissorsCompleted() {
        return rockPaperScissorsCompleted;
    }

    public void setRockPaperScissorsCompleted(boolean rockPaperScissorsCompleted) {
        this.rockPaperScissorsCompleted = rockPaperScissorsCompleted;
    }

    public boolean isFindTheGiftCompleted() {
        return findTheGiftCompleted;
    }

    public void setFindTheGiftCompleted(boolean findTheGiftCompleted) {
        this.findTheGiftCompleted = findTheGiftCompleted;
    }

    public boolean isAllGamesCompleted() {
        return allGamesCompleted;
    }

    public void setAllGamesCompleted(boolean allGamesCompleted) {
        this.allGamesCompleted = allGamesCompleted;
    }

    public String getRewardStatus() {
        return rewardStatus;
    }

    public void setRewardStatus(String rewardStatus) {
        this.rewardStatus = rewardStatus;
    }
}
