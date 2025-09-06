# Mastermind - Reach Apprenticeship Project

Welcome to Mastermind! This is my version of the classic Mastermind game that features both Single and Multiplayer modes. The goal is to guess a secret number combination within 10 attempts, with feedback after every guess. The game features log in, registration, game history, the ability to resume past games, and a queue based match-making system, where users play as a team to solve the secret combination. The backend is built primarily as a Rest API, but switches from HTTP to the Websocket protocol for multiplayer games. 



---

- [🎮 Game Rules](#game-rules)
  - [Example Run](#example-run)
- [🚀 Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation & Setup](#installation--setup)
- [🌀 Game Flow](#game-flow)
- [🏗️ Architecture Overview](#architecture-overview)
  - [Tech Stack](#tech-stack)
  - [Why Java & Spring Boot](#why-java--spring-boot)
  - [💾 Why PostgreSQL](#why-postgresql)
- [🎮 Game Features](#game-features)
  - [Single Player Mode](#single-player-mode)
  - [Multiplayer Mode](#multiplayer-mode)
    
- [🛠️ Backend Routes & Frontend Pages](#backend-routes--frontend-pages)
- [🏛️ Architecture Patterns](#architecture-patterns)
- [📊 Data Models](#data-models)
  - [Player](#player)
  - [SinglePlayerGame](#singleplayergame)
  - [MultiplayerGame](#multiplayergame)
  - [Enums](#enums)
- [🔧 Services & Controllers](#services--controllers)
- [🧪 Testing Strategy](#testing-strategy)
- [🔒 Security Features](#security-features)
- [🚀 Key Achievements](#key-achievements)
- [🔮 Future Enhancements](#future-enhancements)
- [🌱 Personal Growth Milestones](#personal-growth-milestones)


---

## 🎮 Game Rules

- At the start of a game, the computer randomly selects a secret pattern of numbers.
- The length of the pattern depends on the chosen difficulty:
  - **Easy** → 4 digits (0–7)
  - **Medium** → 6 digits (0–8)
  - **Hard** → 9 digits (0–9)
- A player (or team in multiplayer) has **10 attempts** to guess the correct pattern.
- After each guess, feedback is provided:
  - **Correct number** guessed but wrong position
  - **Correct number in the correct position**
  - **Incorrect guess** (no matches)
- Invalid guesses (duplicates, non-numeric input, or wrong length) are rejected and **don’t** count as attempts.
- The game ends with either a **win (correct pattern guessed)** or **loss (attempts used up)**.

### Example Run

Secret: `0 1 3 5`  
- Guess `2 2 4 6` → *All incorrect*  
- Guess `0 2 4 6` → *1 correct number, 1 correct location*  
- Guess `2 2 1 1` → *1 correct number, 0 correct location*  
- Guess `0 1 5 6` → *3 correct numbers, 2 correct location*

> Feedback never reveals **which digits** are correct — only how many are correct.

---


## 🚀 Getting Started

### Prerequisites

- **Java 17+**
- **Node.js 18+**
- **PostgreSQL 12+**
- **Maven 3.6+**

### Installation & Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/danj-jo/mastermind-linkedin.git
cd mastermind-linkedin
### Prerequisites

- **Java 17+**
- **Node.js 18+**
- **PostgreSQL 12+**
- **Maven 3.6+**

### Installation & Setup

#### 1. Clone the Repository
```bash
git clone (https://github.com/danj-jo/mastermind-linkedin.git)
cd mastermind-linkedin
```

#### 2. Database Setup
```bash
# Create PostgreSQL database
createdb mastermind_db

# Update database credentials in src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mastermind_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

#### 3. Backend Setup
```bash
# Install dependencies and run tests
mvn clean install

# Start the Spring Boot application
mvn spring-boot:run or if you have intellij, there is a run button that comes with every spring boot project.
```
The backend will be available at `http://localhost:8080`

#### 4. Frontend Setup
```bash
# Navigate to frontend directory
cd frontend/mastermind-frontend

# Install dependencies
npm install

# Start development server
npm run dev
```
The frontend will be available at `http://localhost:5173`

---

## 🌀 Game Flow

## 1. User Registration Flow

```mermaid
sequenceDiagram
    participant Frontend
    participant AuthController
    participant AuthService
    participant PlayerRepository
    participant GlobalExceptionHandler

    Frontend->>AuthController: POST /api/auth/register
    AuthController->>AuthService: registerNewUser(newUser)
    AuthService->>AuthService: validateUsername(username)
    AuthService->>AuthService: validatePassword(password)
    AuthService->>PlayerRepository: existsByUsername(username)
    PlayerRepository-->>AuthService: false
    AuthService->>PlayerRepository: existsByEmail(email)
    PlayerRepository-->>AuthService: false
    AuthService->>AuthService: encodePassword(password)
    AuthService->>PlayerRepository: saveAndFlush(newPlayer)
    PlayerRepository-->>AuthService: saved
    AuthService-->>AuthController: success
    AuthController-->>Frontend: 200 OK "Success"
```

## 2. User Authentication Flow

```mermaid
sequenceDiagram
    participant Frontend
    participant AuthContext
    participant AuthController
    participant SpringSecurity
    participant MyUserDetailsService
    participant PlayerRepository

    Frontend->>AuthContext: useEffect check auth
    AuthContext->>AuthController: GET /api/auth
    AuthController->>SpringSecurity: getAuthentication()
    SpringSecurity->>MyUserDetailsService: loadUserByUsername(username)
    MyUserDetailsService->>PlayerRepository: findByUsername(username)
    PlayerRepository-->>MyUserDetailsService: Player
    MyUserDetailsService-->>SpringSecurity: UserDetails
    SpringSecurity-->>AuthController: Authentication
    AuthController-->>AuthContext: 200 OK {username}
    AuthContext-->>Frontend: setIsLoggedIn(true)
```

## 3. Single Player Game Creation

```mermaid
sequenceDiagram
    participant Frontend
    participant SinglePlayerGameController
    participant AuthService
    participant SingleplayerGameService
    participant PlayerRepository
    participant SingleplayerGameRepository
    participant GameUtils

    Frontend->>SinglePlayerGameController: POST /singleplayer/games/new
    SinglePlayerGameController->>AuthService: getCurrentAuthenticatedPlayerUsername()
    AuthService-->>SinglePlayerGameController: username
    SinglePlayerGameController->>SingleplayerGameService: createNewGame(difficulty, playerId)
    SingleplayerGameService->>PlayerRepository: findById(playerId)
    PlayerRepository-->>SingleplayerGameService: Player
    SingleplayerGameService->>GameUtils: selectUserDifficulty(difficulty)
    GameUtils-->>SingleplayerGameService: Difficulty enum
    SingleplayerGameService->>GameUtils: generateWinningNumber(difficulty)
    GameUtils-->>SingleplayerGameService: winningNumber
    SingleplayerGameService->>SingleplayerGameRepository: saveAndFlush(game)
    SingleplayerGameRepository-->>SingleplayerGameService: saved
    SingleplayerGameService-->>SinglePlayerGameController: SinglePlayerGame
    SinglePlayerGameController-->>Frontend: 200 OK {numbersToGuess}
```

## 4. Single Player Guess Submission

```mermaid
sequenceDiagram
    participant Frontend
    participant SinglePlayerGameController
    participant SingleplayerGameService
    participant SingleplayerGameRepository
    participant SinglePlayerGame

    Frontend->>SinglePlayerGameController: POST /singleplayer/games/{gameId}/guess
    SinglePlayerGameController->>SingleplayerGameService: submitGuess(gameId, guess)
    SingleplayerGameService->>SingleplayerGameRepository: findById(gameId)
    SingleplayerGameRepository-->>SingleplayerGameService: SinglePlayerGame
    SingleplayerGameService->>SinglePlayerGame: submitGuess(guess)
    SinglePlayerGame->>SinglePlayerGame: validateGuess()
    SinglePlayerGame->>SinglePlayerGame: calculateFeedback()
    SinglePlayerGame->>SinglePlayerGame: addGuessToList()
    SinglePlayerGame-->>SingleplayerGameService: feedback
    SingleplayerGameService->>SingleplayerGameRepository: saveAndFlush(game)
    SingleplayerGameRepository-->>SingleplayerGameService: saved
    SingleplayerGameService-->>SinglePlayerGameController: feedback
    SinglePlayerGameController-->>Frontend: 200 OK {feedback}
```

## 5. Multiplayer Game Matchmaking

```mermaid
sequenceDiagram
    participant Frontend
    participant MultiplayerGameController
    participant AuthService
    participant MultiplayerGameService
    participant EmitterRegistry
    participant SseEmitter

    Frontend->>MultiplayerGameController: GET /multiplayer/join?difficulty=EASY
    MultiplayerGameController->>AuthService: getCurrentAuthenticatedPlayer()
    AuthService-->>MultiplayerGameController: Player
    MultiplayerGameController->>EmitterRegistry: addEmitter(playerId, emitter)
    MultiplayerGameController->>SseEmitter: send("ping", "ready")
    SseEmitter-->>Frontend: SSE: ping event
    MultiplayerGameController->>MultiplayerGameService: joinMultiplayerGame(player, difficulty)
    MultiplayerGameService->>MultiplayerGameService: addToQueue(player, difficulty)
    
    Note over MultiplayerGameService: When 2 players in queue
    MultiplayerGameService->>MultiplayerGameService: createMultiplayerGame(player1, player2)
    MultiplayerGameService->>EmitterRegistry: getEmitter(player1Id)
    MultiplayerGameService->>SseEmitter: send("matched", gameId)
    SseEmitter-->>Frontend: SSE: matched event
    MultiplayerGameService->>EmitterRegistry: getEmitter(player2Id)
    MultiplayerGameService->>SseEmitter: send("matched", gameId)
    SseEmitter-->>Frontend: SSE: matched event
```

## 6. Multiplayer Game Guess via WebSocket

```mermaid
sequenceDiagram
    participant Frontend
    participant MultiplayerWebsocketController
    participant PlayerService
    participant MultiplayerGameService
    participant MultiplayerGame
    participant WebSocketConfig

    Frontend->>WebSocketConfig: WebSocket connection to /ws
    WebSocketConfig-->>Frontend: WebSocket established
    
    Frontend->>MultiplayerWebsocketController: STOMP /app/multiplayer/{gameId}/guess
    MultiplayerWebsocketController->>PlayerService: findPlayerByUsername(username)
    PlayerService-->>MultiplayerWebsocketController: Player
    MultiplayerWebsocketController->>MultiplayerGameService: activeGames.get(gameId)
    MultiplayerGameService-->>MultiplayerWebsocketController: MultiplayerGame
    MultiplayerWebsocketController->>MultiplayerGame: submitGuess(player, guess)
    MultiplayerGame->>MultiplayerGame: validateGuess()
    MultiplayerGame->>MultiplayerGame: calculateFeedback()
    MultiplayerGame->>MultiplayerGame: addGuessToList()
    MultiplayerGame-->>MultiplayerWebsocketController: feedback
    MultiplayerWebsocketController->>WebSocketConfig: @SendTo("/topic/mp")
    WebSocketConfig-->>Frontend: STOMP /topic/mp: MultiplayerTurnMetadata
```

## 7. Error Handling Flow

```mermaid
sequenceDiagram
    participant Frontend
    participant Controller
    participant Service
    participant GlobalExceptionHandler
    participant Frontend

    Frontend->>Controller: HTTP Request
    Controller->>Service: business logic
    Service-->>Controller: throws CustomException
    Controller-->>GlobalExceptionHandler: exception bubbles up
    GlobalExceptionHandler->>GlobalExceptionHandler: @ExceptionHandler(CustomException.class)
    GlobalExceptionHandler->>GlobalExceptionHandler: determine HTTP status
    GlobalExceptionHandler->>GlobalExceptionHandler: create ErrorResponseDTO
    GlobalExceptionHandler-->>Frontend: ResponseEntity with error details
```

## 8. Player Profile Retrieval

```mermaid
sequenceDiagram
    participant Frontend
    participant PlayerController
    participant AuthService
    participant PlayerService
    participant PlayerRepository

    Frontend->>PlayerController: GET /me/
    PlayerController->>AuthService: getCurrentAuthenticatedPlayerUsername()
    AuthService-->>PlayerController: username
    PlayerController->>PlayerService: findPlayerByUsername(username)
    PlayerService->>PlayerRepository: findByUsername(username)
    PlayerRepository-->>PlayerService: Player
    PlayerService-->>PlayerController: Player
    PlayerController->>PlayerController: create UserProfileDao
    PlayerController-->>Frontend: 200 OK UserProfileDao
```

## 🏗️ Architecture Overview

### Tech Stack
- **Backend**: Java 17 with Spring Boot 3.5.5
- **Database**: PostgreSQL using JDBC and JPA for persistence
- **API Layer**: RESTful endpoints for core game logic, user management, and singleplayer mode
- **Real-Time Communication**: WebSockets with STOMP protocol for multiplayer messaging and event broadcasting
- **Frontend Layer**: React with TypeScript, handles UI and user interactions
- **Build Tools**: Maven (backend) and Vite (frontend) for fast, modular builds
- **Testing**: JUnit 5 and Mockito for unit and integration testing

### WebSocket Architecture
- **STOMP Protocol**: Simple messaging abstraction over WebSockets
- **Server-Sent Events**: Real-time game state updates
- **Queue Management**: Thread-safe matchmaking system

### Why Java & Spring Boot?

After my previous REACH project using JavaScript + MongoDB, I chose Java & Spring Boot for several key reasons:

1. **Static Typing**: Java's compile-time type checking prevents runtime errors and enhances code readability
2. **Robust Framework**: Spring Boot provides excellent support for WebSockets, authentication, authorization, and CORS
3. **Enterprise-Grade**: Built-in security features like CSRF protection and role-based authentication
4. **JPA**: JPA allowed me to make queries faster, to put more energy into core game logic. When complex custom queries were needed, I could rely on JPQL. 

### 💾 Why PostgreSQL?

The choice of **PostgreSQL** was driven by the need to manage **complex relationships** between players, games, and guesses:

- **Player ↔ Game Relationships**: Each player can participate in multiple games, and each game can include one or more players (especially in multiplayer mode).  
- **Game ↔ Guesses**: Each game maintains a list of guesses made by players, which allows structured queries and game resumption.  
- **Data Integrity**: Foreign keys and transactions ensure relationships remain consistent.  
- **Efficient Queries**: Indexing and relational tables allow fast retrieval of game history, leaderboard stats, and player performance.  
- **Future Flexibility**: Makes implementing features like leaderboards, friends, or analytics straightforward.

### Backend Routes & Frontend Pages

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/api/auth` | Log in |
| POST   | `/api/auth/register` | Register |

### Multiplayer
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/multiplayer/join` | Join a new multiplayer game |
| GET    | `/multiplayer/{gameID}` | Get multiplayer game details |
| WS     | `/multiplayer/{gameID}/guess` | Send guess (WebSocket) |

### Singleplayer
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/singleplayer/games/new` | Start a new singleplayer game |
| GET    | `/singleplayer/games/{gameID}` | Get singleplayer game details |
| POST   | `/singleplayer/{gameID}/guess` | Submit guess |

### User Profile
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/me` | Current user info (username, email, stats) |
| GET    | `/me/games` | Games belonging to the current user |

---

## 🌐 Frontend Pages

| Route | Description |
|-------|-------------|
| `/home` | Home page / new game menu |
| `/login` | Login page |
| `/register` | Registration page |
| `/me` | User profile page |
| `/lobby` | Multiplayer waiting room |
| `/team` | Active multiplayer game |
| `/game/{gameID}` | Singleplayer or multiplayer session page |


## 📊 Data Models

### Core Entities

#### Player
```java
@Entity
public class Player implements UserDetails {
    private UUID playerId;
    private String username;
    private String email;
    private String password;
    private String role; // ROLE_USER, ROLE_ADMIN
}
```
- Implements Spring Security's `UserDetails` for authentication
- Lean design focused on user representation
- Role-based access control integration

#### SinglePlayerGame
```java
@Entity
public class SinglePlayerGame {
    private UUID gameId;
    private Player player;
    private String winningNumber;
    private Difficulty difficulty;
    private List<String> guesses;
    private Result result;
    private boolean isFinished;
    
    public String submitGuess(String guess) {
        // Core game logic with validation
    }
}
```
- Separate guesses table for optimal query performance
- Built-in game logic with state management
- Resume capability with game persistence

#### MultiplayerGame
```java
@Entity
public class MultiplayerGame {
    private UUID gameId;
    private Player player1;
    private Player player2;
    private String winningNumber;
    private List<MultiplayerGuess> guesses;
    private Result result;
    private boolean isFinished;
}
```
- Two-player game structure
- Separate `MultiplayerGuess` entities for player identification
- Memory-based storage for active games
###Implementation
```java
// Thread-safe queue management
private Map<String, ConcurrentLinkedQueue<Player>> difficultyQueues;

// Emitter management for real-time updates
private Map<String, SseEmitter> activeEmitters;

// WebsocketEventListeners & Channel Interceptors to remove orphaned games; 
```
#### Enums
```java
public enum Difficulty { EASY, MEDIUM, HARD }
public enum Result { PENDING, WIN, LOSS }
public enum GameMode { SINGLE_PLAYER, MULTIPLAYER }
```

## 🔧 Services & Controllers

### Service Layer
- **AuthService**: User registration and authentication
- **PlayerService**: Player data management and retrieval
- **SingleplayerGameService**: Single-player game logic
- **MultiplayerGameService**: Multiplayer game management and matchmaking

### Controller Layer
- **AuthController**: Registration and authentication endpoints
- **PlayerController**: User profile and game history
- **SinglePlayerGameController**: Single-player game operations
- **MultiplayerGameController**: Multiplayer game management
- **MultiplayerWebsocketController**: Real-time WebSocket communication


### Exception Handling
custom exceptions with a global exception handler
examples:
- `GameNotFoundException`
- `PlayerNotFoundException`
- `UnauthorizedGameAccessException`
- `GameCreationException`

## 🔮 Future Enhancements

- **Friends System**: Add social features for multiplayer
- **Leaderboards**: Global and personal statistics

## 🌱 Personal Growth Milestones

This project represents significant growth from my previous REACH submission. I explored and applied several new concepts, including:

- **Relational Databases** – Modeling and querying entity relationships to support multiplayer game logic
- **WebSocket Communication** – Building real-time matchmaking and event dispatch using STOMP and SSE
- **Security Fundamentals** – Implementing CSRF protection, SQL injection prevention, and full authentication/authorization flows
- **Thread Safety** – Managing concurrent access with thread-safe structures like ConcurrentHashMap
- **STOMP Protocol** – Intercepting and handling lifecycle events (CONNECT, SEND, DISCONNECT) for session tracking
- **Server-Sent Events** – Using SSE to notify clients and coordinate multiplayer state transitions

The increased complexity pushed me to adopt proper architectural patterns, improve lifecycle visibility, and design for maintainability—skills. I was also able to see the vision come to life, with the creation of the front end. 

---
