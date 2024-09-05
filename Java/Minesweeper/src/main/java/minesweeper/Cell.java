package minesweeper;

public class Cell {
    private final int xPos;
    private final int yPos;
    private final boolean isMine;
    private boolean revealed;
    private boolean flagged;
    private boolean exploded;
    private int mineImageIndex;
    private int neighboringMines;

    public Cell(int row, int col, boolean isMine) {
        this.xPos = col * App.CELLSIZE; // Use constants from App
        this.yPos = row * App.CELLSIZE + App.TOPBAR;
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

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    // Setters
    public void reveal() {
        if (!flagged) {
            this.revealed = true;
        }
    }

    public boolean toggleFlag() {
        if (!revealed) {
            this.flagged = !this.flagged;
        }
        return this.flagged;
    }

    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }

    public void incrementMineImageIndex() {
        mineImageIndex++;
    }

    public void setNeighboringMines(int count) {
        this.neighboringMines = count;
    }
}
