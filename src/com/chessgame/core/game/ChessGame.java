package com.chessgame.core.game;

import com.chessgame.core.board.Board;
import com.chessgame.core.board.Position;
import com.chessgame.core.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ChessGame implements GameState {
    private GameMode gameMode;
    private String playerColor = "White"; // Default color
    private boolean isPassAndPlay = false;

    // Constants
    private static final String WHITE = "White";
    private static final String BLACK = "Black";

    // Game state
    private final Board board;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private Player currentPlayer;
    private final List<Move> moveHistory;
    private boolean isInReviewMode;
    private Move lastMove;
    private int currentMoveIndex;
    private int movesSincePawnMoveOrCapture = 0;

    // Constructor and initialization
    public ChessGame() {
        this.board = new Board();
        this.whitePlayer = new Player(WHITE);
        this.blackPlayer = new Player(BLACK);
        this.currentPlayer = whitePlayer;
        this.moveHistory = new ArrayList<>();
        this.isInReviewMode = false;
        this.lastMove = null;
        this.currentMoveIndex = -1;
        this.gameMode = GameMode.LOCAL; // Default mode
    }

    public void setGameMode(GameMode mode) {
        this.gameMode = mode;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setPlayerColor(String color) {
        // Convert to lowercase for case-insensitive comparison
        String normalizedColor = color.toLowerCase();
        switch (normalizedColor) {
            case "white":
                this.playerColor = "White";
                break;
            case "black":
                this.playerColor = "Black";
                break;
            case "random":
                this.playerColor = Math.random() < 0.5 ? "White" : "Black";
                break;
            default:
                throw new IllegalArgumentException("Color must be either 'white', 'black', or 'random'");
        }
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPassAndPlay(boolean enabled) {
        this.isPassAndPlay = enabled;
    }

    public boolean isPassAndPlay() {
        return isPassAndPlay;
    }

    // Core game logic
    public boolean movePiece(Position start, Position end) {
        if (isInReviewMode) {
            return false;
        }

        if (!isValidMove(start, end)) {
            return false;
        }

        Piece movingPiece = board.getPieceAt(start.getX(), start.getY());
        Piece capturedPiece = board.getPieceAt(end.getX(), end.getY());

        // Create the move using createMove method
        Move move = createMove(start, end, movingPiece, capturedPiece);

        // Execute the move
        executeMove(move);

        // Update game state
        updateGameState(move);

        return true;
    }

    // Overloaded method to handle promotion moves directly
    public boolean movePiece(Move move) {
        if (isInReviewMode) {
            return false;
        }

        Position start = move.getStart();
        Position end = move.getEnd();

        if (!isValidMove(start, end)) {
            return false;
        }

        // Execute the move directly
        executeMove(move);

        // Update game state
        updateGameState(move);

        return true;
    }

    private boolean isValidMove(Position start, Position end) {
        if (!isLegalMove(start, end)) {
            return false;
        }
        return !wouldPutKingInCheck(start, end);
    }

    private boolean isLegalMove(Position start, Position end) {
        Piece piece = board.getPieceAt(start.getX(), start.getY());
        return piece != null &&
                piece.getColor().equals(currentPlayer.getColor()) &&
                piece.calculateLegalMoves(board, start).contains(new Move(start, end));
    }

    public boolean wouldPutKingInCheck(Position start, Position end) {
        Piece movingPiece = board.getPieceAt(start.getX(), start.getY());
        Piece capturedPiece = board.getPieceAt(end.getX(), end.getY());

        board.setPieceAt(end.getX(), end.getY(), movingPiece);
        board.setPieceAt(start.getX(), start.getY(), null);

        boolean inCheck = isCheck();

        board.setPieceAt(start.getX(), start.getY(), movingPiece);
        board.setPieceAt(end.getX(), end.getY(), capturedPiece);

        return inCheck;
    }

    private Move createMove(Position start, Position end, Piece movingPiece, Piece capturedPiece) {
        Move move = new Move(start, end);
        move.setMovingPiece(movingPiece);
        move.setCapturedPiece(capturedPiece);

        // Handle special moves
        if (isKingCastling(movingPiece, start, end)) {
            handleCastling(move); // Use the handleCastling method instead of inline logic
        }

        if (isEnPassantCapture(movingPiece, start, end)) {
            handleEnPassant(move);
        }

        // Execute move temporarily to check for check/checkmate
        executeMove(move);

        // Switch turn temporarily to check from opponent's perspective
        switchTurn();
        boolean isCheck = isCheck();
        boolean isCheckmate = isCheckmate();
        switchTurn(); // Switch back

        // Undo the move
        undoMove(move);

        // Set the check and checkmate flags
        move.setCheck(isCheck);
        move.setCheckmate(isCheckmate);

        // Set the move notation
        String notation = generateMoveNotation(move);
        move.setNotation(notation);

        return move;
    }

    private String generateMoveNotation(Move move) {
        StringBuilder notation = new StringBuilder();
        Piece piece = move.getMovingPiece();

        // Handle castling
        if (move.isCastling()) {
            notation.append(move.getEnd().getY() > move.getStart().getY() ? "O-O" : "O-O-O");
        } else {
            // Add piece letter (except for pawns)
            if (!(piece instanceof Pawn)) {
                if (piece instanceof King)
                    notation.append("K");
                else if (piece instanceof Queen)
                    notation.append("Q");
                else if (piece instanceof Rook)
                    notation.append("R");
                else if (piece instanceof Bishop)
                    notation.append("B");
                else if (piece instanceof Knight)
                    notation.append("N");
            }

            // Add capture symbol
            if (move.getCapturedPiece() != null ||
                    (piece instanceof Pawn && move.getStart().getY() != move.getEnd().getY())) {
                if (piece instanceof Pawn) {
                    notation.append((char) ('a' + move.getStart().getY()));
                }
                notation.append("x");
            }

            // Add destination square
            notation.append((char) ('a' + move.getEnd().getY()))
                    .append(move.getEnd().getX() + 1);
        }

        // Add check or checkmate symbol
        if (move.isCheckmate()) {
            notation.append("#");
        } else if (move.isCheck()) {
            notation.append("+");
        }

        return notation.toString();
    }

    private boolean isKingCastling(Piece movingPiece, Position start, Position end) {
        if (!(movingPiece instanceof King)) {
            return false;
        }

        // Verify this is a horizontal move of 2 squares
        return Math.abs(end.getY() - start.getY()) == 2 && start.getX() == end.getX();
    }

    private boolean isEnPassantCapture(Piece movingPiece, Position start, Position end) {
        return movingPiece instanceof Pawn &&
                Math.abs(end.getY() - start.getY()) == 1 &&
                board.getPieceAt(end.getX(), end.getY()) == null &&
                lastMove != null &&
                lastMove.getMovingPiece() instanceof Pawn &&
                Math.abs(lastMove.getStart().getX() - lastMove.getEnd().getX()) == 2;
    }

    private void handleEnPassant(Move move) {
        Position capturedPawnPosition = new Position(lastMove.getEnd().getX(), lastMove.getEnd().getY());
        Piece capturedPawn = board.getPieceAt(capturedPawnPosition.getX(), capturedPawnPosition.getY());
        move.setCapturedPiece(capturedPawn);
        move.setEnPassant(true);
        move.setEnPassantCapturePosition(capturedPawnPosition);
    }

    private void handleCastling(Move move) {
        Position start = move.getStart();
        Position end = move.getEnd();

        // Determine if it's kingside or queenside castling
        boolean isKingSide = end.getY() > start.getY();

        // Set up rook positions
        int rookY = isKingSide ? 7 : 0;
        Position rookStart = new Position(start.getX(), rookY);
        Position rookEnd = new Position(start.getX(), isKingSide ? 5 : 3);

        // Get the rook
        Piece rook = board.getPieceAt(rookStart.getX(), rookStart.getY());

        // Set the castling information in the move
        if (rook != null && rook instanceof Rook) {
            move.setCastlingMove(rookStart, rookEnd, rook);
        }
    }

    private void executeMove(Move move) {
        Position start = move.getStart();
        Position end = move.getEnd();
        Piece movingPiece = move.getMovingPiece();

        // Handle promotion
        if (move.isPromotion()) {
            board.setPieceAt(end.getX(), end.getY(), move.getPromotedPiece());
            board.setPieceAt(start.getX(), start.getY(), null);
            return;
        }

        // Update piece's moved status if it's a King or Rook
        if (movingPiece instanceof King) {
            ((King) movingPiece).setHasMoved(true);
        } else if (movingPiece instanceof Rook) {
            ((Rook) movingPiece).setHasMoved(true);
        }

        // Handle castling
        if (move.isCastling()) {
            Position rookStart = move.getCastlingRookStart();
            Position rookEnd = move.getCastlingRookEnd();

            // Add null checks and validation
            Piece rook = board.getPieceAt(rookStart.getX(), rookStart.getY());
            if (rook != null && rook instanceof Rook) {
                board.setPieceAt(rookEnd.getX(), rookEnd.getY(), rook);
                board.setPieceAt(rookStart.getX(), rookStart.getY(), null);
                ((Rook) rook).setHasMoved(true);
            }
        }

        // Handle en passant
        if (move.isEnPassant()) {
            Position capturePos = move.getEnPassantCapturePosition();
            if (capturePos != null) {
                Piece capturedPawn = board.getPieceAt(capturePos.getX(), capturePos.getY());
                if (capturedPawn != null) {
                    board.setPieceAt(capturePos.getX(), capturePos.getY(), null);
                    move.setCapturedPiece(capturedPawn);
                }
            }
        }

        // Execute the main move
        board.setPieceAt(end.getX(), end.getY(), movingPiece);
        board.setPieceAt(start.getX(), start.getY(), null);
    }

    private void updateGameState(Move move) {
        // Update fifty-move counter
        if (move.getMovingPiece() instanceof Pawn || move.getCapturedPiece() != null) {
            movesSincePawnMoveOrCapture = 0;
        } else {
            movesSincePawnMoveOrCapture++;
        }

        if (!isInReviewMode) {
            moveHistory.add(move);
            currentMoveIndex = moveHistory.size() - 1;
        } else {
            truncateMoveHistory();
            moveHistory.add(move);
            currentMoveIndex++;
            isInReviewMode = false;
        }

        lastMove = move;
        board.setLastMove(move);
        switchTurn();
    }

    private void truncateMoveHistory() {
        while (moveHistory.size() > currentMoveIndex + 1) {
            moveHistory.remove(moveHistory.size() - 1);
        }
    }

    // Game state checks
    public boolean isCheck() {
        Position kingPos = findKingPosition(currentPlayer.getColor());
        if (kingPos == null)
            return false;

        String oppositeColor = currentPlayer.getColor().equals(WHITE) ? BLACK : WHITE;

        // Check all opponent pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPieceAt(i, j);
                if (piece != null && piece.getColor().equals(oppositeColor)) {
                    Position piecePos = new Position(i, j);
                    List<Move> moves = piece.calculateLegalMoves(board, piecePos);

                    // Check if any move can capture the king
                    for (Move move : moves) {
                        if (move.getEnd().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isCheckmate() {
        // First verify that the current player is in check
        if (!isCheck()) {
            return false;
        }

        // Then verify that there are no legal moves that can get out of check
        return !canPlayerEscapeCheck();
    }

    private boolean canPlayerEscapeCheck() {
        // Check all pieces of the current player
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPieceAt(i, j);
                if (piece != null && piece.getColor().equals(currentPlayer.getColor())) {
                    Position start = new Position(i, j);
                    List<Move> moves = piece.calculateLegalMoves(board, start);

                    // Try each move to see if it gets out of check
                    for (Move move : moves) {
                        Position end = move.getEnd();

                        // Save current board state
                        Piece capturedPiece = board.getPieceAt(end.getX(), end.getY());

                        // Make the move
                        board.setPieceAt(end.getX(), end.getY(), piece);
                        board.setPieceAt(start.getX(), start.getY(), null);

                        // Check if we're still in check after this move
                        boolean stillInCheck = isCheck();

                        // Restore the board
                        board.setPieceAt(start.getX(), start.getY(), piece);
                        board.setPieceAt(end.getX(), end.getY(), capturedPiece);

                        // If this move gets us out of check, return true
                        if (!stillInCheck) {
                            return true;
                        }
                    }
                }
            }
        }
        // No moves found that escape check
        return false;
    }

    @Override
    public boolean isGameOver() {
        return isCheckmate() ||
                isStalemate() ||
                hasInsufficientMaterial() ||
                isFiftyMoveRule() ||
                isThreefoldRepetition();
    }

    public String getGameResult() {
        if (isCheckmate()) {
            String winner = currentPlayer.getColor().equals("White") ? "Black" : "White";
            return winner + " wins by checkmate!";
        } else if (isStalemate()) {
            return "Game drawn by stalemate!";
        } else if (hasInsufficientMaterial()) {
            return "Game drawn due to insufficient material!";
        } else if (isFiftyMoveRule()) {
            return "Game drawn by fifty-move rule!";
        } else if (isThreefoldRepetition()) {
            return "Game drawn by threefold repetition!";
        }
        return "Game in progress";
    }

    public boolean isStalemate() {
        if (isCheck()) {
            return false;
        }
        return !canPlayerEscapeCheck(); // Reusing this method as it checks for any legal moves
    }

    public boolean hasInsufficientMaterial() {
        List<Piece> remainingPieces = new ArrayList<>();

        // Collect all remaining pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPieceAt(i, j);
                if (piece != null) {
                    remainingPieces.add(piece);
                }
            }
        }

        // King vs King
        if (remainingPieces.size() == 2) {
            return true;
        }

        // King and Bishop/Knight vs King
        if (remainingPieces.size() == 3) {
            boolean hasOnlyOneMinorPiece = remainingPieces.stream()
                    .filter(p -> !(p instanceof King))
                    .allMatch(p -> p instanceof Bishop || p instanceof Knight);
            return hasOnlyOneMinorPiece;
        }

        // King and Bishop vs King and Bishop (same colored squares)
        if (remainingPieces.size() == 4) {
            List<Piece> bishops = remainingPieces.stream()
                    .filter(p -> p instanceof Bishop)
                    .toList();

            if (bishops.size() == 2) {
                // Check if bishops are on same colored squares
                boolean sameBishopColor = true;
                for (int i = 0; i < 8 && sameBishopColor; i++) {
                    for (int j = 0; j < 8; j++) {
                        Piece piece = board.getPieceAt(i, j);
                        if (piece instanceof Bishop) {
                            // Square color is determined by sum of coordinates being even/odd
                            boolean isLightSquare = (i + j) % 2 == 0;
                            sameBishopColor = sameBishopColor && isLightSquare;
                        }
                    }
                }
                return sameBishopColor;
            }
        }

        return false;
    }

    // Move history navigation
    public boolean moveBackward() {
        if (!canMoveBackward())
            return false;

        undoLastMove();
        currentMoveIndex--;
        isInReviewMode = true;
        return true;
    }

    public boolean moveForward() {
        if (!canMoveForward())
            return false;

        currentMoveIndex++;
        redoMove(moveHistory.get(currentMoveIndex));
        isInReviewMode = (currentMoveIndex < moveHistory.size() - 1);
        return true;
    }

    // Utility methods
    private void switchTurn() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }

    private Position findKingPosition(String color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPieceAt(i, j);
                if (piece instanceof King && piece.getColor().equals(color)) {
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    // Getters
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }

    public int getCurrentMoveIndex() {
        return currentMoveIndex;
    }

    /**
     * Sets the current move index
     * This should only be used in conjunction with resetAndReplayMoves
     *
     * @param index the new move index
     */
    public void setCurrentMoveIndex(int index) {
        if (index >= -1 && index < moveHistory.size()) {
            currentMoveIndex = index;
        }
    }

    public boolean isInReviewMode() {
        return isInReviewMode;
    }

    public boolean canMoveBackward() {
        return currentMoveIndex >= 0;
    }

    public boolean canMoveForward() {
        return currentMoveIndex < moveHistory.size() - 1;
    }

    private void undoLastMove() {
        Move moveToUndo = moveHistory.get(currentMoveIndex);

        // Handle promotion
        if (moveToUndo.isPromotion()) {
            // Restore the pawn to its original position
            board.setPieceAt(moveToUndo.getStart().getX(), moveToUndo.getStart().getY(), moveToUndo.getMovingPiece());
            board.setPieceAt(moveToUndo.getEnd().getX(), moveToUndo.getEnd().getY(), moveToUndo.getCapturedPiece());
        } else {
            // Restore the moving piece to its original position
            board.setPieceAt(moveToUndo.getStart().getX(), moveToUndo.getStart().getY(), moveToUndo.getMovingPiece());
            board.setPieceAt(moveToUndo.getEnd().getX(), moveToUndo.getEnd().getY(), moveToUndo.getCapturedPiece());
        }

        // Handle special cases
        if (moveToUndo.isCastling()) {
            // Undo rook movement for castling
            Position rookStart = moveToUndo.getCastlingRookStart();
            Position rookEnd = moveToUndo.getCastlingRookEnd();
            Piece rook = board.getPieceAt(rookEnd.getX(), rookEnd.getY());
            board.setPieceAt(rookStart.getX(), rookStart.getY(), rook);
            board.setPieceAt(rookEnd.getX(), rookEnd.getY(), null);
        }

        // Handle en passant
        if (moveToUndo.isEnPassant()) {
            // Restore the captured pawn
            Position capturePos = moveToUndo.getEnPassantCapturePosition();
            if (capturePos != null) {
                board.setPieceAt(capturePos.getX(), capturePos.getY(), moveToUndo.getCapturedPiece());
            }
        }

        // Update game state
        lastMove = currentMoveIndex > 0 ? moveHistory.get(currentMoveIndex - 1) : null;
        board.setLastMove(lastMove);
        switchTurn();
    }

    private void undoMove(Move move) {
        Position start = move.getStart();
        Position end = move.getEnd();
        Piece movingPiece = move.getMovingPiece();
        Piece capturedPiece = move.getCapturedPiece();

        // Handle promotion
        if (move.isPromotion()) {
            // Restore the pawn to its original position
            board.setPieceAt(start.getX(), start.getY(), movingPiece);
            board.setPieceAt(end.getX(), end.getY(), capturedPiece);
        } else {
            // Restore the captured piece
            board.setPieceAt(end.getX(), end.getY(), capturedPiece);
            board.setPieceAt(start.getX(), start.getY(), movingPiece);
        }

        // Handle castling
        if (move.isCastling()) {
            Position rookStart = move.getCastlingRookStart();
            Position rookEnd = move.getCastlingRookEnd();
            Piece rook = board.getPieceAt(rookEnd.getX(), rookEnd.getY());
            board.setPieceAt(rookStart.getX(), rookStart.getY(), rook);
            board.setPieceAt(rookEnd.getX(), rookEnd.getY(), null);
        }

        // Handle en passant
        if (move.isEnPassant()) {
            Position capturePos = move.getEnPassantCapturePosition();
            if (capturePos != null) {
                Piece capturedPawn = move.getCapturedPiece();
                board.setPieceAt(capturePos.getX(), capturePos.getY(), capturedPawn);
            }
        }

        // Update piece's moved status if it's a King or Rook
        if (movingPiece instanceof King) {
            ((King) movingPiece).setHasMoved(false);
        } else if (movingPiece instanceof Rook) {
            ((Rook) movingPiece).setHasMoved(false);
        }
    }

    private void redoMove(Move move) {
        // Handle promotion
        if (move.isPromotion()) {
            board.setPieceAt(move.getEnd().getX(), move.getEnd().getY(), move.getPromotedPiece());
            board.setPieceAt(move.getStart().getX(), move.getStart().getY(), null);
        } else {
            // Move the piece to its destination
            board.setPieceAt(move.getEnd().getX(), move.getEnd().getY(), move.getMovingPiece());
            board.setPieceAt(move.getStart().getX(), move.getStart().getY(), null);
        }

        // Handle special cases
        if (move.isCastling()) {
            // Move the rook for castling
            Position rookStart = move.getCastlingRookStart();
            Position rookEnd = move.getCastlingRookEnd();
            Piece rook = board.getPieceAt(rookStart.getX(), rookStart.getY());
            board.setPieceAt(rookEnd.getX(), rookEnd.getY(), rook);
            board.setPieceAt(rookStart.getX(), rookStart.getY(), null);
        }

        // Handle en passant
        if (move.isEnPassant()) {
            Position capturePos = move.getEnPassantCapturePosition();
            if (capturePos != null) {
                // Remove the captured pawn
                board.setPieceAt(capturePos.getX(), capturePos.getY(), null);
            }
        }

        // Update game state
        lastMove = move;
        board.setLastMove(move);
        switchTurn();
    }

    /**
     * Returns the last move made in the game
     *
     * @return the last move made, or null if no moves have been made
     */
    public Move getLastMove() {
        if (moveHistory.isEmpty() || currentMoveIndex < 0) {
            return null;
        }
        return moveHistory.get(currentMoveIndex);
    }

    /**
     * Resets the game to its initial state
     */
    public void reset() {
        // Reset the board to initial position
        board.initialize();

        // Reset game state
        currentPlayer = whitePlayer;
        lastMove = null;
        board.setLastMove(null);
        currentMoveIndex = -1;
        movesSincePawnMoveOrCapture = 0;

        // Set review mode if we have move history
        // This prevents making new moves when reviewing history
        isInReviewMode = !moveHistory.isEmpty();

        // Don't clear move history when resetting for navigation
        // This allows us to navigate back to later moves
    }

    /**
     * Resets the board to the initial position and replays all moves up to the
     * current move index
     * This ensures the board state is correct even with complex moves like en
     * passant
     */
    public void resetAndReplayMoves() {
        // Save the current move index
        int targetIndex = currentMoveIndex;

        // Reset the board
        board.initialize();
        currentPlayer = whitePlayer;
        lastMove = null;
        board.setLastMove(null);
        currentMoveIndex = -1;
        movesSincePawnMoveOrCapture = 0;

        // Replay all moves up to the target index
        for (int i = 0; i <= targetIndex; i++) {
            Move move = moveHistory.get(i);
            executeMove(move);
            lastMove = move;
            board.setLastMove(move);
            switchTurn();
        }

        // Set the current move index back to the target
        currentMoveIndex = targetIndex;

        // Set review mode if we're not at the end of the move history
        isInReviewMode = (currentMoveIndex < moveHistory.size() - 1);
    }

    public boolean isFiftyMoveRule() {
        return movesSincePawnMoveOrCapture >= 100; // 50 moves by each player = 100 half-moves
    }

    private String getPositionKey() {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPieceAt(i, j);
                if (piece != null) {
                    key.append(piece.getClass().getSimpleName())
                            .append(piece.getColor())
                            .append(i)
                            .append(j);
                }
            }
        }
        key.append(currentPlayer.getColor()); // Include current player to move
        return key.toString();
    }

    public boolean isThreefoldRepetition() {
        Map<String, Integer> positionCount = new HashMap<>();

        // Count initial position
        String currentPos = getPositionKey();
        positionCount.put(currentPos, 1);

        // Replay all moves and count positions
        Board tempBoard = board.copy();
        for (int i = 0; i < moveHistory.size(); i++) {
            Move move = moveHistory.get(i);

            // Apply the move to get the position after this move
            applyMove(tempBoard, move);

            // Get position key and count it
            String position = getPositionKeyForBoard(tempBoard);
            positionCount.merge(position, 1, Integer::sum);

            if (positionCount.get(position) >= 3) {
                return true;
            }
        }

        return false;
    }

    private void applyMove(Board board, Move move) {
        // Apply the move to the temporary board
        Piece movingPiece = board.getPieceAt(move.getStart().getX(), move.getStart().getY());
        board.setPieceAt(move.getEnd().getX(), move.getEnd().getY(), movingPiece);
        board.setPieceAt(move.getStart().getX(), move.getStart().getY(), null);

        // Handle castling if necessary
        if (move.isCastling()) {
            Position rookStart = move.getCastlingRookStart();
            Position rookEnd = move.getCastlingRookEnd();
            Piece rook = board.getPieceAt(rookStart.getX(), rookStart.getY());
            board.setPieceAt(rookEnd.getX(), rookEnd.getY(), rook);
            board.setPieceAt(rookStart.getX(), rookStart.getY(), null);
        }
    }

    private String getPositionKeyForBoard(Board board) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPieceAt(i, j);
                if (piece != null) {
                    key.append(piece.getClass().getSimpleName())
                            .append(piece.getColor())
                            .append(i)
                            .append(j);
                }
            }
        }
        // Include whose turn it is
        key.append(currentPlayer.getColor());
        return key.toString();
    }

    public int evaluatePosition() {
        // Simple material evaluation
        int evaluation = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPieceAt(i, j);
                if (piece != null) {
                    int value = getPieceValue(piece);
                    if (piece.getColor().equals("White")) {
                        evaluation += value;
                    } else {
                        evaluation -= value;
                    }
                }
            }
        }
        return evaluation;
    }

    private int getPieceValue(Piece piece) {
        if (piece instanceof Pawn)
            return 1;
        if (piece instanceof Knight)
            return 3;
        if (piece instanceof Bishop)
            return 3;
        if (piece instanceof Rook)
            return 5;
        if (piece instanceof Queen)
            return 9;
        if (piece instanceof King)
            return 0;
        return 0;
    }
}