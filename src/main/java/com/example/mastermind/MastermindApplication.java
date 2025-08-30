package com.example.mastermind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

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
