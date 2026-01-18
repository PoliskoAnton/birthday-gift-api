package com.birthday.gift.model;

/**
 * Enum representing the possible states of a reward.
 * LOCKED - Games not completed yet
 * PENDING_CONFIRMATION - All games completed, waiting for admin to send gift
 * CONFIRMED - Gift has been sent and confirmed by admin
 */
public enum RewardStatus {
    LOCKED,
    PENDING_CONFIRMATION,
    CONFIRMED
}
