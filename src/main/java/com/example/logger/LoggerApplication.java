package com.example.logger;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoggerApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("SERVER_ADDRESS", dotenv.get("SERVER_ADDRESS"));
        System.setProperty("SERVER_PORT", dotenv.get("SERVER_PORT"));
        System.setProperty("KAFKA_PORT", dotenv.get("KAFKA_PORT"));
        System.setProperty("KAFKA_ADDRESS", dotenv.get("KAFKA_ADDRESS"));
        System.setProperty("KAFKA_TOPIC_LOGS", dotenv.get("KAFKA_TOPIC_LOGS"));

        SpringApplication.run(LoggerApplication.class, args);
    }

}
