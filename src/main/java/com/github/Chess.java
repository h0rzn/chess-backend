package com.github;

import com.github.engine.Game;
import com.github.engine.generator.Generator;
import com.github.engine.move.Move;
import com.github.services.GameService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
public class Chess {

    /**
     * Entry point of the application
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Chess.class, args);
    }
}