package org.example.userservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduleConfiguration {
    @Scheduled(cron = "*/10 * * * * *")
    public void printServerUptime() {
        System.out.println("HELLO");
    }
}
