package polly.rx.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

import polly.rx.MSG;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.BattleTactic;
import polly.rx.entities.RxRessource;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.sdk.time.Time;


public class QBattleReportParser {
    
    // TEST
    public static void main(String[] args) throws IOException, ParseException {
        final String fileName =  "liveKB.txt"; //$NON-NLS-1$
        try (InputStream is = QBattleReportParser.class.getResourceAsStream(fileName)) {
            final BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8")); //$NON-NLS-1$
            final StringBuilder b = new StringBuilder();
            String line = null;
            while ((line = r.readLine()) != null) {
                b.append(line);
                b.append("\n"); //$NON-NLS-1$
            }
            
            parse(b.toString(), 0);
        }
    }
    
    
    private final static DateFormat getDateFormat() {
        return new SimpleDateFormat("HH:mm dd-MM-yyyy"); //$NON-NLS-1$
    }
    
    
    public static BattleReport parse(String report, int submitterId) throws ParseException {
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
            final List<BattleDrop> drop = new ArrayList<>(RxRessource.values().length);
            for (int i = 0; i < RxRessource.values().length; ++i) {
                final RxRessource res = RxRessource.values()[i];
                if (!s.hasNextInt()) {
                    throw new ParseException(
                            MSG.bind(MSG.qreportParserResourceExpected, res));
                }
                final int amount = s.nextInt();
                drop.add(new BattleDrop(res, amount));
            }
            s.nextLine();
            final boolean artifact = s.hasNextInt() && s.nextInt() == 1;
            if (artifact) {
                s.nextLine();
            }
            
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
            final double attackerBonus = 1.0 + (s.nextInt() / 100.0);
            s.skip("%\\s+Bonus Verteidiger"); //$NON-NLS-1$
            final double defenderBonus = 1.0 + (s.nextInt() / 100.0);
            s.skip("%\\s+Kampfwert Angreifer/XP-Mod\\s*"); //$NON-NLS-1$
            s.useDelimiter("/|\\s+"); //$NON-NLS-1$
            final double kwAttacker = s.nextDouble();
            final double xpModAttacker = s.nextDouble();
            s.skip("\\s+Kampfwert Verteidiger/XP-Mod"); //$NON-NLS-1$
            final double kwDefender = s.nextDouble();
            final double xpModDefender = s.nextDouble();
            
            
            // attacker fleet:
            s.skip("\\s+Angreifer Flotte: "); //$NON-NLS-1$
            s.useDelimiter(" \\("); //$NON-NLS-1$
            final String attackerFleetName = s.next();
            s.useDelimiter(" \\(|\\)"); //$NON-NLS-1$
            
            String attackerName = s.next();
            s.skip("\\)\\s*"); //$NON-NLS-1$
            String attackerClan = ""; //$NON-NLS-1$
            int i = attackerName.indexOf("["); //$NON-NLS-1$
            if (i != -1) {
                attackerClan = attackerName.substring(i + 1, attackerName.length() - 1);
                attackerName = attackerName.substring(0, attackerName.length() - attackerClan.length() - 2);
            }
            
            final List<BattleReportShip> attackerShips = new ArrayList<>(50);
            while (s.findInLine("Verteidiger Flotte: ") == null) { //$NON-NLS-1$
                s.useDelimiter(delimiter);
                final BattleReportShip ship = findShip(s);
                attackerShips.add(ship);
            }

            
            
            // Defender fleet
            s.useDelimiter(" \\("); //$NON-NLS-1$
            final String defenderFleetName = s.next();
            s.useDelimiter(" \\(|\\)"); //$NON-NLS-1$
            
            String defenderName = s.next();
            s.skip("\\)\\s*"); //$NON-NLS-1$
            String defenderClan = ""; //$NON-NLS-1$
            i = defenderName.indexOf("["); //$NON-NLS-1$
            if (i != -1) {
                defenderClan = defenderName.substring(i + 1, defenderName.length() - 1);
                defenderName = defenderName.substring(0, defenderName.length() - defenderClan.length() - 2);
            }
            
            final List<BattleReportShip> defenderShips = new ArrayList<>(50);
            s.useDelimiter(delimiter);
            
            while (s.hasNext()) {
                s.useDelimiter(delimiter);
                final BattleReportShip ship = findShip(s);
                defenderShips.add(ship);
            }
            final BattleReport br = new BattleReport(
                submitterId, 
                quadrant, 
                x, 
                y, 
                drop, 
                artifact, 
                date, 
                tactic, 
                attackerBonus, 
                defenderBonus, 
                kwAttacker, 
                xpModAttacker, 
                kwDefender, 
                xpModDefender, 
                attackerFleetName, 
                attackerName, 
                defenderFleetName, 
                defenderName, 
                attackerClan, 
                defenderClan, 
                attackerShips, 
                defenderShips);
            return br;
        }
    }
    
    
    
    @SuppressWarnings("unused")
    private static BattleReportShip findShip(Scanner s) throws ParseException {
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
        
        return new BattleReportShip(id, 
            shipName, 
            capiName, 
            aw, 
            shield, 
            pz, 
            struct, 
            currentCrew, 
            crew, system, 
            capiXp, 
            crewXp, 
            shieldDmg, 
            currentPz, 
            pzDmg, 
            structDmg, 
            systemDmg, 
            hp, 
            hpDmg, 
            awDmg, 
            crewDmg);
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
