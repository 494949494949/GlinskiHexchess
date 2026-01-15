package com.greefon.chess.logic;

import javax.swing.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChessMoves {
    private static final char[] FILE_LETTERS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L'};

    private static final int[] COLUMN_HEIGHTS = {6, 7, 8, 9, 10, 11, 10, 9, 8, 7, 6};

    public static final ArrayList<Point> whitePawnStartPositions = new ArrayList<>(Arrays.asList(new Point[]{
            new Point(10, 1), new Point(9, 2), new Point(8, 3),
            new Point(7, 4), new Point(6, 5), new Point(6, 6),
            new Point(6, 7), new Point(6, 8), new Point(6, 9)
    }));
    public static final ArrayList<Point> blackPawnStartPositions = new ArrayList<>(Arrays.asList(new Point[]{
            new Point(0, 4), new Point(1, 4), new Point(2, 4),
            new Point(3, 4), new Point(4, 4), new Point(4, 3),
            new Point(4, 2), new Point(4, 1), new Point(4, 0)
    }));

    private static Point moveBottomLeft(Point pos) {
        return new Point(pos.x + 1, pos.y - pos.x / 5);
    }

    static Point moveForward(Point pos) {
        return new Point(pos.x - 1, pos.y - (1 - pos.x / 6));
    }

    static Point moveBackward(Point pos) {
        return new Point(pos.x + 1, pos.y + (1 - pos.x / 5));
    }

    private static Point moveTopRight(Point pos) {
        return new Point(pos.x - 1, pos.y + pos.x / 6);
    }

    private static Point moveTopLeft(Point pos) {
        return new Point(pos.x, pos.y - 1);
    }

    private static Point moveBottomRight(Point pos) {
        return new Point(pos.x, pos.y + 1);
    }

    public static int[] getColumnHeights() {
        return COLUMN_HEIGHTS;
    }

    public static char[] getFileLetters() {
        return FILE_LETTERS;
    }

    public static ArrayList<Point> getWhitePawnStartPositions() {
        return whitePawnStartPositions;
    }

    public static ArrayList<Point> getBlackPawnStartPositions() {
        return blackPawnStartPositions;
    }

    public static List<Point> getLegalMoves(Point pos,
                                            ChessPiece currentPiece,
                                            Map<Point, ChessPiece> board,
                                            Point enPassantPawn,
                                            List<Point> doubleMovablePawns) {
        List<Point> legalMoves = new ArrayList<>();

        switch (currentPiece.getType()) {
            case PAWN:
                handlePawnMoves(pos, currentPiece, board, legalMoves,
                        enPassantPawn, doubleMovablePawns);
                break;

            case ROOK:
                addSlidingMoves(pos, currentPiece, board, legalMoves,
                        ChessMoves::moveForward, ChessMoves::moveBackward,
                        ChessMoves::moveTopLeft, ChessMoves::moveTopRight,
                        ChessMoves::moveBottomLeft, ChessMoves::moveBottomRight);
                break;

            case BISHOP:
                addSlidingMoves(pos, currentPiece, board, legalMoves,
                        p -> moveTopRight(moveForward(p)),
                        p -> moveTopLeft(moveForward(p)),
                        p -> moveBottomRight(moveBackward(p)),
                        p -> moveBottomLeft(moveBackward(p)),
                        p -> moveBottomLeft(moveTopLeft(p)),
                        p -> moveBottomRight(moveTopRight(p)));
                break;

            case KNIGHT:
                addSteppingMoves(pos, currentPiece, board, legalMoves,
                        moveTopRight(moveForward(moveForward(pos))),
                        moveTopLeft(moveForward(moveForward(pos))),
                        moveBottomLeft(moveBackward(moveBackward(pos))),
                        moveBottomRight(moveBackward(moveBackward(pos))),
                        moveForward(moveTopRight(moveTopRight(pos))),
                        moveBottomRight(moveTopRight(moveTopRight(pos))),
                        moveTopRight(moveBottomRight(moveBottomRight(pos))),
                        moveBackward(moveBottomRight(moveBottomRight(pos))),
                        moveTopLeft(moveBottomLeft(moveBottomLeft(pos))),
                        moveBackward(moveBottomLeft(moveBottomLeft(pos))),
                        moveBottomLeft(moveTopLeft(moveTopLeft(pos))),
                        moveForward(moveTopLeft(moveTopLeft(pos))));
                break;

            case QUEEN:
                addSlidingMoves(pos, currentPiece, board, legalMoves,
                        ChessMoves::moveForward, ChessMoves::moveBackward,
                        ChessMoves::moveTopLeft, ChessMoves::moveTopRight,
                        ChessMoves::moveBottomLeft, ChessMoves::moveBottomRight,
                        p -> moveTopRight(moveForward(p)),
                        p -> moveTopLeft(moveForward(p)),
                        p -> moveBottomRight(moveBackward(p)),
                        p -> moveBottomLeft(moveBackward(p)),
                        p -> moveBottomLeft(moveTopLeft(p)),
                        p -> moveBottomRight(moveTopRight(p)));
                break;

            case KING:
                addSteppingMoves(pos, currentPiece, board, legalMoves,
                        moveForward(pos), moveBackward(pos),
                        moveTopLeft(pos), moveTopRight(pos),
                        moveBottomLeft(pos), moveBottomRight(pos),
                        moveTopRight(moveForward(pos)),
                        moveTopLeft(moveForward(pos)),
                        moveBottomRight(moveBackward(pos)),
                        moveBottomLeft(moveBackward(pos)));
                break;
        }
        return legalMoves;
    }

    private static void handlePawnMoves(Point pos, ChessPiece pawn,
                                        Map<Point, ChessPiece> board,
                                        List<Point> moves,
                                        Point enPassantTarget,
                                        List<Point> doubleMovablePawns) {
        Point forwardOne;
        Point forwardTwo;
        Point captureLeft;
        Point captureRight;
        if (pawn.getColor() == ChessColor.WHITE) {
            forwardOne = moveForward(pos);
            forwardTwo = moveForward(forwardOne);
            captureLeft = moveTopLeft(pos);
            captureRight = moveTopRight(pos);
        } else {
            forwardOne = moveBackward(pos);
            forwardTwo = moveBackward(forwardOne);
            captureLeft = moveBottomLeft(pos);
            captureRight = moveBottomRight(pos);
        }

        if (isValidBoardPosition(forwardOne) && !board.containsKey(forwardOne)) {
            moves.add(forwardOne);
            boolean doubleAdvanceValid = isValidBoardPosition(forwardTwo)
                    && !board.containsKey(forwardTwo);
            if (doubleAdvanceValid && pawn.getColor() == ChessColor.WHITE) {
                if (doubleMovablePawns.contains(pos)) {
                    moves.add(forwardTwo);
                }
            } else if (doubleAdvanceValid && pawn.getColor() == ChessColor.BLACK) {
                if (doubleMovablePawns.contains(pos)) {
                    moves.add(forwardTwo);
                }
            }
        }

        checkPawnCapture(pawn, board, moves, captureLeft);
        checkPawnCapture(pawn, board, moves, captureRight);

        if (enPassantTarget != null) {
            handleEnPassant(pos, pawn, moves, enPassantTarget);
        }
    }

    private static void checkPawnCapture(ChessPiece pawn,
                                         Map<Point, ChessPiece> board,
                                         List<Point> moves, Point target) {
        if (isValidBoardPosition(target) && board.containsKey(target)) {
            if (board.get(target).getColor() != pawn.getColor()) {
                moves.add(target);
            }
        }
    }

    private static void handleEnPassant(Point pos, ChessPiece pawn,
                                        List<Point> moves, Point target) {
        if (pawn.getColor() == ChessColor.WHITE) {
            if (moveTopRight(target).equals(pos)) {
                moves.add(moveTopLeft(pos));
            }
            if (moveTopLeft(target).equals(pos)) {
                moves.add(moveTopRight(pos));
            }
        } else {
            if (moveBottomRight(target).equals(pos)) {
                moves.add(moveBottomLeft(pos));
            }
            if (moveBottomLeft(target).equals(pos)) {
                moves.add(moveBottomRight(pos));
            }
        }
    }

    private static void addSlidingMoves(Point start, ChessPiece piece,
                                        Map<Point, ChessPiece> board,
                                        List<Point> moves,
                                        MovementFunction... directions) {
        for (MovementFunction direction : directions) {
            Point current = direction.apply(start);
            while (isValidBoardPosition(current)) {
                if (board.containsKey(current)) {
                    if (board.get(current).getColor() != piece.getColor()) {
                        moves.add(current);
                    }
                    break;
                } else {
                    moves.add(current);
                    current = direction.apply(current);
                }
            }
        }
    }

    private static void addSteppingMoves(Point start, ChessPiece piece,
                                         Map<Point, ChessPiece> board,
                                         List<Point> moves, Point... targets) {
        for (Point target : targets) {
            if (isValidBoardPosition(target)) {
                if (!board.containsKey(target) || board.get(target).getColor() != piece.getColor()) {
                    moves.add(target);
                }
            }
        }
    }

    private static boolean isValidBoardPosition(Point pos) {
        if (pos.x >= 0 && pos.x < COLUMN_HEIGHTS.length) {
            return pos.y >= 0 && pos.y < COLUMN_HEIGHTS[pos.x];
        }
        return false;
    }

    public static String toAddress(int col, int row) {
        if (!isValidBoardPosition(new Point(col, row))) {
            return null;
        }
        return FILE_LETTERS[col] + String.valueOf(row + 1);
    }

    public static Point toPoint(String address) {
        if (address == null || address.length() < 2) {
            return null;
        }

        char fileChar = Character.toUpperCase(address.charAt(0));
        int fileIndex = -1;

        for (int i = 0; i < FILE_LETTERS.length; i++) {
            if (FILE_LETTERS[i] == fileChar) {
                fileIndex = i;
                break;
            }
        }

        if (fileIndex == -1) {
            return null;
        }

        try {
            int rankIndex = Integer.parseInt(address.substring(1)) - 1;

            Point p = new Point(fileIndex, rankIndex);
            if (isValidBoardPosition(p)) {
                return p;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }


    @FunctionalInterface
    private interface MovementFunction {
        Point apply(Point p);
    }
}