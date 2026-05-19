package com.Sem2.DTDM;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

@SpringBootApplication(scanBasePackages = {"com.Sem2.DTDM"})
@EnableMongoAuditing
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("This is main of file-worker");
        try {
            SpringApplication.run(Main.class, args);
            Object lock = new Object();
            synchronized (lock) {
                lock.wait(); // Chờ đợi vô thời hạn
            }
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            // Không gọi System.exit() ở đây nếu muốn giữ container sống
        }
    }
}