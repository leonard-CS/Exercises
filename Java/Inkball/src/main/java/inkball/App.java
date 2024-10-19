package inkball;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.Random;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE; //18
    public static final int BOARD_HEIGHT = 20;

    public static final int FPS = 30;

    public String configPath;
    private JSONObject config;

    public static Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.
    private GameBoard gameBoard;
    private int currentLevelIndex = 0;
    private Line currentLine;

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
        config = loadJSONObject(configPath);
        startLevel();
    }

    private void startLevel() {
        gameBoard = new GameBoard(this, BOARD_HEIGHT - 2, BOARD_WIDTH, currentLevelIndex, config);
    }

    public void nextLevel() {
        // Increment the current level index
        currentLevelIndex++;

        // Check if the new level index exceeds the available levels
        JSONArray levels = config.getJSONArray("levels");
        if (currentLevelIndex >= levels.size()) {
            displayWinMessage();
        } else {
            // Start next level
            startLevel();
        }
    }

    private void displayWinMessage() {
        fill(0);
        textSize(28);
        textAlign(CENTER, CENTER);
        text("*** WIN ***", WIDTH / 2.0f, TOPBAR / 2.0f);
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if (event.getKey() == ' ') {
            gameBoard.togglePauseState();
        }
        if (event.getKey() == 'r' || event.getKey() == 'R') {
            startLevel();
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
        if (!gameBoard.hasGameStarted()) {
            gameBoard.start();
        }
        // create a new player-drawn line object
        currentLine = new Line(mouseX, mouseY);
        gameBoard.addLines(currentLine);
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
            gameBoard.removeCollidingLine(mouseX, mouseY);
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

        drawTopBar();
        gameBoard.draw();
    }

    private void drawTopBar() {
        fill(0);
        rect(CELLSIZE / 2.0f, CELLSIZE / 2.0f, CELLSIZE * 5, CELLSIZE);

        textSize(28);
        if (gameBoard.isPaused()) {
            textAlign(CENTER, CENTER);
            text("*** PAUSED ***", WIDTH / 2.0f, TOPBAR / 2.0f);
        }

        // Draw Score
        textSize(24);
        textAlign(RIGHT, TOP);
        String scoreMessage = String.format("Score: %4d", gameBoard.getScore());
        text(scoreMessage, WIDTH - CELLSIZE / 2.0f, 0);

        // Draw Main Timer
        String timeMessage = String.format("Time: %4d", gameBoard.getRemainingTime());
        text(timeMessage, WIDTH - CELLSIZE / 2.0f, CELLSIZE);

        // Draw Spawn Timer
        int spawnTime = gameBoard.getSpawnTime();
        textAlign(LEFT, CENTER);
        String spawnMessage = String.format("%2d.%1d", spawnTime / 10, spawnTime % 10);
        text(spawnMessage, CELLSIZE * 6, CELLSIZE);
    }

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }
}
