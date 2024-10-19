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
    private final ArrayList<EntryPointCell> spawners;

    private final int currentLevelIndex;
    private String layout;

    private int waitingBallUpdateCount = 0;
    private final ArrayList<Ball> waitingBalls = new ArrayList<>();
    private final ArrayList<Ball> runningBalls = new ArrayList<>();
    private final ArrayList<Line> lines = new ArrayList<>();

    // Times
    private long gameStartTime;
    private long elapsed_time = 0;

    private int levelTime;
    private int spawnInterval;

    private boolean isPaused = false;

    // Scores
    private int score = 0;

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
        this.spawners = new ArrayList<>();

        this.currentLevelIndex = currentLevelIndex;
        this.gameStartTime = p.millis();

        loadBallImages();
        loadLevelConfig(config);

        loadTileImages();
        createBoard(layout);
    }

    private void loadLevelConfig(JSONObject config) {
        JSONArray levels = config.getJSONArray("levels");
        JSONObject currentLevel = levels.getJSONObject(currentLevelIndex);
        
        layout = currentLevel.getString("layout");
        levelTime = currentLevel.getInt("time");
        spawnInterval = currentLevel.getInt("spawn_interval");
        float levelScoreIncreaseModifier = currentLevel.getFloat("score_increase_from_hole_capture_modifier");
        float levelScoreDecreaseModifier = currentLevel.getFloat("score_decrease_from_wrong_hole_modifier");

        JSONArray ballsConfig = currentLevel.getJSONArray("balls");
        loadBalls(ballsConfig);
    }

    private void loadTileImages() {
        entryPointImage = p.loadImage("src/main/resources/inkball/entrypoint.png");
        tileImage = p.loadImage("src/main/resources/inkball/tile.png");

        for (int i = 0; i < NUM_IMAGES; i++) {
            holeImages[i] = p.loadImage("src/main/resources/inkball/hole" + i + ".png");
            wallImages[i] = p.loadImage("src/main/resources/inkball/wall" + i + ".png");
        }
    }

    private void createBoard(String layout) {
        initializeBoardWithTiles();
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
//                    board[rowIndex][colIndex] = new TileCell(tileImage, x, y);
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
        board[rowIndex][colIndex] = new HoleCell(x, y, holeImages[holeType], Color.fromValue(holeType));
        board[rowIndex][colIndex + 1] = null;
        board[rowIndex + 1][colIndex] = null;
        board[rowIndex + 1][colIndex + 1] = null;
    }

    private void initializeBoardWithTiles() {
        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            for (int colIndex = 0; colIndex < numCols; colIndex++) {
                int x = colIndex * App.CELLSIZE;
                int y = rowIndex * App.CELLSIZE + App.TOPBAR;
                board[rowIndex][colIndex] = new TileCell(tileImage, x, y);
            }
        }
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

        if (!isPaused) {
            update();
        }
    }

    public void update() {
        if (getSpawnTime() == 1 && !waitingBalls.isEmpty() && waitingBallUpdateCount == 0) {
            Ball ballToJoin = waitingBalls.remove(0);
            waitingBallUpdateCount = App.CELLSIZE;
            ballToJoin.start(getBallSpawnPosition());
            runningBalls.add(ballToJoin);
        }
        if (waitingBallUpdateCount != 0) {
            // Move waiting balls left 1px/frame
            for (Ball ball : waitingBalls) {
                ball.moveLeft(1);
            }
            waitingBallUpdateCount--;
        }
        if (!runningBalls.isEmpty()) {
            for (Ball ball : runningBalls) {
                ball.update(this);
            }
        }
        // Update Time
        long delta_time = p.millis() - gameStartTime;
        elapsed_time += delta_time;
        gameStartTime = p.millis();
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
    private void loadBallImages() {
        for (int i = 0; i < NUM_IMAGES; i++) {
            ballsImages[i] = p.loadImage("src/main/resources/inkball/ball" + i + ".png");
        }
    }

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
    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    public ArrayList<Line> getLines() {
        return new ArrayList<>(lines);
    }

    public void removeLine(Line line) {
        lines.remove(line);
    }

    public int getScore() {
        return score;
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

    public PImage getBallImage(int i) {
        return ballsImages[i];
    }
}
