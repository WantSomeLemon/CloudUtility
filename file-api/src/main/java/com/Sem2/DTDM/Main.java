package com.Sem2.DTDM;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

@SpringBootApplication(scanBasePackages = "com.Sem2.DTDM")
@EnableMongoRepositories(basePackages = "com.Sem2.DTDM.common.repository")
@EnableMongoAuditing

public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("This is main of file-API");
        System.out.println("");
        SpringApplication.run(Main.class, args);
    }
}