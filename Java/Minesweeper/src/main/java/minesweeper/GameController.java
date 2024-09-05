package minesweeper;

public class GameController {
    private final GameBoard gameBoard;

    public GameController(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void revealCell(int row, int col) {
        Cell cell = gameBoard.getCell(row, col);

        if (cell.isRevealed() || cell.isFlagged()) {
            return; // Don't act on already revealed or flagged cells
        }

        cell.reveal();
        gameBoard.incrementRevealCount();;

        if (cell.isMine()) {
            handleMineExplosion(cell);
        } else if (cell.getNeighboringMines() == 0) {
            revealAdjacentCells(row, col); // Reveal adjacent cells if no neighboring mines
        }
    }

    private void handleMineExplosion(Cell mineCell) {
        mineCell.setExploded(true);
        gameBoard.setGameOver(true);
        arrangeMineExplosion(mineCell);
    }

    private void arrangeMineExplosion(Cell firstMine) {
        gameBoard.minesToExplode.remove(firstMine);
        gameBoard.minesToExplode.add(0, firstMine); // Move the first mine to the start of the list
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

    public void toggleFlag(int row, int col) {
        if (gameBoard.getCell(row, col).toggleFlag()) {
            gameBoard.decrementFlagsCount();
        } else {
            gameBoard.incrementFlagsCount();
        }
    }
}
