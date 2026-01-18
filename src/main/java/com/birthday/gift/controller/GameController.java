package com.birthday.gift.controller;

import com.birthday.gift.controller.dto.GameCompleteRequest;
import com.birthday.gift.controller.dto.ProgressResponse;
import com.birthday.gift.controller.dto.RewardStatusResponse;
import com.birthday.gift.model.GameSession;
import com.birthday.gift.model.RewardStatus;
import com.birthday.gift.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:5173", "http://localhost:3000"})
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Get game progress for a session.
     * Creates a new session if one doesn't exist.
     */
    @GetMapping("/progress")
    public ResponseEntity<ProgressResponse> getProgress(
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {

        if (sessionId == null || sessionId.isEmpty()) {
            // Generate a new session ID
            sessionId = gameService.generateSessionId();
            GameSession session = gameService.getOrCreateSession(sessionId);
            return ResponseEntity.ok(ProgressResponse.fromSession(session));
        }

        GameSession session = gameService.getProgress(sessionId);
        if (session == null) {
            session = gameService.getOrCreateSession(sessionId);
        }

        return ResponseEntity.ok(ProgressResponse.fromSession(session));
    }

    /**
     * Mark a game as completed.
     */
    @PostMapping("/game/complete")
    public ResponseEntity<?> completeGame(
            @RequestHeader("X-Session-Id") String sessionId,
            @Valid @RequestBody GameCompleteRequest request) {

        try {
            GameSession session = gameService.completeGame(sessionId, request.getGameType());
            return ResponseEntity.ok(ProgressResponse.fromSession(session));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Get reward status for a session.
     */
    @GetMapping("/reward/status")
    public ResponseEntity<RewardStatusResponse> getRewardStatus(
            @RequestHeader("X-Session-Id") String sessionId) {

        RewardStatus status = gameService.getRewardStatus(sessionId);
        return ResponseEntity.ok(new RewardStatusResponse(sessionId, status));
    }

    /**
     * Confirm reward (called by admin/Telegram bot).
     * This endpoint would typically be protected in production.
     */
    @PostMapping("/reward/confirm")
    public ResponseEntity<?> confirmReward(
            @RequestHeader("X-Session-Id") String sessionId) {

        try {
            GameSession session = gameService.confirmReward(sessionId);
            return ResponseEntity.ok(new RewardStatusResponse(
                    sessionId,
                    session.getRewardStatus()
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Birthday Gift API"
        ));
    }
}
