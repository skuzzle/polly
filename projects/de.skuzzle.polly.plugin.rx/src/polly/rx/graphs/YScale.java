package polly.rx.graphs;

public class YScale {

    private final int min;
    private final int max;
    private final int step;
    private boolean drawGrid;
    private final String name;



    public YScale(String name, int min, int max, int step) {
        super();
        this.name = name;
        this.min = min;
        this.max = max;
        this.step = step;
    }



    public String getName() {
        return this.name;
    }



    public boolean isDrawGrid() {
        return this.drawGrid;
    }



    public void setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
    }



    public int getMin() {
        return this.min;
    }



    public int getMax() {
        return this.max;
    }



    public int getStep() {
        return this.step;
    }

}
