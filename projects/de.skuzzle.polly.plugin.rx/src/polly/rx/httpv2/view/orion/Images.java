package polly.rx.httpv2.view.orion;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import polly.rx.core.orion.model.SectorType;


public final class Images {
    
    public static BufferedImage[] EMPTY_ROOM; 

    private final static Map<String, BufferedImage> CACHE;
    static {
        CACHE = new HashMap<String, BufferedImage>();
        final SectorType e = SectorType.EMPTY;
        final int c = e.getMaxId() - e.getId() + 1;
        EMPTY_ROOM = new BufferedImage[c];
        for (int i = 0; i < c; ++i) {
            final String name = "" + (e.getId() + i) + ".gif"; //$NON-NLS-1$ //$NON-NLS-2$
            EMPTY_ROOM[i] = getImage(name);
        }
    }
    

    public static BufferedImage getImage(String name) {
        synchronized (CACHE) {
            final BufferedImage cached = CACHE.get(name);
            if (cached != null) {
                return cached;
            }
        }
        final InputStream is = Images.class.getResourceAsStream(name);
        if (is != null) {
            try {
                final BufferedImage img = ImageIO.read(is);
                synchronized (CACHE) {
                    CACHE.put(name, img);
                }
                return img;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
    }
    
    
    
    private Images() {}
}
