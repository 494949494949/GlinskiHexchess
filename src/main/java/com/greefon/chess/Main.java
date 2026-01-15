package com.greefon.chess;

import com.greefon.chess.logic.ChessMoves;
import com.greefon.chess.render.FrameMain;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
//        System.out.println(ChessMoves.toPoint("F1"));
//        System.out.println(ChessMoves.toPoint("F2"));
//        System.out.println(ChessMoves.toPoint("F3"));
        SwingUtilities.invokeLater(FrameMain::new);
    }
}
