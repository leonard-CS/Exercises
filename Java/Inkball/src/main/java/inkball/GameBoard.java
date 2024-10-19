package inkball;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class GameBoard {
    private final PApplet p;
    public final int numRows;
    public final int numCols;
    private final Cell[][] board;
    private final ArrayList<EntryPointCell> spawners = new ArrayList<>();
    private final ArrayList<HoleCell> holes = new ArrayList<>();

    private final int currentLevelIndex;
    private String layout;

    private int waitingBallUpdateCount = 0;
    private final ArrayList<Ball> waitingBalls = new ArrayList<>();
    private final ArrayList<Ball> runningBalls = new ArrayList<>();
    private final ArrayList<Line> lines = new ArrayList<>();

    private boolean gameStart = false;

    // Timing
    private long gameStartTime;
    private long elapsed_time = 0;
    private int levelTime;
    private int spawnInterval;

    // Yellow Tiles
    private long lastMoveTime;
    private long winTime;
    private WallCell topLeftTile;
    private WallCell bottomRightTile;

    // Game Control
    private boolean win = false;
    private boolean isPaused = false;
    private boolean timesup = false;

    // Scores
    private int score = 0;
    private final int[] scoreIncreases = new int[5];
    private final int[] scoreDecreases = new int[5];

    // Images
    private final int NUM_IMAGES = 5;
    private PImage entryPointImage;
    private PImage tileImage;
    private final PImage[] holeImages = new PImage[NUM_IMAGES];
    private final PImage[] wallImages = new PImage[NUM_IMAGES];
    private final PImage[] ballsImages = new PImage[NUM_IMAGES];

    public GameBoard(PApplet p, int numRows, int numCols, int currentLevelIndex, JSONObject config) {
        this.p = p;
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];
        this.currentLevelIndex = currentLevelIndex;

        loadImages();
        loadLevelConfig(config);
        initializeBoard(layout);
    }

    private void loadImages() {
        entryPointImage = p.loadImage("src/main/resources/inkball/entrypoint.png");
        tileImage = p.loadImage("src/main/resources/inkball/tile.png");

        for (int i = 0; i < NUM_IMAGES; i++) {
            holeImages[i] = p.loadImage("src/main/resources/inkball/hole" + i + ".png");
            wallImages[i] = p.loadImage("src/main/resources/inkball/wall" + i + ".png");
            ballsImages[i] = p.loadImage("src/main/resources/inkball/ball" + i + ".png");
        }
    }

    private void loadLevelConfig(JSONObject config) {
        JSONArray levels = config.getJSONArray("levels");
        JSONObject currentLevel = levels.getJSONObject(currentLevelIndex);
        
        layout = currentLevel.getString("layout");
        levelTime = currentLevel.getInt("time");
        spawnInterval = currentLevel.getInt("spawn_interval");

        loadBalls(currentLevel.getJSONArray("balls"));

        // Load score modifiers
        float levelScoreIncreaseModifier = currentLevel.getFloat("score_increase_from_hole_capture_modifier");
        float levelScoreDecreaseModifier = currentLevel.getFloat("score_decrease_from_wrong_hole_modifier");
        String[] ballColors = {"grey", "orange", "blue", "green", "yellow"};
        JSONObject scoreIncreasesConfig = config.getJSONObject("score_increase_from_hole_capture");
        JSONObject scoreDecreasesConfig = config.getJSONObject("score_decrease_from_wrong_hole");

        for (int i = 0; i < scoreIncreases.length; i++) {
            scoreIncreases[i] = (int) (scoreIncreasesConfig.getInt(ballColors[i]) * levelScoreIncreaseModifier);
            scoreDecreases[i] = (int) (scoreDecreasesConfig.getInt(ballColors[i]) * levelScoreDecreaseModifier);
        }
    }

    private void initializeBoard(String layout) {
        fillBoardWithTiles();
        try (BufferedReader br = new BufferedReader(new FileReader(layout))) {
            for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
                String line = br.readLine();
                if (line != null) {
                    parseLine(line, rowIndex);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillBoardWithTiles() {
        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            for (int colIndex = 0; colIndex < numCols; colIndex++) {
                int x = colIndex * App.CELLSIZE;
                int y = rowIndex * App.CELLSIZE + App.TOPBAR;
                board[rowIndex][colIndex] = new TileCell(tileImage, x, y);
            }
        }
    }

    private void parseLine(String line, int rowIndex) {
        for (int colIndex = 0; colIndex < numCols; colIndex++) {
            char cellChar = line.charAt(colIndex);
            int x = colIndex * App.CELLSIZE;
            int y = rowIndex * App.CELLSIZE + App.TOPBAR;
            switch (cellChar) {
                case 'X':
                    board[rowIndex][colIndex] = new WallCell(x, y, wallImages[0], Color.fromValue(0));
                    break;
                case 'S':
                    EntryPointCell spawner = new EntryPointCell(entryPointImage, x, y);
                    board[rowIndex][colIndex] = spawner;
                    spawners.add(spawner);
                    break;
                case 'H':
                    createHoleCell(line, rowIndex, colIndex);
                    colIndex++; // Skip the next index as it's part of the hole
                    break;
                case 'B':
                    colIndex++; // Skip the next index as it's part of a tile
                    break;
                default:
                    if (Character.isDigit(cellChar)) {
                        int wallIndex = cellChar - '0';
                        if (wallIndex < NUM_IMAGES) {
                            board[rowIndex][colIndex] = new WallCell(x, y, wallImages[wallIndex], Color.fromValue(wallIndex));
                        }
                    }
                    break;
            }
        }
    }

    private void createHoleCell(String line, int rowIndex, int colIndex) {
        int holeType = line.charAt(colIndex + 1) - '0';
        int x = colIndex * App.CELLSIZE;
        int y = rowIndex * App.CELLSIZE + App.TOPBAR;
        HoleCell holeCell = new HoleCell(x, y, holeImages[holeType], Color.fromValue(holeType));
        holes.add(holeCell);
        board[rowIndex][colIndex] = holeCell;
        board[rowIndex][colIndex + 1] = null;
        board[rowIndex + 1][colIndex] = null;
        board[rowIndex + 1][colIndex + 1] = null;
    }

    public void draw() {
        // Draw cells
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Cell cell = getCell(row, col);
                if (cell != null) {
                    cell.draw(p);
                }
            }
        }
        // Draw lines
        for (Line line : lines) {
            line.draw(p);
        }
        // Draw waiting balls
        for (int i = 0; i < waitingBalls.size() && i < 5; i++) {
            waitingBalls.get(i).draw(p);
        }
        // Draw running balls
        for (Ball ball : runningBalls) {
            ball.draw(p);
        }

        if (!isPaused && !timesup) {
            updateGame();
        }

        if (win) {
            topLeftTile.draw(p);
            bottomRightTile.draw(p);
        }
    }

    private void updateGame() {
        if (getSpawnTime() == 1 && !waitingBalls.isEmpty() && waitingBallUpdateCount == 0) {
            Ball ballToJoin = waitingBalls.remove(0);
            waitingBallUpdateCount = App.CELLSIZE;
            ballToJoin.start(getBallSpawnPosition());
            runningBalls.add(ballToJoin);
        }
        // Move waiting balls left 1px/frame
        if (waitingBallUpdateCount != 0) {
            for (Ball ball : waitingBalls) {
                ball.moveLeft(1);
            }
            waitingBallUpdateCount--;
        }
        // Update running balls
        if (!runningBalls.isEmpty()) {
            for (Ball ball : new ArrayList<>(runningBalls)) {
                ball.update(this);
            }
        }
        // Update Time
        if (gameStart) {
            long delta_time = p.millis() - gameStartTime;
            elapsed_time += delta_time;
            gameStartTime = p.millis();
        }

        if (getRemainingTime() <= 0) {
            timesup = true;
        }

        if (checkWinCondition() && !win) {
            win = true;
            winTime = p.millis();
            addScoreForRemainingTime();
            initializeYellowTiles();
        }

        if (win) {
            long currentTime = p.millis();
            long moveInterval = (long) (0.067 * 1000);
            if (currentTime - lastMoveTime >= moveInterval) {
                moveYellowTiles();
                lastMoveTime = currentTime;
            }
            if (p.millis() - winTime >= 4.556 * 1000) {
                ((App) p).nextLevel();
            }
        }
    }

    private boolean checkWinCondition() {
        return waitingBalls.isEmpty() && runningBalls.isEmpty();
    }

    private void addScoreForRemainingTime() {
        int remainingTime = getRemainingTime();
        int scoreToAdd = (int) (remainingTime / 0.067); // 1 unit every 0.067 seconds
        score += scoreToAdd;
    }

    private void initializeYellowTiles() {
        topLeftTile = new WallCell(0, App.TOPBAR, wallImages[Color.YELLOW.getValue()], Color.YELLOW);
        bottomRightTile = new WallCell((numCols - 1) * App.CELLSIZE, App.TOPBAR + (numRows - 1) * App.CELLSIZE,
                                        wallImages[Color.YELLOW.getValue()], Color.YELLOW);
    }

    private void moveYellowTiles() {
        topLeftTile.moveClockwise(numCols * App.CELLSIZE, numRows * App.CELLSIZE);
        bottomRightTile.moveClockwise(numCols * App.CELLSIZE, numRows * App.CELLSIZE);
    }

    private PVector getBallSpawnPosition() {
        int index = App.random.nextInt(spawners.size());
        return spawners.get(index).getCenterPosition();
    }

    // Lines
    public void addLines(Line line) {
        lines.add(line);
    }

    public void removeCollidingLine(int mouseX, int mouseY) {
        // Loop through all lines to check for collision
        for (Line line : lines) {
            if (line.isMouseNear(mouseX, mouseY)) {
                lines.remove(line);
                break;
            }
        }
    }

    // Balls
    private void loadBalls(JSONArray ballsConfig) {
        for (int i = 0; i < ballsConfig.size(); i ++) {
            String color = ballsConfig.getString(i);
            Ball ball = createBall(color, i);
            waitingBalls.add(ball);
        }
    }

    private Ball createBall(String color, int index) {
        int x = App.CELLSIZE + App.CELLSIZE * index;
        int y = App.CELLSIZE;
        switch (color) {
            case "grey":
                return new Ball(x, y, ballsImages[0], Color.fromValue(0));
            case "orange":
                return new Ball(x, y, ballsImages[1], Color.fromValue(1));
            case "blue":
                return new Ball(x, y, ballsImages[2], Color.fromValue(2));
            case "green":
                return new Ball(x, y, ballsImages[3], Color.fromValue(3));
            default: // yellow
                return new Ball(x, y, ballsImages[4], Color.fromValue(4));
        }
    }

    // Getters and setters
    public ArrayList<HoleCell> getHoles() {
        return new ArrayList<>(holes);
    }

    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    public ArrayList<Line> getLines() {
        return new ArrayList<>(lines);
    }

    public void removeLine(Line line) {
        lines.remove(line);
    }

    public void removeBall(Ball ball) {
        runningBalls.remove(ball);
    }

    public void resetBall(Ball ball) {
        runningBalls.remove(ball);
        int x = App.CELLSIZE + App.CELLSIZE * waitingBalls.size();
        int y = App.CELLSIZE;
        ball.setPosition(x, y);
        waitingBalls.add(ball);
    }

    public int getScore() {
        return score;
    }

    public void increaseScore(Color color) {
        score += scoreIncreases[color.getValue()];
    }

    public void decreaseScore(Color color) {
        score -= scoreDecreases[color.getValue()];
    }

    public int getRemainingTime() {
        return levelTime - (int)(elapsed_time / 1000);
    }

    public int getSpawnTime() {
        int adjustedSpawnInterval = spawnInterval * 10;
        return adjustedSpawnInterval - (int) ((elapsed_time / 100) % adjustedSpawnInterval);
    }

    public void togglePauseState() {
        isPaused = !isPaused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isTimesUp() {
        return timesup;
    }

    public PImage getBallImage(int i) {
        return ballsImages[i];
    }

    public boolean hasGameStarted() {
        return gameStart;
    }

    public void start() {
        gameStart = true;
        gameStartTime = p.millis();
        Ball ballToJoin = waitingBalls.remove(0);
        waitingBallUpdateCount = App.CELLSIZE;
        ballToJoin.start(getBallSpawnPosition());
        runningBalls.add(ballToJoin);
    }
}
