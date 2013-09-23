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

import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.sdk.time.Time;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.BattleTactic;
import polly.rx.entities.RxRessource;


public class QBattleReportParser {
    
    // TEST
    public static void main(String[] args) throws IOException, ParseException {
        final String fileName =  "liveKB.txt";
        try (InputStream is = QBattleReportParser.class.getResourceAsStream(fileName)) {
            final BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            final StringBuilder b = new StringBuilder();
            String line = null;
            while ((line = r.readLine()) != null) {
                b.append(line);
                b.append("\n");
            }
            
            parse(b.toString(), 0);
        }
    }
    
    
    private final static DateFormat getDateFormat() {
        return new SimpleDateFormat("HH:mm dd-MM-yyyy");
    }
    
    
    public static BattleReport parse(String report, int submitterId) throws ParseException {
        System.out.println(report);
        
        try (Scanner s = new Scanner(report)) {
            final Pattern delimiter = s.delimiter();
            
            s.useLocale(Locale.ENGLISH);
            
            // date
            final DateFormat df = getDateFormat();
            boolean noDate = false;
            Date date;
            try {
                date = df.parse(s.nextLine().trim());
                // round to minutes
                long d = date.getTime() / Milliseconds.fromMinutes(1) * Milliseconds.fromMinutes(1);
                date = new Date(d);
            } catch (java.text.ParseException e) {
                // ignore, use system date and go on
                date = Time.currentTime();
                noDate = true;
            }
            
            s.skip("\\D*");
            
            // drop
            final List<BattleDrop> drop = new ArrayList<>(RxRessource.values().length);
            for (int i = 0; i < RxRessource.values().length; ++i) {
                final RxRessource res = RxRessource.byOrdinal(i);
                if (!s.hasNextInt()) {
                    throw new ParseException("resource expected: " + res);
                }
                final int amount = s.nextInt();
                drop.add(new BattleDrop(res, amount));
            }
            s.nextLine();
            final boolean artifact = s.hasNextInt() && s.nextInt() == 1;
            if (artifact) {
                s.nextLine();
            }
            
            if (!s.next().equals("Gefecht") || !s.next().equals("bei")) {
                throw new ParseException("'Gefecht bei' expected");
            }
            
            String quadrant = s.findInLine("\\D+");
            if (quadrant == null) {
                throw new ParseException("Error while parsing quadrant name");
            }
            quadrant = quadrant.trim();
            s.useDelimiter(",|\\s+");
            final int x = s.nextInt();
            final int y = s.nextInt();
            
            s.useDelimiter(delimiter);
            while (s.findInLine("Gefechtstaktik") == null) {
                s.nextLine();
            }
            final BattleTactic tactic = BattleTactic.parseTactic(s.nextLine().trim());
            s.skip("\\s+Bonus Angreifer");
            s.useDelimiter("%");
            final double attackerBonus = 1.0 + (s.nextInt() / 100.0);
            s.skip("%\\s+Bonus Verteidiger");
            final double defenderBonus = 1.0 + (s.nextInt() / 100.0);
            s.skip("%\\s+Kampfwert Angreifer/XP-Mod\\s*");
            s.useDelimiter("/|\\s+");
            final double kwAttacker = s.nextDouble();
            final double xpModAttacker = s.nextDouble();
            s.skip("\\s+Kampfwert Verteidiger/XP-Mod");
            final double kwDefender = s.nextDouble();
            final double xpModDefender = s.nextDouble();
            
            
            // attacker fleet:
            s.skip("\\s+Angreifer Flotte: ");
            s.useDelimiter(" \\(");
            final String attackerFleetName = s.next();
            s.useDelimiter(" \\(|\\)");
            
            String attackerName = s.next();
            s.skip("\\)\\s*");
            String attackerClan = "";
            int i = attackerName.indexOf("[");
            if (i != -1) {
                attackerClan = attackerName.substring(i + 1, attackerName.length() - 1);
                attackerName = attackerName.substring(0, attackerName.length() - attackerClan.length() - 2);
            }
            
            final List<BattleReportShip> attackerShips = new ArrayList<>(50);
            while (s.findInLine("Verteidiger Flotte: ") == null) {
                s.useDelimiter(delimiter);
                final BattleReportShip ship = findShip(s);
                attackerShips.add(ship);
                System.out.println(ship);
            }

            
            
            // Defender fleet
            s.useDelimiter(" \\(");
            final String defenderFleetName = s.next();
            s.useDelimiter(" \\(|\\)");
            
            String defenderName = s.next();
            s.skip("\\)\\s*");
            String defenderClan = "";
            i = defenderName.indexOf("[");
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
                System.out.println(ship);
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
            br.setNoDate(noDate);
            return br;
        }
    }
    
    
    
    @SuppressWarnings("unused")
    private static BattleReportShip findShip(Scanner s) throws ParseException {
        s.skip("\\s*");
        final String shipName = s.nextLine(); 
        final String capiName = s.nextLine();
        
        // find aw
        final int currentAw = findAttribute1(s, "Angriffswert");
        final int aw = findAttribute2(s);
        final int awDmg = findDmg(s);
        
        // capi hp/xp
        final int currentHp = findAttribute1(s, "Captain");
        final int hp = findAttribute2(s);
        final int hpDmg = findDmg(s);
        final int capiXp = findAttribute1(s, "XPs");
        
        // shields
        final int currentShield = findAttribute1(s, "Schild");
        final int shield = findAttribute2(s);
        final int shieldDmg = findDmg(s);
        
        // crew
        final int currentCrew = findAttribute1(s, "Crew");
        final int crew = findAttribute2(s);
        final int crewDmg = findDmg(s);
        final int crewXp = findAttribute1(s, "XPs");
        
        // pz
        final int currentPz = findAttribute1(s, "Panzerung");
        final int pz = findAttribute2(s);
        final int pzDmg = findDmg(s);
        
        // systems
        final int currentSystem = findAttribute1(s, "Systeme");
        final int system = findAttribute2(s);
        final int systemDmg = findDmg(s);
        
        // structure
        final int currentStruct = findAttribute1(s, "Struktur");
        final int struct = findAttribute2(s);
        final int structDmg = findDmg(s);
        
        int id = 0;
        if (s.findInLine("ID:") != null) {
            s.useDelimiter("\\D+");
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
        s.skip(Pattern.compile(".*?" + skip, Pattern.DOTALL));
        if (!s.hasNextInt()) {
            throw new ParseException("Failed to parse attribute: " + skip);
        }
        return s.nextInt();
    }
    
    
    
    private static int findAttribute2(Scanner s) {
        s.skip(" / ");
        s.useDelimiter("\\D+");
        return s.nextInt();
    }
    
    
    
    private static int findDmg(Scanner s) {
        final String dmg = s.findInLine("\\(-\\d+\\)");
        if (dmg != null) {
            return Integer.parseInt(dmg.replaceAll("[()-]", ""));
        }
        return 0;
    }
}
