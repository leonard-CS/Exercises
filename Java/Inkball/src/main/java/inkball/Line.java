package inkball;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Line {
    private static final float LINE_THICKNESS = 10;
    private final ArrayList<PVector> points;

    public Line(float x, float y) {
        points = new ArrayList<>();
        points.add(new PVector(x, y));
    }

    public void addPoint(int x, int y) {
        points.add(new PVector(x, y));
    }

    public void draw(PApplet p) {
        p.stroke(0);
        p.strokeWeight(LINE_THICKNESS);
        p.noFill();

        p.beginShape();
        for (PVector point : points) {
            p.curveVertex(point.x, point.y);
        }
        p.endShape();
    }

    public boolean isMouseNear(int mouseX, int mouseY) {
        for (PVector point : points) {
            float distance = PVector.dist(new PVector(mouseX, mouseY), point);
            if (distance < App.COLLISION_THRESHOLD) {
                return true;
            }
        }
        return false;
    }
}
