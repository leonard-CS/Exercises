package inkball;

import processing.core.PApplet;
import processing.core.PImage;
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

    private ArrayList<Ball> waitingBalls;

    // Images
    private final int NUM_IMAGES = 5;
    private final PImage[] ballsImages = new PImage[NUM_IMAGES];

    private int currentLevelIndex = 0;
    private String layout;
    private int levelTime;

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
        loadBallImages();
        // Load the JSON configuration file
        JSONObject config = loadJSONObject(configPath);
        // For debugging, print out the JSON object
        // println(config);

        loadLevelConfig(config);
        startLevel(config);

        lines = new ArrayList<>();
    }

    private void loadBallImages() {
        for (int i = 0; i < NUM_IMAGES; i++) {
            ballsImages[i] = loadImage("src/main/resources/inkball/ball" + i + ".png");
        }
    }

    private void loadLevelConfig(JSONObject config) {
        JSONArray levels = config.getJSONArray("levels");
        JSONObject currentLevel = levels.getJSONObject(currentLevelIndex);
        
        layout = currentLevel.getString("layout");
        levelTime = currentLevel.getInt("time");
        int levelSpawnInterval = currentLevel.getInt("spawn_interval");
        float levelScoreIncreaseModifier = currentLevel.getFloat("score_increase_from_hole_capture_modifier");
        float levelScoreDecreaseModifier = currentLevel.getFloat("score_decrease_from_wrong_hole_modifier");
        JSONArray balls = currentLevel.getJSONArray("balls");
        setWaitingBalls(balls);
        
        printLevelInfo(layout, levelTime, levelSpawnInterval, levelScoreIncreaseModifier, levelScoreDecreaseModifier, balls);
    }

    private void setWaitingBalls(JSONArray balls) {
        waitingBalls = new ArrayList<>();
        String color;
        Ball ball;
        int x, y;
        for (int i = 0; i < balls.size(); i++) {
            color = balls.getString(i);
            x = CELLSIZE + CELLSIZE * i;
            y = CELLSIZE;
            switch (color) {
                case "grey":
                    ball = new Ball(ballsImages[0], x, y);
                    break;
                case "orange":
                    ball = new Ball(ballsImages[1], x, y);
                    break;
                case "blue":
                    ball = new Ball(ballsImages[2], x, y);
                    break;
                case "green":
                    ball = new Ball(ballsImages[3], x, y);
                    break;
                default: // yellow
                    ball = new Ball(ballsImages[4], x, y);
            }
            waitingBalls.add(ball);
        }
    }

    private void startLevel(JSONObject config) {
        gameBoard = new GameBoard(BOARD_HEIGHT - 2, BOARD_WIDTH, this, currentLevelIndex, config);
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
        drawBoard();

        //----------------------------------
        //display lines
        //----------------------------------
        for (Line line : lines) {
            line.draw(this);
        }

        //----------------------------------
        //draw balls
        //----------------------------------
        for (int i = 0; i < waitingBalls.size() && i < 5; i++) {
            waitingBalls.get(i).draw(this);
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

        long elapsedTime = millis() - gameBoard.startTime;

        // Draw Main Timer
        int remainingTime = gameBoard.getLevelTime() - (int) (elapsedTime/1000);
        String timeMessage = String.format("Time: %4d", remainingTime);
        text(timeMessage, WIDTH - CELLSIZE/2, CELLSIZE);

        // Draw Spawn Timer
        int spawnInterval = gameBoard.getSpawnInterval() * 10;
        int spawnTime = spawnInterval - (int) ((elapsedTime / 100) % spawnInterval);
        textAlign(LEFT, CENTER);
        String spawnMessage = String.format("%2d.%1d", spawnTime / 10, spawnTime % 10);
        text(spawnMessage, CELLSIZE*6, CELLSIZE);
    }

    private void drawBoard() {
        for (int row = 0; row < gameBoard.numRows; row++) {
            for (int col = 0; col < gameBoard.numCols; col++) {
                Cell cell = gameBoard.getCell(row, col);
                if (cell != null) {
                    cell.draw(this, row, col);
                }
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
