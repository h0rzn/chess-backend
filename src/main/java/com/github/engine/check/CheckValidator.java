package com.github.engine.check;

import com.github.engine.Bitboard;
import com.github.engine.check.CheckStatus;
import com.github.engine.GameBoard;
import com.github.engine.generator.Generator;
import com.github.engine.models.CheckInfo;
import com.github.engine.models.CheckResolveInfo;
import com.github.engine.move.Move;
import com.github.engine.move.Position;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// CheckValidator contains methods to inspect a potential
// check situation and check if the check is resolvable
public class CheckValidator {
    @Setter
    private GameBoard gameBoard;

    public CheckStatus analyzeCheck(int playerColor, Move move) {
        long moveToBoard = (1L << move.getTo().getIndex());
        CheckInfo checkInfo = inCheck(playerColor);
        if (checkInfo == null) {
            return CheckStatus.Unkown;
        }

        if (!checkInfo.isCheck()) {
            if (move.getFrom().getPieceType() == 5) {
                if ((moveToBoard& checkInfo.enemyMoveCovered()) != 0) {
                    // even though the players king is currently not in check
                    // we have to validate that this move would not put him in a check situation

                    // king attempts to move to piece that would put him in check
                    return CheckStatus.KingMoveToNextCheck;
                }
            }
            return CheckStatus.NoCheck;
        }
        return CheckStatus.Check;
    }

    public CheckStatus analyzeCheckWithResolve(int playerColor, Move move) {
        long moveToBoard = (1L << move.getTo().getIndex());
        CheckInfo checkInfo = inCheck(playerColor);
        if (checkInfo == null) {
            return CheckStatus.Unkown;
        }

        if (!checkInfo.isCheck()) {
            // no check
            return CheckStatus.NoCheck;
        } else if (move.getFrom().getPieceType() == 5) {
            if ((moveToBoard& checkInfo.enemyMoveCovered()) != 0) {
                // even though the players king is currently not in check
                // we have to validate that this move would not put him in a check situation

                // king attempts to move to piece that would put him in check
                return CheckStatus.KingMoveToNextCheck;
            }
        }
        if (move.getFrom().getPieceType() == 5) {
            // king escapes does not contain wanted move --> illegal in check move
            if ((moveToBoard & checkInfo.kingEscapes()) == 0) {
                // illegal in check move for king
                return CheckStatus.KingIllegalInCheckMove;
            }
        } else {
            CheckResolveInfo resolveInfo = isCheckResolvable(playerColor, checkInfo);
            if (!resolveInfo.resolvable()) {
                // check but not resolvable
                return CheckStatus.Checkmate;
            }

            // look up move is legal resolve move
            long[] a2d = resolveInfo.attack2Defend();
            long[] b2d = resolveInfo.block2Defend();
            for (int playerPiece = 0; playerPiece < 6; playerPiece++) {
                if ((a2d[playerPiece]&moveToBoard) != 0) {
                    return CheckStatus.MoveResolvesCheck;
                } else if ((b2d[playerPiece]&moveToBoard) != 0) {
                    return CheckStatus.MoveResolvesCheck;
                }
            }
        }
        return CheckStatus.IllegalInCheckMove;
    }

    // validate if the given player is in a check situation
    // CheckInfo returns additional information
    private CheckInfo inCheck(int playerColor) {
        long[] playerPieces;
        long[] enemyPieces;
        int enemyColor;
        if (playerColor == 0) {
            playerPieces = gameBoard.getSetWhite();
            enemyPieces = gameBoard.getSetBlack();
            enemyColor = 1;
        } else {
            playerPieces = gameBoard.getSetBlack();
            enemyPieces = gameBoard.getSetWhite();
            enemyColor = 0;
        }

        long kingBoard = playerPieces[5];
        int kingSquare = Bitboard.bitscanSingle(kingBoard);

        if (kingSquare == -1) {
            // TODO case where king cannot be found
            return null;
        }

        // list of bitboards with move generation for each enemy piece that attack the players king
        long[] attackBoards = new long[6];
        // attackBoards[6] merged into a single bitboard
        long attackRoutes = 0;
        // bitboard marking all squares that enemy pieces can currently move to
        long enemyCovers = 0;

        // generate enemy moves
        Generator generator = new Generator(enemyColor, gameBoard);
        for (int enemyPiece = 0; enemyPiece < 6; enemyPiece++) {
            // skip empty piece boards
            if (enemyPieces[enemyPiece] == 0) {
                continue;
            }
            // get all occupied squares of that pieceType
            List<Integer> enemyPieceSquares = Bitboard.bitscanMulti(enemyPieces[enemyPiece]);

            // iterate over each square (=each Piece) and run move gen
            for (Integer occupiedSquare : enemyPieceSquares) {
                Position enemyPosition = new Position(occupiedSquare, enemyPiece, enemyColor);
                long enemyPieceMoves = generator.generate(enemyPosition, true);

                // if enemy player move generation includes the players king square
                // add that move generation to the combined attackRoutes and
                // the attackBoard of the respective enemy piece
                // if there is no match we still add the move gen to the enemy covers
                // the get a map of all enemy reachable squares
                if ((enemyPieceMoves & kingBoard) != 0) {
                    attackRoutes |= enemyPieceMoves;
                    attackBoards[enemyPiece] |= enemyPieceMoves;
                }
                enemyCovers |= enemyPieceMoves;
            }
        }

        // if any of the enemies attack routes match our kings position,
        // then the players king is in check
        boolean kingInCheck = (attackRoutes & kingBoard) != 0;
        System.out.println("KING BOARD    "+kingBoard);
        System.out.println("ATTACK ROUTES "+attackRoutes);
        System.out.println("ATTACK BOARDS "+Arrays.toString(attackBoards));

        // kingEscapes stores all valid moves for king that resolve
        // a **potential** check situation
        Position playerKingPosition = new Position(kingSquare);
        playerKingPosition.setPieceType(5);
        generator.setPlayerColor(playerColor);
        long kingMoves = generator.generate(playerKingPosition, true);
        kingMoves &= ~enemyPieces[5];

        // cant escape to enemy occupied squares
        long kingEscapes = (kingMoves & ~enemyCovers);
        // remove player squares from escape square
        for (int p = 0; p < 6; p++) {
            kingEscapes &= ~playerPieces[p];
        }

        return new CheckInfo(kingInCheck, kingEscapes,attackBoards, enemyCovers);
    }

    private CheckResolveInfo isCheckResolvable(int playerColor, CheckInfo checkInfo) {
        long[] attackBoards = checkInfo.attackBoards();
        long enemyCovers = checkInfo.enemyMoveCovered();

        long[] playerPieces;
        long[] enemyPieces;
        int enemyColor;
        if (playerColor == 0) {
            playerPieces = gameBoard.getSetWhite();
            enemyPieces = gameBoard.getSetBlack();
            enemyColor = 1;
        } else {
            playerPieces = gameBoard.getSetBlack();
            enemyPieces = gameBoard.getSetWhite();
            enemyColor = 0;
        }

        System.out.println("+++");
        System.out.println("resolve check: playerColor "+playerColor+ " enemyColor "+enemyColor+ "enemy covered "+enemyCovers);

        long[] attack2Defend = new long[6];
        long[] block2Defend = new long[6];
        boolean a2dResolvable = false;
        boolean b2dResolvable = false;

        Generator generator = new Generator(playerColor, gameBoard);
        int playerKingSquare = Bitboard.bitscanSingle(playerPieces[5]);
        int playerKingColumn = playerKingSquare % 8;
        long[] playerMoves = generator.generateAll();

        for (int attackPiece = 0; attackPiece < 6; attackPiece++) {
            // filter out non-attacks
            if (attackBoards[attackPiece] == 0) {
                continue;
            }
            System.out.println("# RESOLVE # handle attack of "+attackPiece + " -> "+attackBoards[attackPiece]);

            // check resolve actions for each player piece
            for (int playerPiece = 0; playerPiece < 6; playerPiece++) {
                List<Integer> pieceSquares = Bitboard.bitscanMulti(playerPieces[playerPiece]);
                //
                // ATTACK 2 DEFEND
                //
                for (Integer pieceSquare : pieceSquares) {
                    System.out.println("# A2D check for "+playerPiece+" on "+pieceSquare);
                    if ((playerMoves[playerPiece] & enemyPieces[attackPiece]) != 0) {

                        // player king cannot a2d enemy piece that is covered
                        if (playerPiece == 5) {
                            if ((playerMoves[5] & enemyCovers) != 0) {
                                continue;
                            }
                        }

                        System.out.println("# A2D register; playerMoves "+playerMoves[playerPiece]+" enemyPiece "+enemyPieces[attackPiece]);
                        // store piece position in piece group board
                        // TODO check if we should store attacked enemy piece in bitboard as well
                        attack2Defend[playerPiece] |= (1L << pieceSquare);
                        a2dResolvable = true;
                    }


                    //
                    // BLOCK 2 DEFEND
                    //
                    if (playerPiece == 5) {
                        continue;
                    }
                    // check if move generation routes of attacker and player piece match
                    long routeMatchBoard = (playerMoves[playerPiece]&attackBoards[attackPiece]);
                    if (routeMatchBoard != 0) {
                        //System.out.println("B2D handling attacker "+attackPiece+" with player piece "+playerPiece);
                        List<Integer> routeMatches = Bitboard.bitscanMulti(routeMatchBoard);

                        // TODO Revwrite this isolation logic with bitboards
                        // TODO bitscan multiple pieces instead of the first occurrence
                        int ePieceSquare = Bitboard.bitscanSingle(enemyPieces[attackPiece]);
                        List<Integer> blocks = isolateBlocks(routeMatches, pieceSquare, playerPiece, ePieceSquare, playerKingSquare, playerKingColumn);

                        // combine blocks
                        long blockBoard = 0;
                        for (int block : blocks) {
                            blockBoard |= (1L << block);
                            b2dResolvable = true;
                        }

                        block2Defend[playerPiece] = blockBoard;
                    }
                }
            }
        }


        // drop a2d for king if move puts him in check
        // we could probably avoid this complete enemy move generation
        generator.setPlayerColor(enemyColor);
        long[] enemyMoves = generator.generateAll();
        long enemyMovesCombined = 0;
        long enemySelfCovers = 0;
        for (long enemyMoveBoard : enemyMoves) {
            enemyMovesCombined |= enemyMoveBoard;
            long n = playerMoves[5] & enemyMoveBoard;
            System.out.println(">> "+enemyMoveBoard+ " " + n);
        }

        /*
        for (int i = 0; i < 6; i++) {
            enemySelfCovers |= (enemyMoves[i] & ene)
        }

         */

        attack2Defend[5] &= ~enemyMovesCombined;

        System.out.println("resolve a2d: " + Arrays.toString(attack2Defend));
        System.out.println("resolve b2d: " + Arrays.toString(block2Defend));
        System.out.println("incheck king moves: "+playerMoves[5]);
        System.out.println("enemy self covers: "+enemySelfCovers);

        // long n = playerMoves[5] &~ 1


        return new CheckResolveInfo(a2dResolvable||b2dResolvable, attack2Defend, block2Defend);
    }

    public static List<Integer> isolateBlocks(List<Integer> matches, int pPieceSquare, int pPieceType, int ePieceSquare, int kingSquare, int kingColumn) {
        List<Integer> blocks = new ArrayList<>();

        if (pPieceSquare == 5) {
            return blocks;
        }

        if (pPieceType == 0 || pPieceType == 2 || pPieceType == 4 || pPieceType == 1) {
            // DIAGONALS
            for (int match : matches) {
                int matchColumn = match % 8;

                if (match / 8 == ePieceSquare / 8) {
                    continue;
                }

                if (matchColumn < (ePieceSquare % 8) && matchColumn > kingColumn) {
                    //System.out.println("> isolate: dia[A] -> " + match);
                    blocks.add(match);
                } else if (matchColumn > (ePieceSquare % 8) && matchColumn < kingColumn) {
                    //System.out.println("> isolate: dia[B] -> " + match);
                    blocks.add(match);
                }
            }
        }

        if (pPieceType == 3 || pPieceType == 4) {
            // LANES
            for (int match : matches) {
                int matchColumn = match % 8;

                if (matchColumn == pPieceSquare % 8) {
                    if (pPieceSquare > kingSquare) {
                        if (match < pPieceSquare) {
                            //System.out.println("> isolate: lane[A] -> " + match);
                            blocks.add(match);
                        }
                    } else {
                        if (match > pPieceSquare) {
                            //System.out.println("> isolate: lane[B] -> " + match);
                            blocks.add(match);
                        }
                    }
                } else if (matchColumn == kingColumn) {
                    blocks.add(match);
                }
            }
        }

        return blocks;
    }

    public CheckValidator(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }
}
