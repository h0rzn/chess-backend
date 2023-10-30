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
    @Getter
    static Game game;
    static final Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        SpringApplication.run(Chess.class, args);

    }

    private static void init(){
        game = new Game();

        game.printChessboard(game.getColorToMove());
        while (true){
            // Gets the input from user
            String move = getMove();
            // Parses the input in T2 format
            Optional<Move> parsedMove = Optional.of(new Move(move));
            // Checks wether the input was a valid input
            if (parsedMove.isPresent()) {
                // Contains the rank, file and index of the current position and the position to move to
                Move move1 = parsedMove.get();
                System.out.println(move1);
                // Generates all possible moves for the current position and stores them in a list
                Generator generator = new Generator(game);
                List<Integer> moves = generator.generate(move1, game.getColorToMove());
                // Checks wether the move from user is in validMove list -> is a valid move
                if(moves.contains(move1.getTo().getIndex())){
                    System.out.println("Valid move");
                    // Executes the move
                    game.makeMove(move1);
                    // Turns the board
                    game.turn();
                    // Prints the board
                    game.printChessboard(game.getColorToMove());
                } else {
                    System.out.println("Invalid move");
                }
            }
        }
    }

    private static String getMove(){
        System.out.println("Enter move: ");
        return scanner.nextLine();
    }

    public static BitSet longToBitSet(long value) {
        BitSet bitSet = new BitSet(Long.SIZE); // Long.SIZE ist 64, da ein long 64 Bits hat
        for (int i = 0; i < Long.SIZE; i++) {
            if ((value & (1L << i)) != 0) {
                bitSet.set(i);
            }
        }
        return bitSet;
    }
}