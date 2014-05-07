package polly.rx.core.orion.model;

import java.util.Arrays;
import java.util.Objects;

import polly.rx.entities.RxRessource;

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



    public static int compareProduction(Production prod1, Production prod2) {
        int c = prod1.getRess().compareTo(prod2.getRess());
        if (c == 0) {
            return Double.compare(prod1.getRate(), prod2.getRate());
        }
        return c;
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
        return p1.getType() == p2.getType()
                && p1.getOwnerName().equals(p2.getOwnerName())
                && p1.getSector().equals(p2.getSector());
    }



    public static int portalHash(Portal p) {
        return Objects.hash(p.getType(), p.getOwnerName());
    }



    public static String portalString(Portal p) {
        return p.getType().toString() + " " + p.getOwnerName(); //$NON-NLS-1$
    }



    public static String fleetString(Fleet f) {
        return f.getName() + " - " + f.getOwnerName(); //$NON-NLS-1$
    }



    public static int fleetHash(Fleet f) {
        return Objects.hash(f.getOwnerName(), f.getName());
    }



    public static boolean fleetsEqual(Fleet f1, Fleet f2) {
        return f1.getOwnerName().equals(f2.getOwnerName())
                && f1.getName().equals(f2.getName());
    }



    public static boolean resourcesEquals(Resources d1, Resources d2) {
        for (final RxRessource ress : RxRessource.values()) {
            if (d1.getAmount(ress) != d2.getAmount(ress)) {
                return false;
            }
        }
        return true;
    }



    public static String resourcesString(Resources drop) {
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < RxRessource.values().length; ++i) {
            b.append(RxRessource.values()[i].toString());
            b.append(": "); //$NON-NLS-1$
            b.append(drop.getAmount(RxRessource.values()[i]));
            if (i != RxRessource.values().length - 1) {
                b.append(", "); //$NON-NLS-1$
            }
        }
        return b.toString();
    }



    public static int resourcesHash(Resources drop) {
        return Arrays.hashCode(drop.getAmountArray());
    }



    public static boolean dropEquals(Drop d1, Drop d2) {
        if (d1.hasArtifact() != d2.hasArtifact()) {
            return false;
        }
        return resourcesEquals(d1, d2);
    }



    public static String dropString(Drop drop) {
        final StringBuilder b = new StringBuilder();
        b.append(resourcesString(drop));
        if (drop.hasArtifact()) {
            b.append(", Artifact: yes"); //$NON-NLS-1$
        } else {
            b.append(", Artifact: no"); //$NON-NLS-1$
        }
        return b.toString();
    }



    public static int dropHash(Drop drop) {
        return Objects.hash(Arrays.hashCode(drop.getAmountArray()), drop.hasArtifact());
    }



    public static int competitorHash(BattleReportCompetitor competitor) {
        return Objects.hash(competitor.getOwnerName(), competitor.getKw(),
                competitor.getShips());
    }



    public static boolean competitorsEqual(BattleReportCompetitor c1,
            BattleReportCompetitor c2) {
        return c1.getOwnerName().equals(c2.getOwnerName()) && c1.getKw() == c2.getKw()
                && c1.getShips().equals(c2.getShips());
    }



    public static String competitorString(BattleReportCompetitor c) {
        final StringBuilder b = new StringBuilder();
        b.append(c.getOwnerName());
        b.append(c.getOwnerClan());
        b.append(" "); //$NON-NLS-1$
        b.append(c.getKw());
        b.append("/"); //$NON-NLS-1$
        b.append(c.getXpMod());
        return b.toString();
    }



    public static int reportHash(BattleReport report) {
        return Objects.hash(report.getTactic(), report.getAttacker(),
                report.getDefender(), report.getDrop(), report.getDate());
    }



    public static boolean reportsEqual(BattleReport b1, BattleReport b2) {
        return b1.getTactic() == b2.getTactic() && b1.getDate().equals(b2.getDate())
                && b1.getAttacker().equals(b2.getAttacker())
                && b1.getDefender().equals(b2.getDefender());
    }



    public static String reportString(BattleReport report) {
        final StringBuilder b = new StringBuilder();
        b.append(report.getAttacker());
        b.append(" vs. "); //$NON-NLS-1$
        b.append(report.getDefender());
        return b.toString();
    }



    public static boolean statsEqual(ShipStats s1, ShipStats s2) {
        return s1.getAw() == s2.getAw() && s1.getShields() == s2.getShields()
                && s1.getPz() == s2.getPz() && s1.getStructure() == s2.getStructure()
                && s1.getMinCrew() == s2.getMinCrew()
                && s1.getMaxCrew() == s2.getMaxCrew();
    }



    public static int statsHash(ShipStats stats) {
        return Objects.hash(stats.getAw(), stats.getShields(), stats.getPz(),
                stats.getStructure(), stats.getMinCrew(), stats.getMaxCrew());
    }



    public static String statsString(ShipStats stats) {
        final StringBuilder b = new StringBuilder();
        b.append("aw: "); //$NON-NLS-1$
        b.append(stats.getAw());
        b.append(", sh: "); //$NON-NLS-1$
        b.append(stats.getShields());
        b.append(", pz: "); //$NON-NLS-1$
        b.append(stats.getPz());
        b.append(", str: "); //$NON-NLS-1$
        b.append(stats.getStructure());
        return b.toString();
    }



    public static boolean alienRaceEquals(AlienRace r1, AlienRace r2) {
        return r1.isAggressive() == r2.isAggressive()
                && r1.getName().equals(r2.getName())
                && r1.getSubName().equals(r2.getSubName());
    }



    public static int alienRaceHash(AlienRace r) {
        return Objects.hash(r.getName(), r.getSubName(), r.isAggressive());
    }



    public static String alienRaceString(AlienRace r) {
        if (r.getSubName().isEmpty()) {
            return r.getName();
        }
        return r.getName() + " - " + r.getSubName(); //$NON-NLS-1$
    }



    public static String alienSpawnString(AlienSpawn spawn) {
        return spawn.getName() + " - " + spawn.getRace().toString(); //$NON-NLS-1$
    }



    public static int alienSpawnHash(AlienSpawn spawn) {
        return Objects.hash(spawn.getName(), spawn.getRace(), spawn.getSector());
    }

    

    public static boolean alienSpawnsEqual(AlienSpawn s1, AlienSpawn s2) {
        return s1.getName().equals(s2.getName()) && s1.getRace().equals(s2.getRace()) && 
                s1.getSector().equals(s2.getSector());
    }

    
    private OrionObjectUtil() {
    }

}
