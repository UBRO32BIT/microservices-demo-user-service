package org.example.userservice.utils;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SmtpUtils {
    @Async("asyncTaskExecutor")
    public void sendWelcomeEmail(String username) {
        try {
            Thread.sleep(5000);
        }
        catch (Exception e) {
            System.out.println("Error at sendWelcomeEmail: " + e.getMessage());
        }
    }
}
