package com.chessgame.core.game;

import com.chessgame.core.board.Position;
import com.chessgame.core.pieces.Piece;

/**
 * Represents a chess move from one position to another.
 * Includes information about special moves like castling, en passant, and
 * promotion.
 */
public class Move {
    /** Starting position of the move */
    private Position start;

    /** Ending position of the move */
    private Position end;

    /** The piece being moved */
    private Piece movingPiece;

    /** The piece being captured, if any */
    private Piece capturedPiece;

    /** Algebraic notation for this move */
    private String notation;

    /** Flag indicating if this is an en passant capture */
    private boolean isEnPassant;

    /** Position of the captured pawn in en passant */
    private Position enPassantCapturePosition;

    /** Starting position of the rook in castling */
    private Position castlingRookStart;

    /** Ending position of the rook in castling */
    private Position castlingRookEnd;

    /** The rook involved in castling */
    private Piece castlingRook;

    /** The piece a pawn is promoted to */
    private Piece promotedPiece;

    /** Flag indicating if this is a pawn promotion */
    private boolean isPromotion;

    /** Flag indicating if this is a castling move */
    private boolean isCastling;

    /** Flag indicating if this move puts the opponent in check */
    private boolean isCheck;

    /** Flag indicating if this move puts the opponent in checkmate */
    private boolean isCheckmate;

    /**
     * Creates a new move from the start position to the end position.
     *
     * @param start The starting position
     * @param end   The ending position
     */
    public Move(Position start, Position end) {
        this.start = start;
        this.end = end;
        this.isCastling = false;
        this.isEnPassant = false;
        this.isPromotion = false;
    }

    /**
     * Gets the starting position of this move.
     *
     * @return The starting position
     */
    public Position getStart() {
        return start;
    }

    /**
     * Gets the ending position of this move.
     *
     * @return The ending position
     */
    public Position getEnd() {
        return end;
    }

    /**
     * Gets the piece captured by this move, if any.
     *
     * @return The captured piece, or null if no piece was captured
     */
    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    /**
     * Sets the piece captured by this move.
     *
     * @param piece The captured piece
     */
    public void setCapturedPiece(Piece piece) {
        this.capturedPiece = piece;
    }

    /**
     * Gets the piece being moved.
     *
     * @return The moving piece
     */
    public Piece getMovingPiece() {
        return movingPiece;
    }

    /**
     * Sets the piece being moved.
     *
     * @param piece The moving piece
     */
    public void setMovingPiece(Piece piece) {
        this.movingPiece = piece;
    }

    /**
     * Gets the algebraic notation for this move.
     *
     * @return The move notation
     */
    public String getNotation() {
        return notation;
    }

    /**
     * Sets the algebraic notation for this move.
     *
     * @param notation The move notation
     */
    public void setNotation(String notation) {
        this.notation = notation;
    }

    /**
     * Sets this move as a castling move.
     *
     * @param rookStart The starting position of the rook
     * @param rookEnd   The ending position of the rook
     * @param rook      The rook being moved
     */
    public void setCastlingMove(Position rookStart, Position rookEnd, Piece rook) {
        this.isCastling = true;
        this.castlingRookStart = rookStart;
        this.castlingRookEnd = rookEnd;
        this.castlingRook = rook;
    }

    /**
     * Checks if this is a castling move.
     *
     * @return true if this is a castling move, false otherwise
     */
    public boolean isCastling() {
        return isCastling;
    }

    /**
     * Gets the starting position of the rook in a castling move.
     *
     * @return The starting position of the rook
     */
    public Position getCastlingRookStart() {
        return castlingRookStart;
    }

    /**
     * Gets the ending position of the rook in a castling move.
     *
     * @return The ending position of the rook
     */
    public Position getCastlingRookEnd() {
        return castlingRookEnd;
    }

    /**
     * Gets the rook involved in a castling move.
     *
     * @return The castling rook
     */
    public Piece getCastlingRook() {
        return castlingRook;
    }

    /**
     * Sets whether this is an en passant capture.
     *
     * @param isEnPassant true if this is an en passant capture, false otherwise
     */
    public void setEnPassant(boolean isEnPassant) {
        this.isEnPassant = isEnPassant;
    }

    /**
     * Checks if this is an en passant capture.
     *
     * @return true if this is an en passant capture, false otherwise
     */
    public boolean isEnPassant() {
        return isEnPassant;
    }

    /**
     * Sets the position of the captured pawn in an en passant capture.
     *
     * @param position The position of the captured pawn
     */
    public void setEnPassantCapturePosition(Position position) {
        this.enPassantCapturePosition = position;
    }

    /**
     * Gets the position of the captured pawn in an en passant capture.
     *
     * @return The position of the captured pawn
     */
    public Position getEnPassantCapturePosition() {
        return enPassantCapturePosition;
    }

    /**
     * Sets the piece that a pawn is promoted to.
     * Also marks this move as a promotion.
     *
     * @param piece The piece to promote to
     */
    public void setPromotedPiece(Piece piece) {
        this.promotedPiece = piece;
        this.isPromotion = true;
    }

    /**
     * Gets the piece that a pawn is promoted to.
     *
     * @return The promotion piece
     */
    public Piece getPromotedPiece() {
        return promotedPiece;
    }

    /**
     * Checks if this is a pawn promotion move.
     *
     * @return true if this is a promotion move, false otherwise
     */
    public boolean isPromotion() {
        return isPromotion;
    }

    /**
     * Checks if this move puts the opponent in check.
     *
     * @return true if this move puts the opponent in check, false otherwise
     */
    public boolean isCheck() {
        return isCheck;
    }

    /**
     * Sets whether this move puts the opponent in check.
     *
     * @param isCheck true if this move puts the opponent in check, false otherwise
     */
    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    /**
     * Checks if this move puts the opponent in checkmate.
     *
     * @return true if this move puts the opponent in checkmate, false otherwise
     */
    public boolean isCheckmate() {
        return isCheckmate;
    }

    /**
     * Sets whether this move puts the opponent in checkmate.
     *
     * @param isCheckmate true if this move puts the opponent in checkmate, false
     *                    otherwise
     */
    public void setCheckmate(boolean isCheckmate) {
        this.isCheckmate = isCheckmate;
    }

    /**
     * Checks if this move is equal to another object.
     * Two moves are considered equal if they have the same start and end positions.
     * For promotion moves, we only check if both moves are promotions, not the
     * specific promotion piece.
     *
     * @param o The object to compare with
     * @return true if the moves are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Move move = (Move) o;

        // Basic equality check for start and end positions
        if (!start.equals(move.start) || !end.equals(move.end)) {
            return false;
        }

        // For promotion moves, we need to check if both moves are promotions
        // We don't check the specific promotion piece here because that's handled
        // separately
        // in the BoardPanel when selecting a promotion piece
        return true;
    }

    /**
     * Returns a hash code for this move.
     * The hash code is based on the start and end positions.
     *
     * @return A hash code value for this move
     */
    @Override
    public int hashCode() {
        return 31 * start.hashCode() + end.hashCode();
    }
}
