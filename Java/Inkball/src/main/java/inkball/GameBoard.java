package inkball;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class GameBoard {
    public final int numRows;
    public final int numCols;
    private Cell[][] board;
    private PApplet pApplet;

    private final int currentLevelIndex;
    private String layout;

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
    private PImage[] holeImages = new PImage[NUM_IMAGES];
    private PImage[] wallImages = new PImage[NUM_IMAGES];

    public GameBoard(int numRows, int numCols, PApplet pApplet, int currentLevelIndex, JSONObject config) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];

        this.pApplet = pApplet;
        this.currentLevelIndex = currentLevelIndex;
        this.startTime = pApplet.millis();
        loadImages();
        loadLevelConfig(config);
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
        JSONArray balls = currentLevel.getJSONArray("balls");
        
        // printLevelInfo(layout, levelTime, levelSpawnInterval, levelScoreIncreaseModifier, levelScoreDecreaseModifier, balls);
    }

    private void loadImages() {
        entryPointImage = pApplet.loadImage("src/main/resources/inkball/entrypoint.png");
        tileImage = pApplet.loadImage("src/main/resources/inkball/tile.png");

        for (int i = 0; i < NUM_IMAGES; i++) {
            holeImages[i] = pApplet.loadImage("src/main/resources/inkball/hole" + i + ".png");
            wallImages[i] = pApplet.loadImage("src/main/resources/inkball/wall" + i + ".png");
        }
    }

    private void createBoard(String filePath) {
        initializeBoardWithTiles();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
                String line = br.readLine();
                if (line != null) {
                    for (int colIndex = 0; colIndex < numCols; colIndex++) {
                        char cellChar = line.charAt(colIndex);
                        switch (cellChar) {
                            case 'X':
                                board[rowIndex][colIndex] = new WallCell(wallImages[0]);
                                break;
                            case 'S':
                                board[rowIndex][colIndex] = new WallCell(entryPointImage);
                                break;
                            case 'H':
                                board[rowIndex][colIndex] = new HoleCell(holeImages[line.charAt(colIndex+1) - '0']);
                                board[rowIndex][colIndex+1] = null;
                                board[rowIndex+1][colIndex] = null;
                                board[rowIndex+1][colIndex+1] = null;
                                colIndex++;
                                break;
                            case 'B':
                                board[rowIndex][colIndex] = new TileCell(tileImage);
                                colIndex++;
                                break;
                            default:
                                if (cellChar >= '0' && cellChar <= '4') {
                                    board[rowIndex][colIndex] = new WallCell(wallImages[cellChar - '0']);
                                }
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception
        }
    }

    private void initializeBoardWithTiles() {
        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            for (int colIndex = 0; colIndex < numCols; colIndex++) {
                board[rowIndex][colIndex] = new TileCell(tileImage);
            }
        }
    }

    // Getters and setters
    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    public int getLevelTime() {
        return levelTime;
    }

    public int getSpawnInterval() {
        return spawnInterval;
    }

    public int getScore() {
        return score;
    }
}
