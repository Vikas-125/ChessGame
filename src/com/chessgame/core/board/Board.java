package com.chessgame.core.board;

import com.chessgame.core.pieces.*;
import com.chessgame.core.game.Move;

/**
 * Represents a chess board with an 8x8 grid of squares.
 * Manages the placement and movement of pieces on the board.
 */
public class Board {
    /** 2D array of squares representing the board */
    private Square[][] squares;

    /** The last move made on this board (important for en passant) */
    private Move lastMove;

    /**
     * Creates a new chess board and initializes it with pieces in their starting
     * positions.
     */
    public Board() {
        squares = new Square[8][8];
        initialize();
    }

    /**
     * Sets the last move made on this board.
     *
     * @param move The move to set as the last move
     */
    public void setLastMove(Move move) {
        this.lastMove = move;
    }

    /**
     * Gets the last move made on this board.
     *
     * @return The last move, or null if no moves have been made
     */
    public Move getLastMove() {
        return lastMove;
    }

    /**
     * Initializes the board with pieces in their starting positions.
     * Sets up the standard chess starting position.
     */
    public void initialize() {
        // Clear the board first
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = new Square(i, j);
            }
        }

        // Place Black pieces (top ranks 7 and 8)
        // Back rank (rank 8)
        squares[7][0].setPiece(new Rook("Black")); // a8
        squares[7][1].setPiece(new Knight("Black")); // b8
        squares[7][2].setPiece(new Bishop("Black")); // c8
        squares[7][3].setPiece(new Queen("Black")); // d8
        squares[7][4].setPiece(new King("Black")); // e8
        squares[7][5].setPiece(new Bishop("Black")); // f8
        squares[7][6].setPiece(new Knight("Black")); // g8
        squares[7][7].setPiece(new Rook("Black")); // h8
        // Pawns (rank 7)
        for (int j = 0; j < 8; j++) {
            squares[6][j].setPiece(new Pawn("Black"));
        }

        // Place White pieces (bottom ranks 1 and 2)
        // Back rank (rank 1)
        squares[0][0].setPiece(new Rook("White")); // a1
        squares[0][1].setPiece(new Knight("White")); // b1
        squares[0][2].setPiece(new Bishop("White")); // c1
        squares[0][3].setPiece(new Queen("White")); // d1
        squares[0][4].setPiece(new King("White")); // e1
        squares[0][5].setPiece(new Bishop("White")); // f1
        squares[0][6].setPiece(new Knight("White")); // g1
        squares[0][7].setPiece(new Rook("White")); // h1
        // Pawns (rank 2)
        for (int j = 0; j < 8; j++) {
            squares[1][j].setPiece(new Pawn("White"));
        }
    }

    /**
     * Checks if the given coordinates are valid board positions.
     *
     * @param x The x-coordinate (0-7)
     * @param y The y-coordinate (0-7)
     * @return true if the position is valid, false otherwise
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    /**
     * Gets the piece at the specified position.
     *
     * @param x The x-coordinate (0-7)
     * @param y The y-coordinate (0-7)
     * @return The piece at the position, or null if the position is empty or
     *         invalid
     */
    public Piece getPieceAt(int x, int y) {
        if (isValidPosition(x, y)) {
            return squares[x][y].getPiece();
        }
        return null;
    }

    /**
     * Sets a piece at the specified position.
     *
     * @param x     The x-coordinate (0-7)
     * @param y     The y-coordinate (0-7)
     * @param piece The piece to place, or null to clear the position
     */
    public void setPieceAt(int x, int y, Piece piece) {
        if (isValidPosition(x, y)) {
            squares[x][y].setPiece(piece);
        }
    }

    /**
     * Gets the square at the specified position.
     *
     * @param x The x-coordinate (0-7)
     * @param y The y-coordinate (0-7)
     * @return The square at the position, or null if the position is invalid
     */
    public Square getSquareAt(int x, int y) {
        if (isValidPosition(x, y)) {
            return squares[x][y];
        }
        return null;
    }

    /**
     * Creates a deep copy of this board.
     * Copies all pieces and the last move.
     *
     * @return A new board with the same state as this one
     */
    public Board copy() {
        Board newBoard = new Board();

        // Copy all pieces to their corresponding positions
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = getPieceAt(i, j);
                if (piece != null) {
                    // Create a new piece of the same type and color
                    newBoard.setPieceAt(i, j, piece.copy());
                }
            }
        }

        // Copy the last move if it exists
        if (this.lastMove != null) {
            newBoard.setLastMove(this.lastMove);
        }

        return newBoard;
    }
}
