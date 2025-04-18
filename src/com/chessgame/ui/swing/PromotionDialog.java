package com.chessgame.ui.swing;

import com.chessgame.core.pieces.*;
import com.chessgame.ui.constants.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for selecting a piece when promoting a pawn
 */
public class PromotionDialog extends JDialog {
    private Piece selectedPiece = null;

    /**
     * Creates a new promotion dialog
     *
     * @param parent the parent frame
     * @param color  the color of the pawn being promoted ("White" or "Black")
     */
    public PromotionDialog(JFrame parent, String color) {
        super(parent, "Pawn Promotion", true);

        // Set up the dialog
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // Create the title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UIConstants.PRIMARY_DARK);
        JLabel titleLabel = new JLabel("Choose promotion piece");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Create the pieces panel
        JPanel piecesPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        piecesPanel.setBackground(UIConstants.PRIMARY_LIGHT);
        piecesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add the piece buttons
        addPieceButton(piecesPanel, new Queen(color), "Queen");
        addPieceButton(piecesPanel, new Rook(color), "Rook");
        addPieceButton(piecesPanel, new Bishop(color), "Bishop");
        addPieceButton(piecesPanel, new Knight(color), "Knight");

        add(piecesPanel, BorderLayout.CENTER);

        // Pack and center the dialog
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Adds a button for a promotion piece
     *
     * @param panel     the panel to add the button to
     * @param piece     the piece to add
     * @param pieceName the name of the piece
     */
    private void addPieceButton(JPanel panel, Piece piece, String pieceName) {
        JButton button = new JButton(pieceName);
        button.setFont(UIConstants.BUTTON_FONT);
        button.setPreferredSize(new Dimension(100, 100));

        // Set the icon for the button
        try {
            PieceIcon icon = new PieceIcon(piece, 60);
            button.setIcon(icon);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
        } catch (Exception e) {
            // If the icon can't be loaded, just use text
            System.err.println("Failed to load icon for " + pieceName);
        }

        // Add action listener
        button.addActionListener(e -> {
            selectedPiece = piece;
            dispose();
        });

        panel.add(button);
    }

    /**
     * Shows the dialog and returns the selected piece
     *
     * @return the selected piece, or null if the dialog was closed
     */
    public Piece showDialog() {
        setVisible(true);
        return selectedPiece;
    }
}
