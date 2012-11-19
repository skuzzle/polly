package polly.rx.graphs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import polly.rx.entities.ScoreBoardEntry;


public class SimpleGraph {
    
    
    private final static double X_MARGIN = 0.1; // %
    private final static double Y_MARGIN = 0.15; // %
    
    
    
    public byte[] storeImage(BufferedImage image) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", out);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return out.toByteArray();
    }
    
    
    
    private final static int Y_STEPS = 10;
    private final static int X_STEPS = 25;
    
    
    
    public BufferedImage createGraph(List<ScoreBoardEntry> entries, 
            ScoreBoardEntry oldest, ScoreBoardEntry youngest, int width, int height) {
        
        final BufferedImage img = new BufferedImage(width, height, 
                BufferedImage.TYPE_INT_RGB);
        
        if (entries.isEmpty()) {
            return img;
        }
        
        int maxPoints = entries.iterator().next().getPoints();
        int minPoints = maxPoints;
        for (final ScoreBoardEntry e : entries) {
            maxPoints = Math.max(maxPoints, e.getPoints());
            minPoints = Math.min(minPoints, e.getPoints());
        }
        
        int xZero = (int)(width * X_MARGIN);
        int yZero = height - (int)(height * Y_MARGIN);
        
        int actualHeight = height - 2 * (int)(height * Y_MARGIN);
        int actualWidth = width - xZero * 2;
        
        double pointDiff = Math.abs(maxPoints - minPoints);
        
        double yScale = (double) actualHeight / pointDiff;
        double xScale = (double) actualWidth / Math.abs(oldest.getDate().getTime() - youngest.getDate().getTime()); // px/ms
        
        final Graphics2D g = img.createGraphics();
        
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        

        
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, width, height);
        g.translate(xZero, yZero);
        
        g.setColor(Color.BLACK);
        g.drawLine(0, 0, width - xZero*2, 0);  // x axis
        g.drawLine(0, 0, 0, (height - yZero*2)); // y axis
        
        
        
        int yStep = (int) (pointDiff / Y_STEPS);
        // draw y axis
        for (int i = minPoints; i <= maxPoints; i += yStep) {
            double y = -Math.abs(minPoints - i) * yScale;
            if (i - minPoints > 0) {
                g.setColor(Color.LIGHT_GRAY);
                g.drawLine(1, (int)y, actualWidth, (int)y);
            }
            g.setColor(Color.BLACK);
            g.drawLine(-2, (int)y, 2, (int)y);
            g.drawString("" + i, -30, (int)y+4);
        }
        
        g.setColor(Color.BLACK);
        
        
        double lastX = 0;
        double lastY = 0;
        
        int xDensity = entries.size() < X_STEPS ? 1 : actualWidth / X_STEPS;
        int i = 0;
        for (ScoreBoardEntry e : entries) {
            double newX = Math.round(Math.abs(e.getDate().getTime() - oldest.getDate().getTime()) * xScale);
            double newY = -Math.round(Math.abs(minPoints - e.getPoints()) * yScale);
            
            int x = (int)newX;
            int y = (int)newY;
            
            if (i > 0 && i%xDensity == 0) {
                g.setColor(Color.LIGHT_GRAY);
                g.drawLine(x, 0, x, height-yZero*2);
            }
            g.setColor(Color.BLACK);
            
            //g.fillOval(x-3, y-3, 6, 6);
            
            if (i%xDensity == 0) {
                g.drawLine(x, 2, x, -2);
                g.rotate(Math.PI/4.0, x-2, 10);
                g.drawString("" + ScoreBoardEntry.CSV_DATE_FORMAT.format(e.getDate()), x-2, 10);
                g.rotate(-Math.PI/4.0, x-2, 10);
            }
            
            g.drawLine((int)lastX, (int)lastY, x, y);
            lastX = newX;
            lastY = newY;
            ++i;
        }
        
        return img;
    }
}
