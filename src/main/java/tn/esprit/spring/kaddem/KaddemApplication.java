package tn.esprit.spring.kaddem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KaddemApplication {
    // Webhook test comment - This is a test commit to trigger the webhook
    public static void main(String[] args) {
        SpringApplication.run(KaddemApplication.class, args);
    }
}
