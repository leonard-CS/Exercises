package minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameBoard {
    private final Cell[][] board;
    public final int NUM_TILES;
    private boolean win;
    private boolean gameOver;
    private final int numMines;
    private int flagsCount;
    private int revealCount;
    private final Random random;
    public List<Cell> minesToExplode;
    public List<Cell> minesExploding;

    public GameBoard(int rows, int cols, int numMines, Random random) {
        this.board = new Cell[rows][cols];
        this.NUM_TILES = rows * cols;
        this.numMines = numMines;
        this.flagsCount = numMines;
        this.revealCount = 0;
        this.random = random;
        this.win = false;
        this.gameOver = false;
        this.minesToExplode = new ArrayList<>();
        this.minesExploding = new ArrayList<>();

        initializeBoard();
        placeMines();
        calculateNeighboringMines();
    }

    private void initializeBoard() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                board[r][c] = new Cell(r, c, false); // Initialize empty cells
            }
        }
    }

    private void placeMines() {
        int placedMines = 0;
        while (placedMines < numMines) {
            int r = random.nextInt(board.length);
            int c = random.nextInt(board[0].length);
            if (!board[r][c].isMine()) {
                board[r][c] = new Cell(r, c, true); // Place mine
                minesToExplode.add(board[r][c]);
                placedMines++;
            }
        }
    }

    private void calculateNeighboringMines() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (!board[r][c].isMine()) {
                    board[r][c].setNeighboringMines(countNeighboringMines(r, c));
                }
            }
        }
    }

    private int countNeighboringMines(int r, int c) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nr = r + i;
                int nc = c + j;
                if (isValidPosition(nr, nc) && board[nr][nc].isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isValidPosition(int r, int c) {
        return r >= 0 && r < board.length && c >= 0 && c < board[0].length;
    }

    // Getters and setters
    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int getRows() {
        return board.length;
    }

    public int getCols() {
        return board[0].length;
    }

    public int getRevealCount() {
        return revealCount;
    }

    public int getFlagsCount() {
        return flagsCount;
    }

    public void incrementFlagsCount() {
        flagsCount++;
    }

    public void decrementFlagsCount() {
        flagsCount--;
    }

    public void incrementRevealCount() {
        revealCount++;
    }

    // Utility methods
    public void updateExplodingMines() {
        for (Cell cell : minesExploding) {
            if (cell.getMineImageIndex() < App.NUM_MINE_IMAGES) {
                cell.incrementMineImageIndex();
            }
        }
        if (minesToExplode.size() > 0) {
            minesExploding.add(minesToExplode.remove(0));
        }
    }

    public List<Cell> getMinesExploding() {
        return minesExploding;
    }
}
