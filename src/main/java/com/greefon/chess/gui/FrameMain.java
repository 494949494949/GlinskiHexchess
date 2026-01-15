package com.greefon.chess.gui;

import javax.swing.*;
import java.awt.*;

public class FrameMain extends JFrame {
    public FrameMain() {
        this.setTitle("Hex chess");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setPreferredSize(new Dimension(1080, 1080));
        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        Board board = new Board();
        this.add(board, gbc);

        this.pack();
        this.setVisible(true);
    }
}
