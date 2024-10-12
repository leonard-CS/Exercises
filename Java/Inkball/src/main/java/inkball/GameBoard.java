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
    }

    private void loadImages() {
        entryPointImage = pApplet.loadImage("src/main/resources/inkball/entrypoint.png");
        tileImage = pApplet.loadImage("src/main/resources/inkball/tile.png");

        for (int i = 0; i < NUM_IMAGES; i++) {
            holeImages[i] = pApplet.loadImage("src/main/resources/inkball/hole" + i + ".png");
            wallImages[i] = pApplet.loadImage("src/main/resources/inkball/wall" + i + ".png");
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
                    board[rowIndex][colIndex] = new EntryPointCell(entryPointImage, x, y);
                    break;
                case 'H':
                    createHoleCell(line, rowIndex, colIndex);
                    colIndex++; // Skip the next index as it's part of the hole
                    break;
                case 'B':
                    board[rowIndex][colIndex] = new TileCell(tileImage, x, y);
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
