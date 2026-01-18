package com.birthday.gift.controller.dto;

import com.birthday.gift.model.RewardStatus;

/**
 * DTO for reward status responses.
 */
public class RewardStatusResponse {

    private String sessionId;
    private String status;
    private String message;

    public RewardStatusResponse() {}

    public RewardStatusResponse(String sessionId, RewardStatus status) {
        this.sessionId = sessionId;
        this.status = status.name();
        this.message = getMessageForStatus(status);
    }

    private String getMessageForStatus(RewardStatus status) {
        return switch (status) {
            case LOCKED -> "Complete all games to unlock your reward";
            case PENDING_CONFIRMATION -> "Please wait while your gift is being sent manually";
            case CONFIRMED -> "Gift received! 20€ successfully sent. Happy Birthday!";
        };
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
