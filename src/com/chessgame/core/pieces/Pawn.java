package com.chessgame.core.pieces;

import com.chessgame.core.board.Board;
import com.chessgame.core.board.Position;
import com.chessgame.core.game.Move;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(String color) {
        super(color);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board, Position currentPosition) {
        List<Move> legalMoves = new ArrayList<>();
        int direction = getColor().equals("White") ? 1 : -1;
        int startRow = getColor().equals("White") ? 1 : 6;
        int promotionRow = getColor().equals("White") ? 7 : 0;

        // Forward moves
        int newX = currentPosition.getX() + direction;
        int newY = currentPosition.getY();

        if (board.isValidPosition(newX, newY) && board.getPieceAt(newX, newY) == null) {
            addMoveWithPossiblePromotion(legalMoves, currentPosition, newX, newY, promotionRow);

            // Double move from start position
            if (currentPosition.getX() == startRow) {
                newX += direction;
                if (board.isValidPosition(newX, newY) && board.getPieceAt(newX, newY) == null) {
                    legalMoves.add(new Move(currentPosition, new Position(newX, newY)));
                }
            }
        }

        // Regular captures
        for (int offset : new int[] { -1, 1 }) {
            int captureX = currentPosition.getX() + direction;
            int captureY = currentPosition.getY() + offset;

            if (board.isValidPosition(captureX, captureY)) {
                Piece targetPiece = board.getPieceAt(captureX, captureY);
                if (targetPiece != null && !targetPiece.getColor().equals(this.getColor())) {
                    addMoveWithPossiblePromotion(legalMoves, currentPosition, captureX, captureY, promotionRow);
                }
            }
        }

        // En passant
        if ((getColor().equals("White") && currentPosition.getX() == 4) ||
                (getColor().equals("Black") && currentPosition.getX() == 3)) {

            Move lastMove = board.getLastMove();
            if (lastMove != null && lastMove.getMovingPiece() instanceof Pawn) {
                int lastMoveStart = lastMove.getStart().getX();
                int lastMoveEnd = lastMove.getEnd().getX();

                // Check if the last move was a pawn double move
                if (Math.abs(lastMoveStart - lastMoveEnd) == 2) {
                    int lastMoveY = lastMove.getEnd().getY();

                    // Check if the enemy pawn is adjacent
                    if (Math.abs(lastMoveY - currentPosition.getY()) == 1) {
                        Position enPassantPos = new Position(
                                currentPosition.getX() + direction,
                                lastMoveY);
                        Position capturedPawnPos = new Position(lastMove.getEnd().getX(), lastMove.getEnd().getY());
                        Move enPassantMove = new Move(currentPosition, enPassantPos);
                        enPassantMove.setEnPassant(true);
                        enPassantMove.setEnPassantCapturePosition(capturedPawnPos);
                        legalMoves.add(enPassantMove);
                    }
                }
            }
        }

        return legalMoves;
    }

    private void addMoveWithPossiblePromotion(List<Move> moves, Position current, int newX, int newY,
            int promotionRow) {
        Move move = new Move(current, new Position(newX, newY));

        if (newX == promotionRow) {
            // Create separate moves for each possible promotion piece
            moves.add(createPromotionMove(move, new Queen(this.getColor())));
            moves.add(createPromotionMove(move, new Rook(this.getColor())));
            moves.add(createPromotionMove(move, new Bishop(this.getColor())));
            moves.add(createPromotionMove(move, new Knight(this.getColor())));
        } else {
            moves.add(move);
        }
    }

    private Move createPromotionMove(Move baseMove, Piece promotedPiece) {
        Move promotionMove = new Move(baseMove.getStart(), baseMove.getEnd());
        promotionMove.setPromotedPiece(promotedPiece);
        return promotionMove;
    }

    @Override
    public Piece copy() {
        Pawn newPawn = new Pawn(this.getColor());
        if (this.hasMoved()) {
            newPawn.setHasMoved(true);
        }
        return newPawn;
    }
}
