package minesweeper;

import java.util.Random;

public class GameBoard {
    private Cell[][] board;
    private boolean gameOver;
    private int numMines;
    private int flagsCount;
    private Random random;

    public GameBoard(int rows, int cols, int numMines) {
        // Initialize board
        this.board = new Cell[rows][cols];
        this.numMines = numMines;
        this.flagsCount = 0;
        this.random = new Random();
        this.gameOver = false;

        // Initialize the board
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = new Cell();
            }
        }


        placeMines();
        calculateNeighboringMines();
    }

    private void placeMines() {
        int placedMines = 0;
        while (placedMines < numMines) {
            int r = random.nextInt(board.length);
            int c = random.nextInt(board[0].length);
            if (!board[r][c].isMine) {
                board[r][c].isMine = true;
                placedMines++;
            }
        }
    }

    private void calculateNeighboringMines() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (!board[r][c].isMine) {
                    int count = countNeighboringMines(r, c);
                    board[r][c].neighboringMines = count;
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
                if (nr >= 0 && nr < board.length && nc >= 0 && nc < board[0].length && board[nr][nc].isMine) {
                    count++;
                }
            }
        }
        return count;
    }

    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getMinesRemaining() {
        return numMines - flagsCount;
    }
}
