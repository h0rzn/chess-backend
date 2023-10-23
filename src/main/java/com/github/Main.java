package com.github;

import com.github.engine.Bitboard;
import com.github.engine.BitboardOld;
import com.github.engine.IBoard;
import com.github.engine.generator.Generator;
import com.github.engine.move.Move;
import lombok.Getter;

import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    @Getter
    static Bitboard bitboard;
    static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        bitboard = new Bitboard();

        bitboard.printChessboard();
        while (true){
            String move = getMove();
            Optional<IBoard.T2<IBoard.T3, IBoard.T3>> parsedMove = parseMove(move);
            if (parsedMove.isPresent()) {
                IBoard.T2<IBoard.T3, IBoard.T3> t2 = parsedMove.get();
                System.out.println(t2);
                Generator generator = new Generator(bitboard);
                List<Integer> moves = generator.generate(t2, 0);
                if(moves.contains(t2.right().index())){
                    System.out.println("Valid move");
                    makeMove(t2, 0);
                    bitboard.printChessboard();
                } else {
                    System.out.println("Invalid move");
                }
            }
        }
    }

    private static boolean makeMove(IBoard.T2<IBoard.T3, IBoard.T3> t2, int color){
       int from = t2.left().index();
       int to = t2.right().index();

       int pieceType = getBitboard().Get(from, color);
       long fromMask = 1L << from;
       long toMask = 1L << to;

       // Remove piece from old position
       if (color == 0){
           getBitboard().getBoardWhite()[pieceType] &= ~fromMask;
       } else {
           getBitboard().getBoardBlack()[pieceType] &= ~fromMask;
       }

       // Remove captured piece from new position if there is one
       for (int i = 0; i < 6; i++){
           if ((toMask & (color == 0 ? getBitboard().getBoardBlack()[i] : getBitboard().getBoardWhite()[i])) != 0){
                if (color == 0){
                     getBitboard().getBoardBlack()[i] &= ~toMask;
                } else {
                     getBitboard().getBoardWhite()[i] &= ~toMask;
                }
                break;
           }
       }

       // Add piece to new position
       if (color == 0){
           getBitboard().getBoardWhite()[pieceType] |= toMask;
       } else {
           getBitboard().getBoardBlack()[pieceType] |= toMask;
       }

       // TODO: En passant
        // TODO: Castling
        // TODO: Promotion

       return true;
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