package minesweeper;

import java.util.Random;

public class GameBoard {
    private final Cell[][] board;
    private boolean gameOver;
    private final int numMines;
    private int flagsCount;
    private final Random random;

    public GameBoard(int rows, int cols, int numMines) {
        // Initialize board
        this.board = new Cell[rows][cols];
        this.numMines = numMines;
        this.flagsCount = 0;
        this.random = new Random();
        this.gameOver = false;

        // Initialize the board
        initializeBoard();
        placeMines();
        calculateNeighboringMines();
    }

    private void initializeBoard() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                board[r][c] = new Cell(false); // Initialize with no mines
            }
        }
    }

    private void placeMines() {
        int placedMines = 0;
        while (placedMines < numMines) {
            int r = random.nextInt(board.length);
            int c = random.nextInt(board[0].length);
            if (!board[r][c].isMine()) {
                board[r][c] = new Cell(true);
                placedMines++;
            }
        }
    }

    private void calculateNeighboringMines() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (!board[r][c].isMine()) {
                    int count = countNeighboringMines(r, c);
                    board[r][c].setNeighboringMines(count);
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

    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int getMinesRemaining() {
        return numMines - flagsCount;
    }

    public int getRows() {
        return board.length;
    }
    
    public int getCols() {
        return board[0].length;
    }    
}
