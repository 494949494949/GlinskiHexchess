package com.greefon.chess.logic;

public class ChessPiece {
    private final ChessType type;
    private final ChessColor color;

    public ChessPiece(ChessType type, ChessColor color) {
        this.type = type;
        this.color = color;
    }

    public ChessType getType() {
        return type;
    }

    public ChessColor getColor() {
        return color;
    }

    @Override
    public String toString() {
        return type.name() + ", " + color;
    }
}