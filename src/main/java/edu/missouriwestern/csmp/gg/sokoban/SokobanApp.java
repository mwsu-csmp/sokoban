package edu.missouriwestern.csmp.gg.sokoban;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:sokoban-layout.xml")
public class SokobanApp {

    public static void main(String[] args) {
        SpringApplication.run(SokobanApp.class, args);
    }
}