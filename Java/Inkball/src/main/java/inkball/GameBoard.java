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
    private PApplet p;
    public final int numRows;
    public final int numCols;
    private Cell[][] board;
    private ArrayList<EntryPointCell> spawners;

    private final int currentLevelIndex;
    private String layout;

    private int waitingBallUpdateCount = 0;
    private final ArrayList<Ball> waitingBalls;
    private final ArrayList<Ball> runningBalls;

    // Times
    public final long startTime;
    private int levelTime;
    private int spawnInterval;

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
        this.startTime = p.millis();

        waitingBalls = new ArrayList<>();
        runningBalls = new ArrayList<>();

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
                    board[rowIndex][colIndex] = new WallCell(wallImages[0], x, y);
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
                            board[rowIndex][colIndex] = new WallCell(wallImages[wallIndex], x, y);
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
        board[rowIndex][colIndex] = new HoleCell(holeImages[holeType], x, y);
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
        // Draw waiting balls
        for (int i = 0; i < waitingBalls.size() && i < 5; i++) {
            waitingBalls.get(i).draw(p);
        }
        // Draw running balls
        for (Ball ball : runningBalls) {
            ball.draw(p);
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
                ball.update();
            }
        }
    }

    private PVector getBallSpawnPosition() {
        int index = App.random.nextInt(spawners.size());
        PVector position = spawners.get(index).getPosition();
        int offset = App.CELLSIZE / 2;
        position.set(position.x + offset, position.y + offset);
        return position;
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
                return new Ball(ballsImages[0], x, y);
            case "orange":
                return new Ball(ballsImages[1], x, y);
            case "blue":
                return new Ball(ballsImages[2], x, y);
            case "green":
                return new Ball(ballsImages[3], x, y);
            default: // yellow
                return new Ball(ballsImages[4], x, y);
        }
    }

    // Getters and setters
    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    public int getScore() {
        return score;
    }

    public int getLevelTime() {
        return levelTime;
    }

    public int getSpawnInterval() {
        return spawnInterval;
    }

    public long getElapsedTime() {
        return p.millis() - startTime;
    }

    public int getRemainingTime() {
        return levelTime - (int)(getElapsedTime() / 1000);
    }

    public int getSpawnTime() {
        int spawnInterval = getSpawnInterval() * 10;
        return spawnInterval - (int) ((getElapsedTime() / 100) % spawnInterval);
    }
}
