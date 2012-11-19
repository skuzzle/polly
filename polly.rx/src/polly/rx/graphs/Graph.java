package polly.rx.graphs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class Graph {
    
    private final Color GRID_COLOR = new Color(230, 230, 230);
    
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                final Graph graph = new Graph(600, 400, 0, 30000, 2000);
                
                String[] label = new String[24];
                for (int i = 0; i < label.length; ++i) {
                    label[i] = "" + (24 - i);
                }
                
                final PointSet set = new PointSet();
                graph.setPointSet(set);
                graph.setxLabels(label);
                graph.setDrawGridHorizontal(true);
                graph.setDrawGridVertical(true);
                graph.setConnect(true);
                
                for (int i = 0; i < 24; ++i) {
                    set.add(new Point(i, Math.random()*30000));
                }
                
                JPanel p = new JPanel() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void paintComponent(Graphics g) {
                        graph.draw((Graphics2D)g);
                    }
                };
                
                frame.setLayout(new BorderLayout());
                frame.add(p, BorderLayout.CENTER);
                frame.setVisible(true);
                frame.pack();
                frame.setSize(new Dimension(700, 450));
            }
        });

    }
    
    
    private String[] xLabels;
    private final int width;
    private final int height;
    private int minY;
    private int maxY;
    private int stepY;
    private boolean drawGridVertical;
    private boolean drawGridHorizontal;
    private boolean connect;
    private PointSet pointSet;
    
    
    
    public Graph(int width, int height, int minY, int maxY, int stepY) {
        this.width = width;
        this.height = height;
        this.minY = minY;
        this.maxY = maxY;
        this.stepY = stepY;
    }
    
    
    
    public int getWidth() {
        return this.width;
    }


    
    public int getHeight() {
        return this.height;
    }



    public String[] getxLabels() {
        return this.xLabels;
    }


    
    public void setxLabels(String[] xLabels) {
        this.xLabels = xLabels;
    }



    public int getMinY() {
        return this.minY;
    }


    
    public void setMinY(int minY) {
        this.minY = minY;
    }


    
    public int getMaxY() {
        return this.maxY;
    }


    
    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }


    
    public int getStepY() {
        return this.stepY;
    }


    
    public void setStepY(int stepY) {
        this.stepY = stepY;
    }


    
    public boolean isDrawGridVertical() {
        return this.drawGridVertical;
    }


    
    public void setDrawGridVertical(boolean drawGridVertical) {
        this.drawGridVertical = drawGridVertical;
    }


    
    public boolean isDrawGridHorizontal() {
        return this.drawGridHorizontal;
    }


    
    public void setDrawGridHorizontal(boolean drawGridHorizontal) {
        this.drawGridHorizontal = drawGridHorizontal;
    }


    
    public boolean isConnect() {
        return this.connect;
    }


    
    public void setConnect(boolean connect) {
        this.connect = connect;
    }


    
    public PointSet getPointSet() {
        return this.pointSet;
    }


    
    public void setPointSet(PointSet pointSet) {
        this.pointSet = pointSet;
    }


    
    public void draw(Graphics2D g) {
        
        final FontMetrics m = g.getFontMetrics();
        final double xLabelWidth = this.getLongestStringWidth(this.xLabels, m) + 10.0;
        final double yLabelWidth = m.stringWidth("" + this.maxY) + 10.0;

        final int zeroX = (int) Math.round(yLabelWidth);
        final int zeroY = (int) Math.round(this.height - xLabelWidth);
        final int actualWidth = this.width - zeroX;
        final int actualHeight = this.height - (int)xLabelWidth;

        g.setColor(Color.WHITE);
        g.clearRect(0, 0, this.width, this.height);
        g.setColor(Color.BLACK);
        
        g.translate(zeroX, zeroY);
        g.drawLine(0, 0, 0, -actualHeight);
        g.drawLine(0, 0, actualWidth, 0);
        
        drawCross(g, 0, -actualHeight);
        drawCross(g, actualWidth, 0);
        
        final double xScale = actualWidth / this.xLabels.length;
        for(int x = 0; x < xLabels.length; ++x) {
            int newX = (int) Math.round(x * xScale);
            
            if (this.drawGridVertical && x > 0) {
                g.setColor(GRID_COLOR);
                g.drawLine(newX, 0, newX, -actualHeight);
                g.setColor(Color.BLACK);
            }
            
            g.drawLine(newX, -2, newX, 2);
            g.rotate(Math.PI / 4.0, newX, 10);
            g.drawString(xLabels[x], newX, 10);
            g.rotate(-Math.PI / 4.0, newX, 10);
            
        }
        
        final int steps = this.maxY / this.stepY;
        final double yScale = (double)actualHeight / (double)steps;
        for(int y = 0; y < steps; ++y) {
            int newY = -(int) Math.round(y * yScale);
            
            if (this.drawGridHorizontal && y > 0) {
                g.setColor(GRID_COLOR);
                g.drawLine(0, newY, actualWidth, newY);
                g.setColor(Color.BLACK);
            }
            
            g.drawLine(-2, newY, 2, newY);
            g.drawString("" + y * this.stepY, -(int)yLabelWidth + 5, 
                newY + m.getAscent() / 2);
        }
        
        int lastX = 0;
        int lastY = 0;
        g.setColor(this.pointSet.getColor());
        double scale = (double)actualHeight / (this.maxY - this.minY);
        for (final Point p : this.pointSet) {
            final int x = (int) Math.round(p.getX() * xScale);
            final int y = -(int) Math.round(p.getY() * scale);
            if (this.connect) {
                g.drawLine(lastX, lastY, x, y);
            }
            drawCross(g, x, y);
            lastX = x;
            lastY = y;
        }
    }
    
    
    
    private void drawCross(Graphics2D g, int x, int y) {
        g.rotate(Math.PI / 4.0, x, y);
        g.drawLine(x - 3, y, x + 3, y);
        g.drawLine(x, y - 3, x, y + 3);
        g.rotate(-Math.PI / 4.0, x, y);
    }
    
    
    
    private double getLongestStringWidth(String[] strings, FontMetrics m) {
        double stringWidth = 0.0;
        for (final String s : strings) {
            stringWidth = Math.max(stringWidth, m.stringWidth(s));
        }
        return stringWidth;
    }
}