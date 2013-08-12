package polly.rx.graphs;

import java.awt.Color;

public class HighlightArea {

    private final String name;
    private final double xMin;
    private final double xMax;
    private final Color color;



    public HighlightArea(String name, double xMin, double xMax, Color color) {
        this.name = name;
        this.xMin = xMin;
        this.xMax = xMax;
        this.color = color;
    }

    

    public String getName() {
        return this.name;
    }



    public double getxMin() {
        return this.xMin;
    }



    public double getxMax() {
        return this.xMax;
    }



    public Color getColor() {
        return this.color;
    }
}
