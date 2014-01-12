package polly.rx.core.orion.model;

import java.util.Objects;

public final class OrionObjectUtil {

    public static boolean sectorsEqual(Sector s1, Sector s2) {
        return s1.getX() == s2.getX() && s1.getY() == s2.getY()
                && s1.getQuadName().equals(s2.getQuadName());
    }



    public static int sectorHash(Sector s) {
        return Objects.hash(s.getQuadName(), s.getX(), s.getY());
    }



    public static String sectorString(Sector s) {
        return String.format("%s %d, %d", s.getQuadName(), s.getX(), s.getY()); //$NON-NLS-1$
    }



    public static boolean wormholesEquals(Wormhole h1, Wormhole h2) {
        return h1.getSource().equals(h2.getSource())
                && h1.getTarget().equals(h2.getTarget());
    }



    public static int wormholeHash(Wormhole w) {
        return Objects.hash(w.getSource(), w.getTarget());
    }



    public static String wormholeString(Wormhole w) {
        return String
                .format("%s - von: %s %d,%d nach: %s %d,%d. Entladung: %d-%d", //$NON-NLS-1$
                        w.getName(), w.getSource().getQuadName(), w.getSource().getX(), w
                                .getSource().getY(), w.getTarget().getQuadName(), w
                                .getTarget().getX(), w.getTarget().getY(), w
                                .getMinUnload(), w.getMaxUnload());
    }



    public static boolean productionEquals(Production p1, Production p2) {
        return p1.getRess() == p2.getRess();
    }



    public static String productionString(Production p) {
        return String.format("%s: %.2f", p.getRess(), p.getRate()); //$NON-NLS-1$
    }



    public static int productionHash(Production p1) {
        return Objects.hash(p1.getRess());
    }



    public static boolean quadrantsEquals(Quadrant quad1, Quadrant quad2) {
        return quad1.getName().equalsIgnoreCase(quad2.getName());
    }



    public static int quadrantHash(Quadrant q) {
        return Objects.hash(q.getName().toLowerCase());
    }



    public static String quadrantString(Quadrant q) {
        return q.getName();
    }



    public static boolean portalsEqual(Portal p1, Portal p2) {
        return p1.getType() == p2.getType() && p1.getOwner().equals(p2.getOwner());
    }



    public static int portalHash(Portal p) {
        return Objects.hash(p.getType(), p.getOwner());
    }



    public static String portalString(Portal p) {
        return p.getType().toString() + " " + p.getOwner(); //$NON-NLS-1$
    }



    private OrionObjectUtil() {
    }
}