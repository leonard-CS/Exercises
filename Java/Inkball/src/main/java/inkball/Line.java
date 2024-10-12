package inkball;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Line {
    private ArrayList<PVector> points;

    public Line(float x, float y) {
        points = new ArrayList<>();
        points.add(new PVector(x, y));
    }

    public void addPoint(int x, int y) {
        points.add(new PVector(x, y));
    }

    public void draw(PApplet p) {
        p.stroke(0);
        p.strokeWeight(2);
        p.noFill();

        p.beginShape();
        for (PVector point : points) {
            p.curveVertex(point.x, point.y);
        }
        p.endShape();
    }
}
