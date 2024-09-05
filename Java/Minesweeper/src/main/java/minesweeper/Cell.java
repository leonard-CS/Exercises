package minesweeper;

// public class Cell {
//     boolean isMine;
//     boolean isRevealed;
//     boolean isFlagged;
//     int neighboringMines;
// }

public class Cell {
    public final int xPos;
    public final int yPos;
    private final boolean isMine;
    private boolean revealed;
    private boolean flagged;
    private boolean exploded;
    private int mineImageIndex;
    private int neighboringMines;

    // Constructor
    public Cell(int row, int col, boolean isMine) {
        this.xPos = col * 32; //col + CELLSIZE
        this.yPos = row * 32 + 64; //row * CELLSIZE + TOPBAR;
        this.isMine = isMine;
        this.revealed = false;
        this.flagged = false;
        this.exploded = false;
        this.mineImageIndex = 0;
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

    public int getMineImageIndex() {
        return mineImageIndex;
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

    public void inccMineImageIndex() {
        mineImageIndex++;
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
}
