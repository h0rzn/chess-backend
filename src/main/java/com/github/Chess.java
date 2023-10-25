package com.github;

import com.github.engine.Bitboard;
import com.github.engine.IBoard;
import com.github.engine.generator.Generator;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
public class Chess {
    @Getter
    static Bitboard bitboard;
    static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        SpringApplication.run(Chess.class, args);
    }

    private static void init(){
        bitboard = new Bitboard();

        bitboard.printChessboard(bitboard.getColorToMove());
        while (true){
            // Gets the input from user
            String move = getMove();
            // Parses the input in T2 format
            Optional<IBoard.T2<IBoard.T3, IBoard.T3>> parsedMove = parseMove(move);
            // Checks wether the input was a valid input
            if (parsedMove.isPresent()) {
                // Contains the rank, file and index of the current position and the position to move to
                IBoard.T2<IBoard.T3, IBoard.T3> t2 = parsedMove.get();
                System.out.println(t2);
                // Generates all possible moves for the current position and stores them in a list
                Generator generator = new Generator(bitboard);
                List<Integer> moves = generator.generate(t2, bitboard.getColorToMove());
                // Checks wether the move from user is in validMove list -> is a valid move
                if(moves.contains(t2.right().index())){
                    System.out.println("Valid move");
                    // Executes the move
                    bitboard.makeMove(t2);
                    // Turns the board
                    bitboard.turn();
                    // Prints the board
                    bitboard.printChessboard(bitboard.getColorToMove());
                } else {
                    System.out.println("Invalid move");
                }
            }
        }
    }



    private static Optional<IBoard.T2<IBoard.T3, IBoard.T3>> parseMove(String move) {
        try {
            String[] split = move.split("-");
            String[] from = split[0].split("");
            String[] to = split[1].split("");

            IBoard.T3 moveFrom = IBoard.t2ToT3.apply(new IBoard.T2<>(from[0].toUpperCase(), Integer.parseInt(from[1])-1));
            IBoard.T3 moveTo = IBoard.t2ToT3.apply(new IBoard.T2<>(to[0].toUpperCase(), Integer.parseInt(to[1])-1));

            return Optional.of(IBoard.T2.of(moveFrom, moveTo));
        } catch (Exception e){
            return Optional.empty();
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