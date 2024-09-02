package minesweeper;

// public class Cell {
//     boolean isMine;
//     boolean isRevealed;
//     boolean isFlagged;
//     int neighboringMines;
// }

public class Cell {
    private final boolean isMine;
    private boolean revealed;
    private boolean flagged;
    private boolean exploded;
    private int explodeFrame;
    private int neighboringMines;

    // Constructor
    public Cell(boolean isMine) {
        this.isMine = isMine;
        this.revealed = false;
        this.flagged = false;
        this.exploded = false;
        this.neighboringMines = 0;
    }

    // Getters
    public boolean isMine() {
        return isMine;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public boolean isExploded() {
        return exploded;
    }

    public int getNeighboringMines() {
        return neighboringMines;
    }

    // Setters
    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }

    public void setNeighboringMines(int neighboringMines) {
        this.neighboringMines = neighboringMines;
    }

    // Utility methods
    public boolean reveal() {
        if (!flagged) {
            this.revealed = true;
            return true;
        }
        return false;
    }

    public void toggleFlag() {
        if (!revealed) {
            this.flagged = !this.flagged;
        }
    }

    @Override
    public String toString() {
        return "Cell{" +
                "isMine=" + isMine +
                ", isRevealed=" + revealed +
                ", isFlagged=" + flagged +
                ", neighboringMines=" + neighboringMines +
                '}';
    }
}

