package com.greefon.chess.logic;

import java.awt.Point;
import java.util.*;

public class Game {
    private final Map<Point, ChessPiece> pieces;
    private ChessColor turn;
    private Point enPassantPawn;
    private boolean gameOver;

    public List<Point> doubleMovableWhitePawns = new ArrayList<>(
            ChessMoves.getWhitePawnStartPositions()
    );
    public List<Point> doubleMovableBlackPawns = new ArrayList<>(
            ChessMoves.getBlackPawnStartPositions()
    );

    public Game() {
        this.pieces = new HashMap<>();
        reset();
    }

    public void reset() {
        pieces.clear();
        turn = ChessColor.WHITE;
        enPassantPawn = null;
        doubleMovableWhitePawns = new ArrayList<>(
                ChessMoves.getWhitePawnStartPositions()
        );
        doubleMovableBlackPawns = new ArrayList<>(
                ChessMoves.getBlackPawnStartPositions()
        );
        gameOver = false;
        setupBoard();
    }

    public ChessPiece getPiece(int col, int row) {
        return pieces.get(new Point(col, row));
    }

    public ChessPiece getPiece(Point p) {
        return pieces.get(p);
    }

    public ChessColor getTurn() {
        return turn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public List<Point> getLegalMoves(Point pos) {
        ChessPiece piece = pieces.get(pos);
        if (piece == null || piece.getColor() != turn) {
            return new ArrayList<>();
        }

        List<Point> doubleMovablePawns;
        if (piece.getColor() == ChessColor.WHITE) {
            doubleMovablePawns = doubleMovableWhitePawns;
        } else {
            doubleMovablePawns = doubleMovableBlackPawns;
        }

        return ChessMoves.getLegalMoves(pos, piece, pieces, enPassantPawn, doubleMovablePawns);
    }

    public void performMove(Point from, Point to) {
        ChessPiece piece = pieces.remove(from);
        ChessPiece target = pieces.get(to);

        if (target != null && target.getType() == ChessType.KING) {
            gameOver = true;
        }

        // Пешки
        if (piece.getType() == ChessType.PAWN) {
            // Взятие на проходе
            if (target == null && (from.x != to.x || from.y != to.y)) {
                if (piece.getColor() == ChessColor.WHITE) {
                    if (ChessMoves.moveBackward(to).equals(enPassantPawn)) {
                        pieces.remove(enPassantPawn);
                    }
                } else {
                    if (ChessMoves.moveForward(to).equals(enPassantPawn)) {
                        pieces.remove(enPassantPawn);
                    }
                }
            }

            // Двойные движения пешкой (возможность взятия на проходе в след.ходу)
            if (piece.getColor() == ChessColor.WHITE) {
                doubleMovableWhitePawns.remove(from);
                if (ChessMoves.moveForward(ChessMoves.moveForward(from)).equals(to)) {
                    enPassantPawn = to;
                } else {
                    enPassantPawn = null;
                }
            } else {
                doubleMovableBlackPawns.remove(from);
                if (ChessMoves.moveBackward(ChessMoves.moveBackward(from)).equals(to)) {
                    enPassantPawn = to;
                } else {
                    enPassantPawn = null;
                }
            }
        } else {
            enPassantPawn = null;
        }

        pieces.put(to, piece);

        turn = (turn == ChessColor.WHITE) ? ChessColor.BLACK : ChessColor.WHITE;
    }


    private void setPiece(String address, ChessType type, ChessColor color) {
        Point point = ChessMoves.toPoint(address);

        if (point != null) {
            ChessPiece piece = new ChessPiece(type, color);
            pieces.put(point, piece);
        }
    }

    private void setPiece(Point point, ChessType type, ChessColor color) {
        if (point != null) {
            ChessPiece piece = new ChessPiece(type, color);
            pieces.put(point, piece);
        }
    }

    private void setPiece(int x, int y, ChessType type, ChessColor color) {
        Point point = new Point(x, y);

        ChessPiece piece = new ChessPiece(type, color);
        pieces.put(point, piece);
    }

    private void setupBoard() {
        pieces.clear();

        // Белые
        setPiece(9,6, ChessType.KING, ChessColor.WHITE);

        setPiece(10,4, ChessType.QUEEN, ChessColor.WHITE);

        setPiece(10,5, ChessType.BISHOP, ChessColor.WHITE);
        setPiece(9,5, ChessType.BISHOP, ChessColor.WHITE);
        setPiece(8,5, ChessType.BISHOP, ChessColor.WHITE);

        setPiece(10,3, ChessType.KNIGHT, ChessColor.WHITE);
        setPiece(8,7, ChessType.KNIGHT, ChessColor.WHITE);

        setPiece(10,2, ChessType.ROOK, ChessColor.WHITE);
        setPiece(7,8, ChessType.ROOK, ChessColor.WHITE);

        setPiece(10,1, ChessType.PAWN, ChessColor.WHITE);
        setPiece(9,2, ChessType.PAWN, ChessColor.WHITE);
        setPiece(8,3, ChessType.PAWN, ChessColor.WHITE);
        setPiece(7,4, ChessType.PAWN, ChessColor.WHITE);
        setPiece(6,5, ChessType.PAWN, ChessColor.WHITE);
        setPiece(6,6, ChessType.PAWN, ChessColor.WHITE);
        setPiece(6,7, ChessType.PAWN, ChessColor.WHITE);
        setPiece(6,8, ChessType.PAWN, ChessColor.WHITE);
        setPiece(6,9, ChessType.PAWN, ChessColor.WHITE);


        // Чёрные
        setPiece(0,1, ChessType.KING, ChessColor.BLACK);

        setPiece(1,0, ChessType.QUEEN, ChessColor.BLACK);

        setPiece(0,0, ChessType.BISHOP, ChessColor.BLACK);
        setPiece(1,1, ChessType.BISHOP, ChessColor.BLACK);
        setPiece(2,2, ChessType.BISHOP, ChessColor.BLACK);

        setPiece(0,2, ChessType.KNIGHT, ChessColor.BLACK);
        setPiece(2,0, ChessType.KNIGHT, ChessColor.BLACK);

        setPiece(0,3, ChessType.ROOK, ChessColor.BLACK);
        setPiece(3,0, ChessType.ROOK, ChessColor.BLACK);

        setPiece(0,4, ChessType.PAWN, ChessColor.BLACK);
        setPiece(1,4, ChessType.PAWN, ChessColor.BLACK);
        setPiece(2,4, ChessType.PAWN, ChessColor.BLACK);
        setPiece(3,4, ChessType.PAWN, ChessColor.BLACK);
        setPiece(4,4, ChessType.PAWN, ChessColor.BLACK);
        setPiece(4,3, ChessType.PAWN, ChessColor.BLACK);
        setPiece(4,2, ChessType.PAWN, ChessColor.BLACK);
        setPiece(4,1, ChessType.PAWN, ChessColor.BLACK);
        setPiece(4,0, ChessType.PAWN, ChessColor.BLACK);
    }
}