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
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE; //27
    public static final int BOARD_HEIGHT = 20; //18+2

    public static final int FPS = 30;

    public static int NUM_MINES = 100;

    public String configPath;

    public static Random random = new Random();
	
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

    private long startTime;
	
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
        gameBoard = new GameBoard(BOARD_HEIGHT-2, BOARD_WIDTH, NUM_MINES); //BOARD_HEIGHT-2 to skip top bar
        gameController = new GameController(gameBoard);
        startTime = millis(); // Initialize start time
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {


    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        //draw game board
        background(200);
        drawTopBar();
    }

    private void drawTopBar() {
        fill(150);
        rect(0, 0, width, TOPBAR);

        // Draw timer
        long elapsedTime = millis() - startTime;
        int seconds = (int) (elapsedTime / 1000);

        String timeStr = String.format("Time: %d", seconds);
        textSize(32);
        fill(0); // White color for text
        textAlign(RIGHT, CENTER);
        text(timeStr, width - 20, TOPBAR / 2); // Adjust position as needed
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
