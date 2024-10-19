package inkball;

import processing.core.PImage;

public class WallCell extends Cell {
    private final Color color;

    public WallCell(int x, int y, PImage image, Color color) {
        super(x, y, image);
        this.color = color;
    }

    public inkball.Color getColor() {
        return color;
    }
}
