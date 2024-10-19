package inkball;

import processing.core.PApplet;
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

    public static final float COLLISION_THRESHOLD = 10.0f;

    public String configPath;

    public static Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.
    private GameBoard gameBoard;
    private Line currentLine;

    private int currentLevelIndex = 0;
    private JSONObject config;

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
        // For debugging, print out the JSON object
        // println(config);
        startLevel();
    }

    private void startLevel() {
        gameBoard = new GameBoard(this, BOARD_HEIGHT - 2, BOARD_WIDTH, currentLevelIndex, config);
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if (event.getKeyCode() == ' ') {
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
        if (!gameBoard.hasGameStart()) {
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

        //----------------------------------
        //display Board for current level:
        //----------------------------------
        drawTopBar();
        gameBoard.draw();

        //----------------------------------
        //display lines
        //----------------------------------

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
        rect((float) CELLSIZE / 2, (float) CELLSIZE / 2, CELLSIZE * 5, CELLSIZE);

        textSize(28);
        if (gameBoard.isPaused()) {
            textAlign(CENTER, CENTER);
            text("*** PAUSED ***", (float) WIDTH / 2, (float) TOPBAR / 2);
        }

        // Draw Score
        textSize(24);
        textAlign(RIGHT, TOP);
        String scoreMessage = String.format("Score: %4d", gameBoard.getScore());
        text(scoreMessage, WIDTH - (float) CELLSIZE / 2, 0);

        // Draw Main Timer
        String timeMessage = String.format("Time: %4d", gameBoard.getRemainingTime());
        text(timeMessage, WIDTH - (float) CELLSIZE / 2, CELLSIZE);

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
