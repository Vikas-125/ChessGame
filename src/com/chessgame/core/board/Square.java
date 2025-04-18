package com.chessgame.core.board;

import com.chessgame.core.pieces.Piece;

/**
 * Represents a square on the chess board.
 * Each square has coordinates and may contain a chess piece.
 */
public class Square {
    /** The x-coordinate (rank, 0-7) */
    private int x;

    /** The y-coordinate (file, 0-7) */
    private int y;

    /** The chess piece on this square, or null if empty */
    private Piece piece;

    /**
     * Creates a new empty square at the specified coordinates.
     *
     * @param x The x-coordinate (0-7)
     * @param y The y-coordinate (0-7)
     */
    public Square(int x, int y) {
        this.x = x;
        this.y = y;
        this.piece = null;
    }

    /**
     * Gets the x-coordinate (rank).
     *
     * @return The x-coordinate (0-7)
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate (file).
     *
     * @return The y-coordinate (0-7)
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the piece on this square.
     *
     * @return The piece on this square, or null if empty
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Sets the piece on this square.
     *
     * @param piece The piece to place on this square, or null to clear the square
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Checks if this square is occupied by a piece.
     *
     * @return true if the square contains a piece, false otherwise
     */
    public boolean isOccupied() {
        return piece != null;
    }
}
