package com.birthday.gift.telegram;

import com.birthday.gift.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class BirthdayBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(BirthdayBot.class);

    private final String botUsername;
    private final String adminChatId;
    private final GameService gameService;

    public BirthdayBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.admin.chat-id}") String adminChatId,
            GameService gameService) {
        super(botToken);
        this.botUsername = botUsername;
        this.adminChatId = adminChatId;
        this.gameService = gameService;
        log.info("Birthday Bot initialized with username: {}", botUsername);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            // Handle callback queries (button clicks)
            if (update.hasCallbackQuery()) {
                String callbackData = update.getCallbackQuery().getData();
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

                if (callbackData.startsWith("confirm_")) {
                    String sessionId = callbackData.replace("confirm_", "");
                    handleConfirmReward(chatId, sessionId);
                }
                return;
            }

            // Handle text messages
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                String chatId = update.getMessage().getChatId().toString();

                if (messageText.equals("/start")) {
                    sendWelcomeMessage(chatId);
                } else if (messageText.equals("/pending")) {
                    sendPendingRewards(chatId);
                } else if (messageText.equals("/help")) {
                    sendHelpMessage(chatId);
                } else if (messageText.equals("/showbirthday")) {
                    handleShowBirthday(chatId);
                } else if (messageText.equals("/hidebirthday")) {
                    handleHideBirthday(chatId);
                } else if (messageText.equals("/status")) {
                    handleStatusCheck(chatId);
                }
            }
        } catch (Exception e) {
            log.error("Error processing update", e);
        }
    }

    private void sendWelcomeMessage(String chatId) {
        String message = """
            🎂 *Birthday Gift Bot*

            I'll notify you when someone completes all birthday games and needs their reward!

            *Commands:*
            /pending - Show pending rewards
            /help - Show help message

            Your chat ID: `%s`
            """.formatted(chatId);

        sendMessage(chatId, message);
    }

    private void sendHelpMessage(String chatId) {
        String message = """
            🎁 *Birthday Gift Bot Help*

            This bot manages birthday gift rewards.

            *How it works:*
            1. User completes 3 mini-games on the website
            2. You receive a notification here
            3. Send the gift (e.g., 20€) manually
            4. Click "Confirm Sent" button
            5. User sees confirmation on their screen

            *Commands:*
            /pending - Show all pending rewards
            /help - Show this message
            """;

        sendMessage(chatId, message);
    }

    private void sendPendingRewards(String chatId) {
        var pendingSessions = gameService.getPendingSessions();

        if (pendingSessions.isEmpty()) {
            sendMessage(chatId, "✅ No pending rewards at the moment.");
            return;
        }

        StringBuilder sb = new StringBuilder("📋 *Pending Rewards:*\n\n");
        for (var session : pendingSessions) {
            sb.append("• Session: `").append(session.getSessionId().substring(0, 8)).append("...`\n");
            sb.append("  Created: ").append(session.getCreatedAt()).append("\n\n");
        }

        sendMessage(chatId, sb.toString());

        // Send confirm buttons for each
        for (var session : pendingSessions) {
            sendConfirmButton(chatId, session.getSessionId());
        }
    }

    /**
     * Called by GameService when all games are completed
     */
    public void notifyNewPendingReward(String sessionId) {
        if (adminChatId == null || adminChatId.equals("YOUR_CHAT_ID_HERE")) {
            log.warn("Admin chat ID not configured, skipping notification");
            return;
        }

        String message = """
            🎉 *New Reward Pending!*

            Someone completed all birthday games!

            Session: `%s`

            Please send the gift (20€) and confirm below.
            """.formatted(sessionId.substring(0, 8) + "...");

        sendMessage(adminChatId, message);
        sendConfirmButton(adminChatId, sessionId);
    }

    private void sendConfirmButton(String chatId, String sessionId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Confirm reward for session: " + sessionId.substring(0, 8) + "...");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton confirmBtn = new InlineKeyboardButton();
        confirmBtn.setText("✅ Confirm Sent (20€)");
        confirmBtn.setCallbackData("confirm_" + sessionId);
        row.add(confirmBtn);

        rows.add(row);
        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to send confirm button", e);
        }
    }

    private void handleConfirmReward(String chatId, String sessionId) {
        try {
            gameService.confirmReward(sessionId);
            sendMessage(chatId, "✅ *Reward Confirmed!*\n\nSession: `" + sessionId.substring(0, 8) + "...`\n\nThe user will now see the confirmation on their screen.");
        } catch (Exception e) {
            sendMessage(chatId, "❌ Error confirming reward: " + e.getMessage());
        }
    }

    private void handleShowBirthday(String chatId) {
        // Only allow admin to control visibility
        if (!chatId.equals(adminChatId)) {
            sendMessage(chatId, "❌ You are not authorized to use this command.");
            return;
        }
        gameService.setBirthdayVisible(true);
        sendMessage(chatId, "🎂 *Birthday page is now VISIBLE!*\n\nUsers can now access the birthday page.");
    }

    private void handleHideBirthday(String chatId) {
        // Only allow admin to control visibility
        if (!chatId.equals(adminChatId)) {
            sendMessage(chatId, "❌ You are not authorized to use this command.");
            return;
        }
        gameService.setBirthdayVisible(false);
        sendMessage(chatId, "🔒 *Birthday page is now HIDDEN!*\n\nUsers cannot access the birthday page.");
    }

    private void handleStatusCheck(String chatId) {
        boolean visible = gameService.isBirthdayVisible();
        String status = visible ? "🟢 VISIBLE" : "🔴 HIDDEN";
        sendMessage(chatId, "*Birthday Page Status:* " + status + "\n\nUse /showbirthday or /hidebirthday to change.");
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("Markdown");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to {}", chatId, e);
        }
    }
}
