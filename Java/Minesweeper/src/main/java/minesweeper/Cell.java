package minesweeper;

// public class Cell {
//     boolean isMine;
//     boolean isRevealed;
//     boolean isFlagged;
//     int neighboringMines;
// }

public class Cell {
    private final boolean isMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int neighboringMines;

    // Constructor
    public Cell(boolean isMine) {
        this.isMine = isMine;
        this.isRevealed = false;
        this.isFlagged = false;
        this.neighboringMines = 0;
    }

    // Getters
    public boolean isMine() {
        return isMine;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public int getNeighboringMines() {
        return neighboringMines;
    }

    // Setters
    public void setRevealed(boolean isRevealed) {
        this.isRevealed = isRevealed;
    }

    public void setFlagged(boolean isFlagged) {
        this.isFlagged = isFlagged;
    }

    public void setNeighboringMines(int neighboringMines) {
        this.neighboringMines = neighboringMines;
    }

    // Utility methods
    public void reveal() {
        if (!isFlagged) {
            this.isRevealed = true;
        }
    }

    public void toggleFlag() {
        if (!isRevealed) {
            this.isFlagged = !this.isFlagged;
        }
    }

    @Override
    public String toString() {
        return "Cell{" +
                "isMine=" + isMine +
                ", isRevealed=" + isRevealed +
                ", isFlagged=" + isFlagged +
                ", neighboringMines=" + neighboringMines +
                '}';
    }
}

