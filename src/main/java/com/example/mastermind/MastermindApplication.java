package com.example.mastermind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

/**
 * Main Spring Boot application class for the Mastermind game.
 * 
 * Upon startup, this application automatically creates log files in the logs/ directory.
 * Logging is configured to provide comprehensive application monitoring, including:
 * - Authentication attempts and security events
 * - Game creation, gameplay, and completion events
 * - Database operations and data access patterns
 * - Error tracking and debugging information
 * - Performance metrics and application health
 * - Emitter failures and WebSocket connection issues
 * 
 * The log files are essential for:
 * - Debugging production issues
 * - Monitoring application performance
 * - Tracking user activity and game statistics
 * - Security auditing and compliance
 * - Troubleshooting authentication and authorization problems
 * - Diagnosing real-time communication failures in multiplayer games

 */
@SpringBootApplication
public class MastermindApplication {

	public static void main(String[] args) {
        File logDir = new File("logs");
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            if (created) {
                System.out.println("Created logs directory");
            }
        }
        SpringApplication.run(MastermindApplication.class, args);
	}

}
