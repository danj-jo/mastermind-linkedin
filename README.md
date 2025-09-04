# 🎯 Mastermind Game - Reach Apprenticeship Project 

A real-time multiplayer Mastermind game built with Java & Spring Boot on the backend, PostgreSQL for the perstitence layer, and React on the frontend. It features WebSocket communication, server sent events, authentication via Spring Security. The goal of the game is to guess a four digit number between 0-7 (Easy Mode) within 10 tries. 

## 🚀 How to get started 

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

#### 5. Production Build
```bash
# Backend
mvn clean package
java -jar target/mastermind-linkedin-0.0.1-SNAPSHOT.jar

# Frontend
cd frontend/mastermind-frontend
npm run build
npm start
```

## 🏗️ Architecture Overview

### Tech Stack
- **Backend**: Java 17 & Spring Boot 3.5.5
- **Database**: PostgreSQL with JDBC & JPA
- **Real-time Communication**: WebSockets with STOMP protocol
- **Frontend**: React 19 with TypeScript & Tailwind CSS
- **Build Tools**: Maven (Backend), Vite (Frontend)
- **Testing**: JUnit 5 & Mockito

### Why This Tech Stack?

After my previous REACH project using JavaScript + MongoDB, I chose Java & Spring Boot for several key reasons:

1. **Static Typing**: Java's compile-time type checking prevents runtime errors and enhances code readability
2. **Robust Framework**: Spring Boot provides excellent support for WebSockets, authentication, authorization, and CORS
3. **Enterprise-Grade**: Built-in security features like CSRF protection and role-based authentication
4. **Relational Database**: PostgreSQL's relationship management was crucial for tracking player-game associations

## 🎮 Game Features

### Single Player Mode
- **Difficulty Levels**: Easy (4 digits), Medium (6 digits), Hard (8 digits)
- **Game Persistence**: Save and resume games
- **Smart Hints**: Automatic feedback on guess accuracy
- **Game History**: Track past games and performance

### Multiplayer Mode
- **Real-time Matchmaking**: Queue-based system with difficulty-specific rooms
- **Live Updates**: WebSocket communication for instant game state updates
- **Turn-based Gameplay**: Structured guessing with player identification
- **Memory Management**: Games stored in memory until completion

## 🏛️ Architecture Patterns

### MVC Pattern Implementation
- **Model**: JPA entities representing game state and user data
- **View**: React components with responsive UI
- **Controller**: RESTful endpoints with WebSocket support

### Layered Architecture
```
┌─────────────────┐
│   Controllers   │ ← REST API & WebSocket endpoints
├─────────────────┤
│    Services     │ ← Business logic layer
├─────────────────┤
│  Repositories   │ ← Data access layer
├─────────────────┤
│   Database      │ ← PostgreSQL persistence
└─────────────────┘
```

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
  

## 🌐 Real-time Communication

### WebSocket Architecture
- **STOMP Protocol**: Simple messaging abstraction over WebSockets
- **Server-Sent Events**: Real-time game state updates
- **Queue Management**: Thread-safe matchmaking system

### Multiplayer Implementation
```java
// Thread-safe queue management
private Map<String, ConcurrentLinkedQueue<Player>> difficultyQueues;

// Emitter management for real-time updates
private Map<String, SseEmitter> activeEmitters;
```

**Key Features:**
- **Concurrent Data Structures**: `ConcurrentHashMap` and `ConcurrentLinkedQueue` for thread safety
- **Early Emitter Registration**: Prevents connection timing issues
- **Difficulty-based Queues**: Separate matchmaking for each difficulty level

## 🧪 Testing Strategy

- **Controller Tests**: API endpoint validation
- **Service Tests**: Business logic verification
- **Entity Tests**: Model behavior testing
- **Repository Tests**: Data persistence validation
- **Exception Tests**: Error handling verification
- **Integration Tests**: End-to-end functionality

### Testing Technologies
- **JUnit 5**: Modern testing framework
- **Mockito**: Mocking and stubbing
- **Spring Boot Test**: Integration testing support
- **H2 Database**: In-memory testing database

## 🔒 Security Features

- **Spring Security**: Authentication and authorization
- **CSRF Protection**: Cross-site request forgery prevention
- **Role-based Access**: User permission management
- **Password Encryption**: Secure credential storage
- **CORS Configuration**: Cross-origin request handling

## 📁 Project Structure

```
mastermind-linkedin/
├── src/main/java/com/example/mastermind/
│   ├── config/                 # Security and WebSocket configuration
│   ├── controllers/            # REST API and WebSocket controllers
│   ├── customExceptions/       # Custom exception definitions
│   ├── dataAccessObjects/      # JPA repositories
│   ├── dataTransferObjects/    # Request/Response DTOs
│   ├── models/                 # Entities and enums
│   ├── services/               # Business logic layer
│   └── utils/                  # Utility classes
├── frontend/mastermind-frontend/
│   ├── app/                    # React application
│   │   ├── components/         # Reusable UI components
│   │   ├── routes/             # Page components
│   │   └── AuthContext.tsx     # Authentication context
│   └── public/                 # Static assets
└── src/test/                   # Comprehensive test suite
```

## 🚀 Key Achievements

1. **Real-time Multiplayer**: Implemented WebSocket-based matchmaking system & Server Sent Events to ping users upon match
2. **Robust Architecture**: Clean separation of concerns with layered design
3. **Security Implementation**: Full authentication and authorization system
4. **Performance Optimization**: Efficient database queries and memory management

## 🔮 Future Enhancements

- **Friends System**: Add social features for multiplayer
- **Leaderboards**: Global and personal statistics

## 📝 Development Journey

This project represents significant growth from my previous REACH submission. Moving from JavaScript/MongoDB to Java/PostgreSQL required learning:

- **Static Typing**: Embracing compile-time safety
- **Relational Databases**: Understanding entity relationships
- **WebSocket Communication**: Real-time application development
- **Spring Security**: Enterprise-grade authentication
- **Thread Safety**: Concurrent programming concepts

The increased complexity forced me to implement robust error handling, comprehensive testing, and proper architectural patterns - skills essential for enterprise development.

---
