package polly.rx.graphs;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import polly.rx.graphs.Point.PointType;


public class Graph {
    
    private final Color GRID_COLOR = new Color(230, 230, 230);
    
    
    // TEST: Graph
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                final ImageGraph graph = new ImageGraph(600, 400);
                
                String[] label = new String[24];
                for (int i = 0; i < label.length; ++i) {
                    label[i] = "" + (24 - i); //$NON-NLS-1$
                }
                
                final PointSet set = new PointSet();
                set.setName("Rot"); //$NON-NLS-1$
                final PointSet set2 = new PointSet(Color.BLUE);
                set2.setName("Blau"); //$NON-NLS-1$
                set.setColor(Color.RED);
                graph.addPointSet(set);
                graph.addPointSet(set2);
                graph.setxLabels(label);
                graph.setDrawGridVertical(true);
                
                for (int i = 0; i < 24; ++i) {
                    set.add(new Point(i+0.5, i*i*50, PointType.X));
                    set2.add(new Point(i, i*1000, PointType.DOT));
                }
                
                JPanel p = new JPanel() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void paintComponent(Graphics g) {
                        graph.updateImage();
                        graph.drawImageTo((Graphics2D) g);
                    }
                };
                
                
                PointSet test = new PointSet(Color.GREEN);
                test.setName("Green"); //$NON-NLS-1$
                test.add(1, 2000, PointType.DOT);
                test.add(1, 2500, PointType.DOT);
                test.add(1, 3000, PointType.DOT);
                test.add(1, 6000, PointType.DOT);
                graph.addPointSet(test);
                
                graph.setRightScale(new YScale("Just a test", 0, 200, 20)); //$NON-NLS-1$
                PointSet rightSet = new PointSet(Color.BLACK);
                for (int i = 0; i < 24; ++i) {
                    rightSet.add(i, Math.random() * 200, PointType.BOX);
                }
                rightSet.setConnect(true);
                rightSet.setConnectColor(Color.LIGHT_GRAY);
                
                graph.addHighlightArea(new HighlightArea("Test", 2, 5, new Color(0, 0, 0, 20))); //$NON-NLS-1$
                
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
    private boolean drawGridVertical;
    private final Collection<PointSet> data;
    private final Collection<HighlightArea> highlights;
    private final Collection<NamedPoint> rawPoints;
    private YScale left;
    private YScale right;
    
    
    
    public Graph(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new ArrayList<PointSet>();
        this.highlights = new ArrayList<HighlightArea>();
        this.rawPoints = new TreeSet<NamedPoint>();
    }
    
    
    
    public void addHighlightArea(HighlightArea ha) {
        this.highlights.add(ha);
    }
    
    
    
    public YScale getLeftScale() {
        return this.left;
    }
    
    
    
    public void setLeftScale(YScale left) {
        this.left = left;
    }
    
    
    
    public YScale getRightScale() {
        return this.right;
    }
    
    
    
    public void setRightScale(YScale right) {
        this.right = right;
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


    
    public boolean isDrawGridVertical() {
        return this.drawGridVertical;
    }


    
    public void setDrawGridVertical(boolean drawGridVertical) {
        this.drawGridVertical = drawGridVertical;
    }


    
    public void addPointSet(PointSet pointSet) {
        this.data.add(pointSet);
    }
    
    
    
    public Collection<PointSet> getData() {
        return this.data;
    }
    
    
    
    public Collection<NamedPoint> getRawPointsFromLastDraw() {
        return this.rawPoints;
    }
    

    
    private void drawScale(YScale scale, Graphics2D g, int actualHeight, 
            int actualWidth, int xOffset, boolean left, int yLabelWidth) {
        
        final int steps = (int) Math.round(
            (scale.getMax() - scale.getMin()) / (double) scale.getStep());
        final double yScale = (double)actualHeight / (double)steps;
        final FontMetrics m = g.getFontMetrics();
        
        final int newX;
        if (left) {
            newX =  xOffset + -(int)yLabelWidth + 5;
        } else {
            newX = xOffset + 5;
        }
        
        g.setColor(Color.BLACK);
        for(int y = 0; y <= steps; ++y) {
            int newY = -(int) Math.round(y * yScale);
            
            if (scale.isDrawGrid() && y > 0) {
                g.setColor(GRID_COLOR);
                g.drawLine(0, newY, actualWidth, newY);
                g.setColor(Color.BLACK);
            }
            
            if (y != steps) {
                // ommit last tick
                g.drawLine(-2 + xOffset, newY, 2 + xOffset, newY);
            }
            
            g.drawString("" + (y * scale.getStep() + scale.getMin()), newX,  //$NON-NLS-1$
                newY + m.getAscent() / 2);
        }
        
        g.drawLine(xOffset, 0, xOffset, -actualHeight);
        
        final Font f = g.getFont();
        final Font fCopy = f.deriveFont(Font.BOLD);
        g.setFont(fCopy);
        g.drawString(scale.getName(), 
            newX, 
            -actualHeight - m.getAscent() / 2);
        g.setFont(f);
    }
    
    
    
    public void draw(Graphics2D g) {
        final FontMetrics m = g.getFontMetrics();
        final double xLabelWidth = this.getLongestStringWidth(this.xLabels, m) + 10.0;
        final double leftYLabelWidth = Math.max(
            m.stringWidth("" + this.left.getMax()),  //$NON-NLS-1$
            m.stringWidth(this.left.getName())) + 10.0;
        
        double rightYLabelWidth;
        if (this.right == null) {
            rightYLabelWidth = xLabelWidth;
        } else {
            rightYLabelWidth = Math.max(
                Math.max(
                    (int)xLabelWidth, 
                    m.stringWidth("" + this.right.getMax())),  //$NON-NLS-1$
                m.stringWidth(this.right.getName())) + 10.0;
        }
        
        
        final int zeroX = (int) Math.round(leftYLabelWidth);
        final int zeroY = (int) Math.round(this.height - xLabelWidth);
        final int actualWidth = this.width - zeroX - (int) Math.round(rightYLabelWidth);
        final int actualHeight = this.height - (int)xLabelWidth - 20;

        g.setBackground(new Color(244, 244, 244));
        g.clearRect(0, 0, this.width, this.height);
        g.setColor(Color.BLACK);
        
        g.translate(zeroX, zeroY);
        
        // x axis
        g.drawLine(0, 0, actualWidth, 0);
        
        final double xScale = (double) actualWidth / (this.xLabels.length - 1);
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
        
        // left y axis
        if (this.left != null) {
            this.drawScale(this.left, g, actualHeight, actualWidth, 0, 
                true, (int)leftYLabelWidth);
        }
        
        if (this.right != null) {
            this.drawScale(this.right, g, actualHeight, actualWidth, 
                actualWidth, false, (int)rightYLabelWidth);
        } else {
            final int x = actualWidth;// - (int) rightYLabelWidth;
            g.setColor(Color.BLACK);
            g.drawLine(x, 0, x, -actualHeight);
        }
        
        this.drawPoints(g, this.data, actualHeight, zeroX, zeroY, xScale);
        
        g.setColor(Color.BLACK);
        g.drawLine(0, -actualHeight, actualWidth, -actualHeight);
        this.drawLegend(g, actualWidth);
        this.drawHighlightAreas(g, actualHeight, xScale);
    }
    
    
    
    private void drawPoints(Graphics2D g, Collection<PointSet> pointSets,
            int actualHeight, int zeroX, int zeroY, double xScale) {
        
        final Stroke reset = g.getStroke();
        
        this.rawPoints.clear();
        for (final PointSet points : pointSets) {
            YScale yScale = points.getScale();
            double scale = (double)actualHeight / (yScale.getMax() - yScale.getMin());
            
            boolean first = true;
            int lastX = 0;
            int lastY = 0;
            for (final Point p : points) {
                final int x = (int) Math.round(p.getX() * xScale);
                final int y = -(int) Math.round((p.getY() - yScale.getMin()) * scale);
                
                if (y > 0) {
                    continue;
                }
                
                if (points.isConnect() && !first) {
                    g.setColor(points.getConnectColor());
                    g.setStroke(new BasicStroke(points.getStrength()));
                    g.drawLine(lastX, lastY, x, y);
                    g.setStroke(reset);
                }
                
                first = false;
                g.setColor(points.getColor());
                drawPoint(g, x, y, p.getType());
                
                // retranslate
                if (p instanceof NamedPoint) {
                    this.rawPoints.add(new NamedPoint(((NamedPoint) p).getName(), 
                        x + zeroX, y + zeroY, PointType.NONE));
                } else {
                    this.rawPoints.add(new NamedPoint(points.getName() + ": " + p.getY(),  //$NON-NLS-1$
                        x + zeroX, y + zeroY, PointType.NONE));
                }
                lastX = x;
                lastY = y;
            }
        }
    }
    
    
    
    private void drawHighlightAreas(Graphics2D g, double actualHeight, double xScale) {
        final FontMetrics m = g.getFontMetrics();
        
        for (final HighlightArea ha : this.highlights) {
            g.setColor(ha.getColor());
            
            final int nameWidth = Math.round(m.stringWidth(ha.getName()));
            
            final int xmin = (int) Math.round(ha.getxMin() * xScale);
            final int xmax = (int) Math.round(ha.getxMax() * xScale);
            
            g.fillRect(xmin, -(int)actualHeight, xmax - xmin, (int) actualHeight);
            
            if (nameWidth > 0.5 * (xmax - xmin)) {
                continue;
            }
            g.setColor(Color.BLACK);
            int stringX = xmin + (xmax - xmin) / 2 - nameWidth / 2;
            g.drawString(ha.getName(), stringX, 
                -(int) actualHeight / 2);
        }
    }
    
    
    
    private void drawLegend(Graphics2D g, int actualWidth) {
        final FontMetrics m = g.getFontMetrics(); 
        double width = 0.0;
        
        for (final PointSet points : this.data) {
            width = Math.max(width, m.stringWidth(points.getName()));
        }
        
        final int legendX = (int) Math.round(actualWidth - (width + 30.0));
        int legendY = -20;

        for (final PointSet points : this.data) {
            if (!points.getName().equals("")) { //$NON-NLS-1$
                g.setColor(points.getColor());
                g.fillRect(legendX, legendY, 10, 10);
                g.drawString(points.getName(), legendX + 15, legendY + 10);
                legendY -= 20;
            }
        }
        g.setColor(Color.BLACK);
    }
    
    
    
    private void drawPoint(Graphics2D g, int x, int y, PointType type) {
        switch (type) {
        case X:
            this.drawCross(g, x, y);
            break;
        case DOT:
            g.drawOval(x - 2, y - 2, 4, 4);
            break;
        case BOX:
            g.drawRect(x - 2, y - 2, 4, 4);
            break;
        default:
            break;
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