package org.reversi.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The springboot Reversi application.
 */
@SpringBootApplication
public class ReversiApplication {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ReversiApplication.class, args);
    }

}
