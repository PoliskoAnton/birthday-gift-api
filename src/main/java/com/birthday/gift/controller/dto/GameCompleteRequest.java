package com.birthday.gift.controller.dto;

import com.birthday.gift.model.GameType;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for game completion requests.
 */
public class GameCompleteRequest {

    @NotNull(message = "Game type is required")
    private GameType gameType;

    public GameCompleteRequest() {}

    public GameCompleteRequest(GameType gameType) {
        this.gameType = gameType;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }
}
