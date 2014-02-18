package polly.rx.core.orion;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import polly.rx.MSG;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorType;
import polly.rx.core.orion.pathplanning.Graph;
import polly.rx.core.orion.pathplanning.Graph.EdgeCosts;
import polly.rx.core.orion.pathplanning.Graph.LazyBuilder;
import polly.rx.parsing.ParseException;


public final class QuadrantUtils {

    private QuadrantUtils() {}
    
    
    private final static EdgeCosts<Costs> COST_CALCULATOR = new EdgeCosts<Costs>() {
        @Override
        public double calculate(Costs data) {
            return data.isDiagonal ? 1.0 : 2.0;
        }
    };
    
    
    
    private static class Costs {
        private final boolean isDiagonal;
        
        public Costs(boolean isDiagonal) {
            this.isDiagonal = isDiagonal;
        }
    }
    
    
    
    public interface SectorFilter {
        public boolean accept(Sector sector);
    }
    
    
    
    public final static SectorFilter ACCEPT_ALL = new SectorFilter() {
        @Override
        public boolean accept(Sector sector) {
            return true;
        }
    };
    
    
    
    public static String createMapKey(int x, int y) {
        return x + "_" + y; //$NON-NLS-1$
    }
    
    
    
    public static String createMapKey(Sector sector) {
        return createMapKey(sector.getX(), sector.getY());
    }
    
    
    
    /**
     * Parses a Sector specification given as <tt>&lt;Quadname&gt; &lt;x&gt; &lt;y&gt;
     * and returns the respective Sector instance retrieved from {@link Orion}.
     * 
     * @param s The String to parse.
     * @return The Sector.
     * @throws ParseException If the String has the wrong format.
     */
    public static Sector parse(String s) throws ParseException {
        // parse backwards
        try {
            int i = s.lastIndexOf(' ');
            final int y = Integer.parseInt(s.substring(i + 1));
            s = s.substring(0, i);
            i = s.lastIndexOf(' ');
            final int x = Integer.parseInt(s.substring(i + 1));
            final String quadName = s.substring(0, i);
            return Orion.INSTANCE.getQuadrantProvider().getQuadrant(
                    quadName).getSector(x, y);
        } catch (Exception e) {
            throw new ParseException(MSG.routeParseError, e);
        }
    }
    
    
    
    public static int getDistance(Sector source, Sector target, final Quadrant quadrant) {
        final Graph<Sector, Costs> g = new Graph<>();
        return getDistance(source, target, quadrant, g);
    }
    
    
    
    private static int getDistance(Sector source, Sector target, final Quadrant quadrant, 
            Graph<Sector, Costs> g) {
        final LazyBuilder<Sector, Costs> builder = new LazyBuilder<Sector, Costs>() {
            
            final Set<Sector> done = new HashSet<>();
            
            @Override
            public void collectIncident(Graph<Sector, Costs> source, Sector currentNode) {
                if (!done.add(currentNode)) {
                    return;
                }
                for (int i = -1; i < 2; ++i) {
                    for (int j = -1; j < 2; ++j) {
                        if (i == 0 && j == 0) {
                            // exclude sector itself
                            continue;
                        }
                        final int x = currentNode.getX() + i;
                        final int y = currentNode.getY() + j;
                        final boolean diagonal = Math.abs(i) == 1 && Math.abs(j) == 1;
                        
                        if (x >= 0 && y >= 0 && x <= quadrant.getMaxX() && y <= quadrant.getMaxY()) {
                            final Sector neighbor = quadrant.getSector(x, y);
                            
                            if (neighbor.getType() != SectorType.NONE) {
                                final Graph<Sector, Costs>.Node current = source.getNode(currentNode);
                                final Graph<Sector, Costs>.Node node = source.getNode(neighbor, neighbor);
                                current.edgeTo(node, new Costs(diagonal));
                            }
                        }
                    }
                }
            }
        };
        final Graph<Sector, Costs>.Path path = g.findShortestPath(source, target, builder, 
                Graph.<Sector>noHeuristic(), COST_CALCULATOR);
        if (path.getPath().isEmpty() && !source.equals(target)) {
            // no path found <=> max distance
            return Integer.MAX_VALUE;
        }
        return path.getPath().size();
    }
    
    
    
    public static List<Sector> getNearSectors(Sector source, Quadrant quadrant, 
            int maxDistance, SectorFilter filter) {
        final List<Sector> result = new ArrayList<>();
        final Graph<Sector, Costs> g = new Graph<>();
        
        for (int i = -maxDistance; i < maxDistance; ++i) {
            for (int j = -maxDistance; j < maxDistance; ++j) {
                final int x = source.getX() + i;
                final int y = source.getY() + j;
                if (x >= 0 && y >= 0 && x <= quadrant.getMaxX() && y <= quadrant.getMaxY()) {
                    final Sector sector = quadrant.getSector(x, y);
                    
                    if (sector.getType() != SectorType.NONE && filter.accept(sector)) {
                        final int dist = getDistance(source, sector, quadrant, g);
                        if (dist <= maxDistance) {
                            result.add(sector);
                        }
                    }
                        
                }
            }
        }
        return result;
    }
    
    
    
    public static BufferedImage createQuadImage(Quadrant quad) {
        final int ss = 10; // sector size in pixels
        final Color background = new Color(51, 51, 102, 255);
        final BufferedImage img = new BufferedImage(quad.getMaxX() * ss, 
                quad.getMaxY() * ss, BufferedImage.TYPE_INT_ARGB);
        
        final Graphics2D g = img.createGraphics();
        g.setColor(background);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        
        final ImageObserver obs = new ImageObserver() {
            @Override
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width,
                    int height) {
                return false;
            }
        };
        
        for (int y = 0; y < quad.getMaxY(); ++ y) {
            for (int x = 0; x < quad.getMaxX(); ++x) {
                final Sector s = quad.getSector(x + 1, y + 1);
                if (s.getType() != SectorType.NONE) {
                    final BufferedImage sectorImage = s.getType().getImage();
                    g.drawImage(sectorImage, x * ss, y * ss, ss, ss, obs);
                }
            }
        }
        return img;
    }
}
