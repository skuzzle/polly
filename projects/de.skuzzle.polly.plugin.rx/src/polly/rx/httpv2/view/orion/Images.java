package polly.rx.httpv2.view.orion;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import polly.rx.core.orion.model.SectorType;


public final class Images {
    
    public static BufferedImage[] EMPTY_ROOM; 
            
    static {
        final SectorType e = SectorType.EMPTY;
        final int c = e.getMaxId() - e.getId() + 1;
        EMPTY_ROOM = new BufferedImage[c];
        for (int i = 0; i < c; ++i) {
            final String name = "" + (e.getId() + i) + ".gif"; //$NON-NLS-1$ //$NON-NLS-2$
            EMPTY_ROOM[i] = getImage(name);
        }
    }
    

    public static BufferedImage getImage(String name) {
        final InputStream is = Images.class.getResourceAsStream(name);
        if (is != null) {
            try {
                final BufferedImage img = ImageIO.read(is);
                return img;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
    }
    
    
    
    private Images() {}
}
