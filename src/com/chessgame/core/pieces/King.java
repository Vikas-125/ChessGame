package com.chessgame.core.pieces;

import com.chessgame.core.board.Board;
import com.chessgame.core.board.Position;
import com.chessgame.core.game.Move;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(String color) {
        super(color);
    }

    @Override
    public Piece copy() {
        King newKing = new King(this.getColor());
        if (this.hasMoved()) {
            newKing.setHasMoved(true);
        }
        return newKing;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    @Override
    public List<Move> calculateLegalMoves(Board board, Position currentPosition) {
        List<Move> legalMoves = new ArrayList<>();
        int[][] moveOffsets = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},     // Horizontal and vertical moves
            {1, 1}, {-1, -1}, {1, -1}, {-1, 1}    // Diagonal moves
        };

        // Normal king moves
        for (int[] offset : moveOffsets) {
            int newX = currentPosition.getX() + offset[0];  // Fixed: Add offset[0] to X coordinate
            int newY = currentPosition.getY() + offset[1];

            if (board.isValidPosition(newX, newY)) {
                Piece targetPiece = board.getPieceAt(newX, newY);
                if (targetPiece == null || !targetPiece.getColor().equals(this.getColor())) {
                    legalMoves.add(new Move(currentPosition, new Position(newX, newY)));
                }
            }
        }

        // Castling moves
        if (!hasMoved) {
            int row = currentPosition.getX();
            // Kingside castling
            if (canCastle(board, currentPosition, true)) {
                Position endPos = new Position(row, currentPosition.getY() + 2);
                legalMoves.add(new Move(currentPosition, endPos));
            }
            // Queenside castling
            if (canCastle(board, currentPosition, false)) {
                Position endPos = new Position(row, currentPosition.getY() - 2);
                legalMoves.add(new Move(currentPosition, endPos));
            }
        }

        return legalMoves;
    }

    private boolean canCastle(Board board, Position kingPos, boolean kingSide) {
        int row = kingPos.getX();
        int rookY = kingSide ? 7 : 0;
        
        // Check if rook is in place and hasn't moved
        Piece rook = board.getPieceAt(row, rookY);
        if (!(rook instanceof Rook) || ((Rook) rook).hasMoved()) {
            return false;
        }

        // Check if path is clear
        int start = kingSide ? kingPos.getY() + 1 : 1;
        int end = kingSide ? 6 : kingPos.getY() - 1;
        for (int y = start; y <= end; y++) {
            if (board.getPieceAt(row, y) != null) {
                return false;
            }
        }

        // Check if king is not in check and path is not under attack
        int direction = kingSide ? 1 : -1;
        for (int y = kingPos.getY(); y != kingPos.getY() + (3 * direction); y += direction) {
            Position pos = new Position(row, y);
            if (isPositionUnderAttack(board, pos)) {
                return false;
            }
        }

        return true;
    }

    private boolean isPositionUnderAttack(Board board, Position pos) {
        String oppositeColor = this.getColor().equals("White") ? "Black" : "White";
        
        // Check all positions on the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPieceAt(i, j);
                // If we find an attacking piece
                if (piece != null && piece.getColor().equals(oppositeColor)) {
                    Position piecePos = new Position(i, j);
                    
                    // Skip other kings to prevent infinite recursion
                    if (piece instanceof King) {
                        // For kings, just check if the position is one move away
                        int dx = Math.abs(pos.getX() - piecePos.getX());
                        int dy = Math.abs(pos.getY() - piecePos.getY());
                        if (dx <= 1 && dy <= 1) {
                            return true;
                        }
                        continue;
                    }
                    
                    // For other pieces, check their legal moves
                    List<Move> moves = piece.calculateLegalMoves(board, piecePos);
                    for (Move move : moves) {
                        if (move.getEnd().equals(pos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
