package minesweeper;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class GameController {
    private GameBoard gameBoard;

    public GameController(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void handleMousePress(MouseEvent e) {
        // Handle mouse press events
    }

    public void handleKeyPress(KeyEvent e) {
        // Handle key press events
    }

    public void revealCell(int row, int col) {
        Cell cell = gameBoard.getCell(row, col);

        if (cell.isRevealed() || cell.isFlagged()) {
            return; // No action if the cell is already revealed or flagged
        }

        if (cell.reveal()) {
            gameBoard.revealCount++;
        }

        if (cell.isMine()) {
            gameBoard.setGameOver(true);
        } else if (cell.getNeighboringMines() == 0) {
            revealAdjacentCells(row, col);
        }
    }

    private void revealAdjacentCells(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
    
                if (isValidPosition(newRow, newCol)) {
                    Cell adjacentCell = gameBoard.getCell(newRow, newCol);
    
                    if (!adjacentCell.isRevealed() && !adjacentCell.isMine()) {
                        revealCell(newRow, newCol); // Recursively reveal adjacent cells
                    }
                }
            }
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < gameBoard.getRows() && col >= 0 && col < gameBoard.getCols();
    }
}
