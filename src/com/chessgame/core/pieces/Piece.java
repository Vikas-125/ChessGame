package com.chessgame.core.pieces;

import com.chessgame.core.board.Board;
import com.chessgame.core.board.Position;
import com.chessgame.core.game.Move;

import java.util.List;

/**
 * Abstract base class for all chess pieces.
 * Defines common properties and behaviors that all chess pieces share.
 */
public abstract class Piece {
    /** The color of the piece ("White" or "Black") */
    private String color;

    /**
     * Flag indicating whether the piece has moved (important for castling and pawn
     * double moves)
     */
    protected boolean hasMoved; // Changed from private to protected

    /**
     * Creates a new piece with the specified color.
     *
     * @param color The color of the piece ("White" or "Black")
     */
    public Piece(String color) {
        this.color = color;
        this.hasMoved = false;
    }

    /**
     * Gets the color of the piece.
     *
     * @return The color ("White" or "Black")
     */
    public String getColor() {
        return color;
    }

    /**
     * Checks if the piece has moved.
     *
     * @return true if the piece has moved, false otherwise
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Sets the moved status of the piece.
     *
     * @param hasMoved true if the piece has moved, false otherwise
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Calculates all legal moves for this piece from the current position.
     *
     * @param board           The current board state
     * @param currentPosition The current position of the piece
     * @return A list of all legal moves for this piece
     */
    public abstract List<Move> calculateLegalMoves(Board board, Position currentPosition);

    /**
     * Creates a deep copy of this piece.
     *
     * @return A new piece with the same properties as this one
     */
    public abstract Piece copy();

    /**
     * Returns a string representation of the piece.
     *
     * @return A string in the format "PieceType (Color)"
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " (" + color + ")";
    }
}
