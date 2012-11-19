package polly.rx.graphs;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.skuzzle.polly.tools.streams.FastByteArrayOutputStream;


public class ImageGraph extends Graph {

    private final BufferedImage image;
    
    
    
    public ImageGraph(int width, int height, int minY, int maxY, int stepY) {
        super(width, height, minY, maxY, stepY);
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    
    
    
    public void updateImage() {
        this.draw(this.image.createGraphics());
    }
    
    
    
    public byte[] getBytes() {
        final FastByteArrayOutputStream out = new FastByteArrayOutputStream(); 
        try {
            ImageIO.write(this.image, "png", out);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return out.getBuffer();
    }
}
