package inkball;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import processing.core.PImage;

public class GameBoard {
    public final int numRows;
    public final int numCols;
    private Cell[][] board;

    // Images
    private final PImage[] holeImages;
    private final PImage entryPointImage;
    private final PImage[] wallImages;
    private final PImage tileImage;

    public GameBoard(int numRows, int numCols, String layout, PImage entryPointImage, PImage tileImage, PImage[] holeImages, PImage[] wallImages) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];
        this.entryPointImage = entryPointImage;
        this.tileImage = tileImage;
        this.holeImages = holeImages;
        this.wallImages = wallImages;
        createBoard(layout);
    }

    private void createBoard(String filePath) {
        initializeBoardWithTiles();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
                String line = br.readLine();
                if (line != null) {
                    for (int colIndex = 0; colIndex < numCols; colIndex++) {
                        char cellChar = line.charAt(colIndex);
                        switch (cellChar) {
                            case 'X':
                                board[rowIndex][colIndex] = new WallCell(wallImages[0]);
                                break;
                            case 'S':
                                board[rowIndex][colIndex] = new WallCell(entryPointImage);
                                break;
                            case 'H':
                                board[rowIndex][colIndex] = new HoleCell(holeImages[line.charAt(colIndex+1) - '0']);
                                board[rowIndex][colIndex+1] = null;
                                board[rowIndex+1][colIndex] = null;
                                board[rowIndex+1][colIndex+1] = null;
                                colIndex++;
                                break;
                            case 'B':
                                board[rowIndex][colIndex] = new TileCell(tileImage);
                                colIndex++;
                                break;
                            default:
                                if (cellChar >= '0' && cellChar <= '4') {
                                    board[rowIndex][colIndex] = new WallCell(wallImages[cellChar - '0']);
                                }
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception
        }
    }

    private void initializeBoardWithTiles() {
        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            for (int colIndex = 0; colIndex < numCols; colIndex++) {
                board[rowIndex][colIndex] = new TileCell(tileImage);
            }
        }
    }

    // Getters and setters
    public Cell getCell(int r, int c) {
        return board[r][c];
    }
}
