package polly.rx.parsing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import polly.rx.MSG;
import polly.rx.core.orion.Orion;
import polly.rx.core.orion.model.DefaultBattleReport;
import polly.rx.core.orion.model.DefaultBattleReportCompetitor;
import polly.rx.core.orion.model.DefaultDrop;
import polly.rx.core.orion.model.DefaultReportShip;
import polly.rx.core.orion.model.DefaultSector;
import polly.rx.core.orion.model.DefaultShipStats;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.ReportShip;
import polly.rx.entities.BattleTactic;
import polly.rx.entities.RxRessource;
import polly.rx.entities.ShipHelper;
import polly.rx.entities.ShipType;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.sdk.time.Time;


public class NewModelReportParser {
    private final static DateFormat getDateFormat() {
        return new SimpleDateFormat("HH:mm dd-MM-yyyy"); //$NON-NLS-1$
    }
    
    
    public static DefaultBattleReport parse(String report) throws ParseException {
        try (Scanner s = new Scanner(report)) {
            final Pattern delimiter = s.delimiter();
            
            s.useLocale(Locale.ENGLISH);
            
            // date
            final DateFormat df = getDateFormat();
            Date date;
            try {
                date = df.parse(s.nextLine().trim());
                // round to minutes
                long d = date.getTime() / Milliseconds.fromMinutes(1) * Milliseconds.fromMinutes(1);
                date = new Date(d);
            } catch (java.text.ParseException e) {
                // ignore, use system date and go on
                date = Time.currentTime();
            }
            
            s.skip("\\D*"); //$NON-NLS-1$
            
            // drop
            final Map<RxRessource, Integer> d = new EnumMap<>(RxRessource.class);
            for (int i = 0; i < RxRessource.values().length; ++i) {
                final RxRessource res = RxRessource.values()[i];
                if (!s.hasNextInt()) {
                    throw new ParseException(
                            MSG.bind(MSG.qreportParserResourceExpected, res));
                }
                final int amount = s.nextInt();
                d.put(res, amount);
            }
            s.nextLine();
            final boolean artifact = s.hasNextInt() && s.nextInt() == 1;
            if (artifact) {
                s.nextLine();
            }
            
            final DefaultDrop drop = new DefaultDrop(d, artifact);
            
            if (!s.next().equals("Gefecht") || !s.next().equals("bei")) { //$NON-NLS-1$ //$NON-NLS-2$
                throw new ParseException(MSG.qreportParserAttackAtExpected);
            }
            
            String quadrant = s.findInLine("\\D+"); //$NON-NLS-1$
            if (quadrant == null) {
                throw new ParseException(MSG.qreportParserInvalidLocation);
            }
            quadrant = quadrant.trim();
            s.useDelimiter(",|\\s+"); //$NON-NLS-1$
            final int x = s.nextInt();
            final int y = s.nextInt();
            
            s.useDelimiter(delimiter);
            while (s.findInLine("Gefechtstaktik") == null) { //$NON-NLS-1$
                s.nextLine();
            }
            final BattleTactic tactic = BattleTactic.parseTactic(s.nextLine().trim());
            s.skip("\\s+Bonus Angreifer"); //$NON-NLS-1$
            s.useDelimiter("%"); //$NON-NLS-1$
            final float attackerBonus = 1.0f + (s.nextInt() / 100.0f);
            s.skip("%\\s+Bonus Verteidiger"); //$NON-NLS-1$
            final float defenderBonus = 1.0f + (s.nextInt() / 100.0f);
            s.skip("%\\s+Kampfwert Angreifer/XP-Mod\\s*"); //$NON-NLS-1$
            s.useDelimiter("/|\\s+"); //$NON-NLS-1$
            final float kwAttacker = s.nextFloat() / attackerBonus;
            final float xpModAttacker = s.nextFloat();
            s.skip("\\s+Kampfwert Verteidiger/XP-Mod"); //$NON-NLS-1$
            final float kwDefender = s.nextFloat() / defenderBonus;
            final float xpModDefender = s.nextFloat();
            
            
            // attacker fleet:
            s.skip("\\s+Angreifer Flotte: "); //$NON-NLS-1$
            s.useDelimiter(" \\("); //$NON-NLS-1$
            final String attackerFleetName = s.next();
            s.useDelimiter(" \\(|\\)"); //$NON-NLS-1$
            
            String attackerName = s.next();
            s.skip("\\)\\s*"); //$NON-NLS-1$
            final String attackerClan = VenadHelper.getClan(attackerName);
            attackerName = VenadHelper.getName(attackerName);
            
            final List<ReportShip> attackerShips = new ArrayList<>(50);
            while (s.findInLine("Verteidiger Flotte: ") == null) { //$NON-NLS-1$
                s.useDelimiter(delimiter);
                final DefaultReportShip ship = findShip(s, date);
                attackerShips.add(ship);
            }

            
            
            // Defender fleet
            s.useDelimiter(" \\("); //$NON-NLS-1$
            final String defenderFleetName = s.next();
            s.useDelimiter(" \\(|\\)"); //$NON-NLS-1$
            
            String defenderName = s.next();
            s.skip("\\)\\s*"); //$NON-NLS-1$
            final String defenderClan = VenadHelper.getClan(defenderName);
            defenderName = VenadHelper.getName(defenderName);
            
            final List<ReportShip> defenderShips = new ArrayList<>(50);
            s.useDelimiter(delimiter);
            
            while (s.hasNext()) {
                s.useDelimiter(delimiter);
                final DefaultReportShip ship = findShip(s, date);
                defenderShips.add(ship);
            }
            
            final DefaultBattleReportCompetitor attacker = 
                    new DefaultBattleReportCompetitor(attackerName, attackerClan, 
                            attackerFleetName, kwAttacker, xpModAttacker, attackerShips);
            final DefaultBattleReportCompetitor defender = 
                    new DefaultBattleReportCompetitor(defenderName, defenderClan, 
                            defenderFleetName, kwDefender, xpModDefender, defenderShips);
            
            final Quadrant q = Orion.INSTANCE.getQuadrantProvider().getQuadrant(quadrant);
            final DefaultSector location = new DefaultSector(q.getSector(x, y));
            
            final DefaultBattleReport result = new DefaultBattleReport(tactic, attacker, 
                    defender, location, drop, date);
            return result;
        }
    }
    
    
    
    @SuppressWarnings("unused")
    private static DefaultReportShip findShip(Scanner s, Date date) throws ParseException {
        s.skip("\\s*"); //$NON-NLS-1$
        final String shipName = s.nextLine(); 
        final String capiName = s.nextLine();
        
        // find aw
        final int currentAw = findAttribute1(s, "Angriffswert"); //$NON-NLS-1$
        final int aw = findAttribute2(s);
        final int awDmg = findDmg(s);
        
        // capi hp/xp
        final int currentHp = findAttribute1(s, "Captain"); //$NON-NLS-1$
        final int hp = findAttribute2(s);
        final int hpDmg = findDmg(s);
        final int capiXp = findAttribute1(s, "XPs"); //$NON-NLS-1$
        
        // shields
        final int currentShield = findAttribute1(s, "Schild"); //$NON-NLS-1$
        final int shield = findAttribute2(s);
        final int shieldDmg = findDmg(s);
        
        // crew
        final int currentCrew = findAttribute1(s, "Crew"); //$NON-NLS-1$
        final int crew = findAttribute2(s);
        final int crewDmg = findDmg(s);
        final int crewXp = findAttribute1(s, "XPs"); //$NON-NLS-1$
        
        // pz
        final int currentPz = findAttribute1(s, "Panzerung"); //$NON-NLS-1$
        final int pz = findAttribute2(s);
        final int pzDmg = findDmg(s);
        
        // systems
        final int currentSystem = findAttribute1(s, "Systeme"); //$NON-NLS-1$
        final int system = findAttribute2(s);
        final int systemDmg = findDmg(s);
        
        // structure
        final int currentStruct = findAttribute1(s, "Struktur"); //$NON-NLS-1$
        final int struct = findAttribute2(s);
        final int structDmg = findDmg(s);
        
        int id = 0;
        if (s.findInLine("ID:") != null) { //$NON-NLS-1$
            s.useDelimiter("\\D+"); //$NON-NLS-1$
            id = s.nextInt();
        }
        if (s.hasNextLine()) {
            s.nextLine();
        }
        
        final DefaultShipStats stats = new DefaultShipStats(aw, shield, pz, struct, 
                currentCrew, crew);
        final DefaultShipStats dmg = new DefaultShipStats(awDmg, shieldDmg, pzDmg, 
                structDmg, crewDmg, crewDmg);
        
        final ShipType type = ShipHelper.getShipType(shipName);
        final int shipClass = ShipHelper.getShipClass(shipName);
        final String simpleName = ShipHelper.getSimpleName(shipName);
        
        return new DefaultReportShip(type, simpleName, capiName, 
                stats, dmg, shipClass, id, crewXp, capiXp, date);
    }
    
    
    
    private static int findAttribute1(Scanner s, String skip) throws ParseException {
        s.skip(Pattern.compile(".*?" + skip, Pattern.DOTALL)); //$NON-NLS-1$
        if (!s.hasNextInt()) {
            throw new ParseException(MSG.bind(MSG.qreportParserFailedToParseAttr, skip));
        }
        return s.nextInt();
    }
    
    
    
    private static int findAttribute2(Scanner s) {
        s.skip(" / "); //$NON-NLS-1$
        s.useDelimiter("\\D+"); //$NON-NLS-1$
        return s.nextInt();
    }
    
    
    
    private static int findDmg(Scanner s) {
        final String dmg = s.findInLine("\\(-\\d+\\)"); //$NON-NLS-1$
        if (dmg != null) {
            return Integer.parseInt(dmg.replaceAll("[()-]", "")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return 0;
    }
}
