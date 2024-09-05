package minesweeper;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    private GameBoard gameBoard;
    private GameController gameController;

    public static final int CELLSIZE = 32; //8;
    // public static final int CELLHEIGHT = 32;

    // public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE; //27
    public static final int BOARD_HEIGHT = 20; //18+2

    public static final int FPS = 30;

    public static int NUM_TILES = 18 * 27;
    public static int NUM_MINES = 100;

    public String configPath;

    private static Random random = new Random();

    private long startTime;
    private long gameTime;

    private int explodeFrames;

    private PImage flagImage;
    private PImage[] tileImages;
    private PImage[] mineImages;
	
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

	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);
		//See PApplet javadoc:
		//loadJSONObject(configPath)
		//loadImage(this.getClass().getResource(filename).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));

        //create attributes for data storage, eg board

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

        // Load the mine images
        mineImages = new PImage[10];
        for (int i = 0; i < mineImages.length; i++) {
            mineImages[i] = loadImage("src/main/resources/minesweeper/mine" + i + ".png");
        }
    }

    private void startGame() {
        gameBoard = new GameBoard(BOARD_HEIGHT - 2, BOARD_WIDTH, NUM_MINES, random); // Reinitialize game board
        gameController = new GameController(gameBoard); // Reinitialize game controller
        startTime = millis(); // Reset start time
        explodeFrames = -1;
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event) {
        if (event.getKey() == 'r' || event.getKey() == 'R') {
            startGame(); // Restart game
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (mouseButton == LEFT) { // Left mouse button
            // Convert mouse coordinates to board coordinates
            int col = mouseX / CELLSIZE;
            int row = (mouseY - TOPBAR) / CELLSIZE; // Adjust for top bar
    
            // Ensure the click is within the board boundaries
            if (isValidCell(row, col)) {
                // Reveal the cell
                if (!gameBoard.isWin() && !gameBoard.isGameOver()) {
                    gameController.revealCell(row, col);
                }
                // Redraw the board to reflect the changes
                redraw();
            }
        }
        if (mouseButton == RIGHT) {
            int col = mouseX / CELLSIZE;
            int row = (mouseY - TOPBAR) / CELLSIZE;

            if (isValidCell(row, col)) {
                Cell cell = gameBoard.getCell(row, col);
                cell.toggleFlag();
            }
        }
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < BOARD_HEIGHT && col >= 0 && col < BOARD_WIDTH;
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Redraw the board to show the new hover effect
        redraw();
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        //draw game board
        background(200);
        drawTopBar();
        drawBoard();
    }

    private void drawTopBar() {
        fill(150);
        rect(0, 0, width, TOPBAR);

        // Update time if game is not over
        if (!gameBoard.isGameOver() && !gameBoard.isWin()) {
            gameTime = (millis() - startTime) / 1000;
        }
        
        // Draw timer
        textSize(32);
        fill(0); // White color for text
        textAlign(RIGHT, CENTER);
        textAlign(RIGHT, CENTER);
        text("Time: " + gameTime, width - 20, TOPBAR / 2); // Adjust position as needed

        // Draw reveal count
        textSize(32);
        fill(0); // White color for text
        textAlign(LEFT, CENTER);
        text("Reveal Count: " + gameBoard.revealCount, 20, TOPBAR / 2); // Adjust position as needed
    }

    private void drawBoard() {
        // Iterate through each cell in the game board
        for (int row = 0; row < BOARD_HEIGHT - 2; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                // Calculate the position where the tile should be drawn
                int x = col * CELLSIZE;
                int y = row * CELLSIZE + TOPBAR;
    
                // Get the cell's state
                Cell cell = gameBoard.getCell(row, col);
    
                // Determine the image to be used for this cell
                PImage tileImageToUse = determineTileImage(cell, row, col);

                image(tileImageToUse, x, y, CELLSIZE, CELLSIZE);
                if (!cell.isRevealed() && cell.isFlagged()) {
                    image(flagImage, x, y, CELLSIZE, CELLSIZE);
                }
                if (cell.isRevealed() && cell.getNeighboringMines() > 0) {
                    drawMineCount(cell.getNeighboringMines(), x, y);
                }
                if (gameBoard.revealCount == (NUM_TILES - NUM_MINES) && !gameBoard.isGameOver()) {
                    gameBoard.setWin(true);
                    drawWin();
                }
            }
        }

        if (gameBoard.isGameOver()) {
            updateMine();
            drawExplodingMines();
        }

    }

    private void updateMine() {
        explodeFrames++;
        if (explodeFrames % 3 == 0) {
            for (Cell cell : gameBoard.minesExploding) {
                if (cell.getMineImageIndex() < mineImages.length - 1) {
                    cell.inccMineImageIndex();
                }
            }
            if (gameBoard.minesToExplode.size() > 0) {
                gameBoard.minesExploding.add(gameBoard.minesToExplode.remove(0));
                System.out.println("mine added to minesExploding");
            }
        }
    }

    private void drawExplodingMines() {
        for (Cell cell : gameBoard.minesExploding) {
            drawExplosionEffect(cell, cell.xPos, cell.yPos);
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

    private void drawMineCount(int mineCount, int x, int y) {
        textSize(20);
        fill(color(mineCountColour[mineCount][0], mineCountColour[mineCount][1], mineCountColour[mineCount][2]));
        textAlign(CENTER, CENTER);
        text(mineCount, x + CELLSIZE / 2, y + CELLSIZE / 2);
    }

    private void drawWin() {
        textSize(64);
        fill(0, 255, 0); // Green color for text
        textAlign(CENTER, CENTER);
        text("You Win!", width / 2, height / 2);
    }

    private void drawExplosionEffect(Cell cell, int x, int y) {
        System.out.println("mine index" + cell.getMineImageIndex());
        image(mineImages[cell.getMineImageIndex()], x, y, CELLSIZE, CELLSIZE);

        textSize(64);
        fill(255, 0, 0); // Red color for text
        textAlign(CENTER, CENTER);
        text("You Lost!", width / 2, height / 2);
    }

    private boolean isMouseOverCell(int row, int col) {
        // Adjust mouseY for the top bar since mouseY is relative to the entire window
        float adjustedMouseY = mouseY - TOPBAR;

        // Check if mouse is within the bounds of the cell
        return mouseX >= col * CELLSIZE && mouseX < (col + 1) * CELLSIZE &&
            adjustedMouseY >= row * CELLSIZE && adjustedMouseY < (row + 1) * CELLSIZE;
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
