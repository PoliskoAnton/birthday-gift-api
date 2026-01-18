# Birthday Gift Backend (Spring Boot)

REST API backend for the Birthday Gift application.

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+

### Running the Server

```bash
cd birthday-backend

# Using Maven wrapper (if available)
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

The server runs on **http://localhost:8080**

## Deploy to Render

### Option 1: One-Click Deploy
1. Push this folder to a GitHub repository
2. Go to [render.com](https://render.com) and create a new Web Service
3. Connect your GitHub repo
4. Select "Docker" as environment
5. Add environment variables (see below)

### Option 2: Using render.yaml Blueprint
1. Push to GitHub
2. Go to Render Dashboard → New → Blueprint
3. Connect repo and deploy

### Environment Variables (set in Render Dashboard)
| Variable | Description | Example |
|----------|-------------|---------|
| `PORT` | Server port | `8080` |
| `TELEGRAM_BOT_TOKEN` | Telegram bot token | `123456:ABC...` |
| `TELEGRAM_BOT_USERNAME` | Bot username | `mybot` |
| `TELEGRAM_ADMIN_CHAT_ID` | Your Telegram chat ID | `123456789` |
| `CORS_ORIGINS` | Allowed frontend URLs | `https://zhurylo.eu,https://your-app.vercel.app` |

## API Endpoints

### GET /api/health
Health check endpoint.

**Response:**
```json
{
  "status": "UP",
  "service": "Birthday Gift API"
}
```

### GET /api/progress
Get game progress for current session. Creates new session if none exists.

**Headers:**
- `X-Session-Id` (optional): Session identifier

**Response:**
```json
{
  "sessionId": "uuid-string",
  "ticTacToeCompleted": false,
  "rockPaperScissorsCompleted": false,
  "findTheGiftCompleted": false,
  "allGamesCompleted": false,
  "rewardStatus": "LOCKED"
}
```

### POST /api/game/complete
Mark a game as completed.

**Headers:**
- `X-Session-Id`: Session identifier (required)

**Body:**
```json
{
  "gameType": "TIC_TAC_TOE"
}
```

Valid game types: `TIC_TAC_TOE`, `ROCK_PAPER_SCISSORS`, `FIND_THE_GIFT`

### GET /api/reward/status
Get current reward status.

**Headers:**
- `X-Session-Id`: Session identifier (required)

**Response:**
```json
{
  "sessionId": "uuid-string",
  "status": "PENDING_CONFIRMATION",
  "message": "Please wait while your gift is being sent manually"
}
```

### POST /api/reward/confirm
Confirm reward delivery (admin endpoint).

**Headers:**
- `X-Session-Id`: Session identifier (required)

## Reward States

1. **LOCKED** - Games not completed yet
2. **PENDING_CONFIRMATION** - All games completed, waiting for admin to send gift
3. **CONFIRMED** - Gift has been sent and confirmed

## Database

Uses H2 in-memory database. Access console at:
- URL: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:birthdaydb`
- Username: `sa`
- Password: (empty)

## Manual Reward Confirmation Flow

1. User completes all games
2. Reward status changes to `PENDING_CONFIRMATION`
3. Admin (via Telegram bot or dashboard) sees pending reward
4. Admin sends gift manually (e.g., 20€)
5. Admin calls `POST /api/reward/confirm`
6. Frontend polls and displays confirmation
