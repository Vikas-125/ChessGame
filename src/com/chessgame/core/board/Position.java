package com.chessgame.core.board;

/**
 * Represents a position on the chess board.
 * Uses zero-based coordinates where (0,0) is the bottom-left corner.
 */
public class Position {
    /** The x-coordinate (rank, 0-7) */
    private int x;

    /** The y-coordinate (file, 0-7) */
    private int y;

    /**
     * Creates a new position with the specified coordinates.
     *
     * @param x The x-coordinate (0-7)
     * @param y The y-coordinate (0-7)
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
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
     * Checks if this position is equal to another object.
     * Two positions are equal if they have the same x and y coordinates.
     *
     * @param o The object to compare with
     * @return true if the positions are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    /**
     * Returns a hash code for this position.
     *
     * @return A hash code value for this position
     */
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
