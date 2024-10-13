package inkball;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE; //18
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    private GameBoard gameBoard;
    private ArrayList<Line> lines;
    private Line currentLine;

    private int currentLevelIndex = 0;

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
        //See PApplet javadoc:
		//loadJSONObject(configPath)
		// the image is loaded from relative path: "src/main/resources/inkball/..."
		/*try {
            result = loadImage(URLDecoder.decode(this.getClass().getResource(filename+".png").getPath(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }*/

        frameRate(FPS);
        // Load the JSON configuration file
        JSONObject config = loadJSONObject(configPath);
        // For debugging, print out the JSON object
        // println(config);

        startLevel(config);

        lines = new ArrayList<>();
    }

    private void startLevel(JSONObject config) {
        gameBoard = new GameBoard(this, BOARD_HEIGHT - 2, BOARD_WIDTH, currentLevelIndex, config);
    }

    private void printLevelInfo(String layout, int levelTime, int levelSpawnInterval, float levelScoreIncreaseModifier, float levelScoreDecreaseModifier, JSONArray balls) {
        println("Level " + (currentLevelIndex + 1) + ":");
        println("  Layout: " + layout);
        println("  Time: " + levelTime);
        println("  Spawn Interval: " + levelSpawnInterval);
        println("  Score Increase Modifier: " + levelScoreIncreaseModifier);
        println("  Score Decrease Modifier: " + levelScoreDecreaseModifier);
        println("  Balls: " + balls.join(", "));
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
        // create a new player-drawn line object
        currentLine = new Line(mouseX, mouseY);
        lines.add(currentLine);
    }
	
	@Override
    public void mouseDragged(MouseEvent e) {
        // add line segments to player-drawn line object if left mouse button is held
        if (e.getButton() == LEFT) {
            if (currentLine != null) {
                currentLine.addPoint(mouseX, mouseY);
            }
        }

		// remove player-drawn line object if right mouse button is held 
		// and mouse position collides with the line
        if (e.getButton() == RIGHT || (e.getButton() == LEFT && e.isControlDown())) {
            removeCollidingLine(mouseX, mouseY);
        }
    }

    private void removeCollidingLine(int mouseX, int mouseY) {
        // Loop through all lines to check for collision
        for (Line line : lines) {
            if (line.isMouseNear(mouseX, mouseY)) {
                lines.remove(line);
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
		currentLine = null;
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        background(200);

        //----------------------------------
        //display Board for current level:
        //----------------------------------
        drawTopBar();
        gameBoard.draw();
        gameBoard.update();

        //----------------------------------
        //display lines
        //----------------------------------
        for (Line line : lines) {
            line.draw(this);
        }

        // ----------------------------------
        //display score
        //----------------------------------
        //TODO
        
		//----------------------------------
        //----------------------------------
		//display game end message

    }

    private void drawTopBar() {
        fill(0);
        rect(CELLSIZE/2, CELLSIZE/2, CELLSIZE*5, CELLSIZE);

        // Draw Score
        textSize(24);
        textAlign(RIGHT, TOP);
        String scoreMessage = String.format("Score: %4d", gameBoard.getScore());
        text(scoreMessage, WIDTH - CELLSIZE/2, 0);

        // Draw Main Timer
        String timeMessage = String.format("Time: %4d", gameBoard.getRemainingTime());
        text(timeMessage, WIDTH - CELLSIZE/2, CELLSIZE);

        // Draw Spawn Timer
        int spawnTime = gameBoard.getSpawnTime();
        textAlign(LEFT, CENTER);
        String spawnMessage = String.format("%2d.%1d", spawnTime / 10, spawnTime % 10);
        text(spawnMessage, CELLSIZE*6, CELLSIZE);
    }

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
