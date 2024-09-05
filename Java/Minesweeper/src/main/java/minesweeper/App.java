package minesweeper;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.Random;

public class App extends PApplet {

    private GameBoard gameBoard;
    private GameController gameController;
    
    public static final int CELLSIZE = 32;
    public static final int TOPBAR = 64;
    public static final int WIDTH = 864;
    public static final int HEIGHT = 640;
    public static final int BOARD_WIDTH = WIDTH / CELLSIZE;
    public static final int BOARD_HEIGHT = 20;
    public static final int FPS = 30;
    public static int NUM_MINES = 100;
    
    public static final int NUM_MINE_IMAGES = 9;
    private PImage flagImage;
    private PImage[] tileImages;
    private PImage[] mineImages;

    private long startTime;
    private long gameTime;

    private int explodeFrames;

    public static int[][] mineCountColour = new int[][] {
        {0, 0, 0},     // 0 - not shown
        {0, 0, 255},   // 1 - blue
        {0, 133, 0},   // 2 - green
        {255, 0, 0},   // 3 - red
        {0, 0, 132},   // 4 - dark blue
        {132, 0, 0},   // 5 - dark red
        {0, 132, 132}, // 6 - cyan
        {132, 0, 132}, // 7 - purple
        {32, 32, 32}   // 8 - gray
    };
    
    public static final Random random = new Random();

    public App() {}

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup() {
        frameRate(FPS);
        loadImages();
        startGame();
    }

    private void loadImages() {
        flagImage = loadImage("src/main/resources/minesweeper/flag.png");

        // Load the tile images
        tileImages = new PImage[3];
        tileImages[0] = loadImage("src/main/resources/minesweeper/tile.png");
        for (int i = 1; i < tileImages.length; i++) {
            tileImages[i] = loadImage("src/main/resources/minesweeper/tile" + i + ".png");
        }

        // Load mine images
        mineImages = new PImage[10];
        for (int i = 0; i < mineImages.length; i++) {
            mineImages[i] = loadImage("src/main/resources/minesweeper/mine" + i + ".png");
        }
    }

    private void startGame() {
        gameBoard = new GameBoard(BOARD_HEIGHT - 2, BOARD_WIDTH, NUM_MINES, random);
        gameController = new GameController(gameBoard);
        startTime = millis();
        explodeFrames = -1;
    }

    @Override
    public void draw() {
        background(200);
        drawTopBar();
        drawBoard();
    }

    private void drawTopBar() {
        fill(150);
        rect(0, 0, width, TOPBAR);

        if (!gameBoard.isGameOver() && !gameBoard.isWin()) {
            gameTime = (millis() - startTime) / 1000;
        }

        textSize(32);
        fill(0);
        textAlign(LEFT, CENTER);
        text("Flag: " + gameBoard.getFlagsCount(), 20, TOPBAR / 2);

        textAlign(RIGHT, CENTER);
        text("Time: " + gameTime, width - 20, TOPBAR / 2);
    }

    private void drawBoard() {
        for (int row = 0; row < BOARD_HEIGHT - 2; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                int x = col * CELLSIZE;
                int y = row * CELLSIZE + TOPBAR;
                Cell cell = gameBoard.getCell(row, col);
                PImage tileImage = determineTileImage(cell, row, col);
                
                image(tileImage, x, y, CELLSIZE, CELLSIZE);
                if (!cell.isRevealed() && cell.isFlagged()) {
                    image(flagImage, x, y, CELLSIZE, CELLSIZE);
                }
                if (cell.isRevealed() && cell.getNeighboringMines() > 0) {
                    drawMineCount(cell.getNeighboringMines(), x, y);
                }
            }
        }

        if (gameBoard.isGameOver()) {
            updateExplodingMines();
            drawExplodingMines();
        }

        if (gameBoard.getRevealCount() == (gameBoard.NUM_TILES - NUM_MINES) && !gameBoard.isGameOver()) {
            gameBoard.setWin(true);
            drawWin();
        }
    }

    private PImage determineTileImage(Cell cell, int row, int col) {
        if (cell.isRevealed()) {
            return tileImages[0];
        } else if (isMouseOverCell(row, col)) {
            return tileImages[2];
        } else {
            return tileImages[1];
        }
    }

    private void updateExplodingMines() {
        explodeFrames++;
        if (explodeFrames % 3 == 0) {
            gameBoard.updateExplodingMines();
        }
    }

    private void drawExplodingMines() {
        for (Cell cell : gameBoard.getMinesExploding()) {
            drawExplosionEffect(cell);
        }
    }

    private void drawExplosionEffect(Cell cell) {
        int x = cell.getXPos();
        int y = cell.getYPos();
        image(mineImages[cell.getMineImageIndex()], x, y, CELLSIZE, CELLSIZE);

        textSize(64);
        fill(255, 0, 0);
        textAlign(CENTER, CENTER);
        text("You Lost!", width / 2, height / 2);
    }

    private void drawMineCount(int mineCount, int x, int y) {
        textSize(20);
        fill(color(mineCountColour[mineCount][0], mineCountColour[mineCount][1], mineCountColour[mineCount][2]));
        textAlign(CENTER, CENTER);
        text(mineCount, x + CELLSIZE / 2, y + CELLSIZE / 2);
    }

    private void drawWin() {
        textSize(64);
        fill(0, 255, 0);
        textAlign(CENTER, CENTER);
        text("You Win!", width / 2, height / 2);
    }

    private boolean isMouseOverCell(int row, int col) {
        float adjustedMouseY = mouseY - TOPBAR;
        return mouseX >= col * CELLSIZE && mouseX < (col + 1) * CELLSIZE &&
                adjustedMouseY >= row * CELLSIZE && adjustedMouseY < (row + 1) * CELLSIZE;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKey() == 'r' || event.getKey() == 'R') {
            startGame(); // Restart game
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int col = mouseX / CELLSIZE;
        int row = (mouseY - TOPBAR) / CELLSIZE;

        if (isValidCell(row, col) & !gameBoard.isGameOver() & !gameBoard.isWin()) {
            if (mouseButton == LEFT) {
                gameController.revealCell(row, col);
            } else if (mouseButton == RIGHT) {
                gameController.toggleFlag(row, col);
            }
            redraw();
        }
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < BOARD_HEIGHT - 2 && col >= 0 && col < BOARD_WIDTH;
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                NUM_MINES = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.printf("Error: Invalid parameter for number '%s' of mines. Default: %s mines.%n", args[0], NUM_MINES);
            }
        }
        
        PApplet.main("minesweeper.App");
    }
}
