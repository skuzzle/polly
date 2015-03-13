package polly.rx.httpv2.view.orion;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import polly.rx.core.orion.model.SectorType;


public final class Images {

    public static final BufferedImage[] EMPTY_ROOM;

    private static final BufferedImage GRADIENT;

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
        GRADIENT = getImage("gradient.jpg");
    }

    /**
     * Gets a gradient color between green and red where 0 percentage is full
     * green and 1 percentage is full red.
     */
    public static Color getGradientColor(double percentage) {
        final int gradWidth = GRADIENT.getWidth() - 1;
        final int x = (int) Math.floor(gradWidth * percentage);
        return new Color(GRADIENT.getRGB(x, 0));
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
