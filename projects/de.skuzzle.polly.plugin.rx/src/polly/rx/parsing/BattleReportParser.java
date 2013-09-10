package polly.rx.parsing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.tools.iterators.ArrayIterator;

import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.BattleTactic;
import polly.rx.entities.RxRessource;


public class BattleReportParser {
    
    private final static Pattern NUBER_PATTERN = Pattern.compile("\\d+"); 
    
    final static Pattern WHERE_PATTERN = Pattern.compile(
        "Gefecht bei (.*) (\\d+),(\\d+)");
    final static int QUADRANT_GROUP = 1;
    final static int X_GROUP = 2;
    final static int Y_GROUP = 3;
    
    
    private final static Pattern HEADER_PATTERN = Pattern.compile(
        "Die (Angreifer|Verteidiger) waren siegreich\\s+Gefechtstaktik\\s+([^\n]+)\n" +
        "\\s*Bonus Angreifer\\s+(-?\\d+)%\\s+Bonus Verteidiger\\s+(-?\\d+)%\n\\s*" +
        "Kampfwert Angreifer/XP-Mod\\s+(\\d+(?:\\.\\d+)?)/(\\d+(\\.\\d+)?)\\s+" +
        "Kampfwert Verteidiger/XP-Mod\\s+(\\d+(?:\\.\\d+)?)/(\\d+(\\.\\d+)?)", 
        Pattern.DOTALL);
    private final static int TACTIC_GROUP = 2;
    private final static int ATTACKER_BONUS_GROUP = 3;
    private final static int DEFENDER_BONUS_GROUP = 4;
    private final static int ATTACKER_KW_GROUP = 5;
    private final static int ATTACKER_XPMOD_GROUP = 6;
    private final static int DEFENDER_KW_GROUP = 8;
    private final static int DEFENDER_XPMOD_GROUP = 9;
    
    
    private final static Pattern FLEET_NAME_PATTERN = Pattern.compile("(Angreifer|Verteidiger) Flotte: ([^\\)]+)\\(([^\\)]+)\\)");
    private final static int FLEET_NAME_GROUP = 2;
    private final static int VENAD_NAME_GROUP = 3;
    
    
    private final static Pattern SHIP_PATTERN = Pattern.compile("(.*?)\\s{2,}(.*?)\\s+" + 
        "Angriffswert\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+Captain\\s+(-?\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+XPs\\s+(\\d+)\\s+" + 
        "Schild\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+Crew\\s+(-?\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+XPs\\s+(\\d+)\\s+" +
        "Panzerung\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+Systeme\\s+(-?\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+" + 
        "Struktur\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s*(?:ID:(\\d+)\\s+)?");
    private final static int SHIP_NAME_GROUP = 1;
    private final static int CAPI_NAME_GROUP = 2;
    private final static int AW_GROUP = 4;
    private final static int AW_DMG_GROUP = 6;
    private final static int HP_GROUP = 8;
    private final static int HP_DMG_GROUP = 10;
    private final static int XP_CAPI_GROUP = 11;
    private final static int SHIELDS_GROUP = 13;
    private final static int SHIELDS_DMG_GROUP = 15;
    private final static int MIN_CREW_GROUP = 16;
    private final static int MAX_CREW_GROUP = 17;
    private final static int CREW_DMG_GROUP = 19;
    private final static int XP_CREW_GROUP = 20;
    private final static int CURRENT_PZ_GROUP = 21;
    private final static int PZ_GROUP = 22;
    private final static int PZ_DMG_GROUP = 24;
    private final static int SYSTEMS_GROUP = 26;
    private final static int SYSTEMS_DMG_GROUP = 28;
    private final static int STRUCTURE_GROUP = 30;
    private final static int STRUCTURE_DMG_GROUP = 32;
    private final static int ID_GROUP = 33;
    
    
    private final static DateFormat getDateFormat() {
        return new SimpleDateFormat("HH:mm dd-MM-yyyy");
    }
    
    
    
    private BattleReportParser() {}
    
    
    
    public final static BattleReport parseReport(String paste, User submitter) throws ParseException {
        try {
            return parseReportHelper(paste, submitter);
        } catch (Exception e) {
            throw new ParseException("ungültiger Kampfbericht");
        }
    }
    
    
    
    private static BattleReport parseReportHelper(String paste, User submitter) throws ParseException {
        String[] lines = paste.split("[\n\r]+");
        
        ArrayIterator<String> it = ArrayIterator.get(lines);
        // KB date
        Date date = null;
        try {
            date = getDateFormat().parse(it.next());
        } catch (Exception e) {
            throw new ParseException("invalid date: " + it.previous());
        }
        
        // KB drop
        it.next();
        Matcher numbers = NUBER_PATTERN.matcher(it.next());
        List<BattleDrop> drops = new ArrayList<BattleDrop>(14);
        int i = 0;
        while (numbers.find()) {
            int amount = Integer.parseInt(numbers.group());
            RxRessource r = RxRessource.byOrdinal(i++);
            drops.add(new BattleDrop(r, amount));
        }
        
        // kb artifact
        boolean artifact = it.peekNext().startsWith("1 Artefakt");
        
        // KB location
        while (!it.peekNext().startsWith("Gefecht bei ")) {
            it.next();
        }
        String tmp = it.next();
        Matcher where = WHERE_PATTERN.matcher(tmp);
        if (!where.find()) {
            throw new ParseException("Invalid Quadrant specification: " + tmp);
        }
        
        String quadrant = RegexUtils.substr(tmp, where, QUADRANT_GROUP);
        int x = RegexUtils.subint(tmp, where, X_GROUP);
        int y = RegexUtils.subint(tmp, where, Y_GROUP);
        
        // KB tactic
        StringBuilder kbheader = new StringBuilder();
        while (it.hasNext() && !it.peekNext().startsWith("Angreifer")) {
            kbheader.append(it.next());
            kbheader.append('\n');
        }
        
        tmp = kbheader.toString();
        Matcher header = HEADER_PATTERN.matcher(tmp);
        if (!header.find()) {
            throw new ParseException("Invalid header");
        }
        BattleTactic tactic = BattleTactic.parseTactic(
            RegexUtils.substr(tmp, header, TACTIC_GROUP));
        double attackerBonus = 
            parseBonus(RegexUtils.substr(tmp, header, ATTACKER_BONUS_GROUP));
        double defenderBonus = 
            parseBonus(RegexUtils.substr(tmp, header, DEFENDER_BONUS_GROUP));
        double attackerKw = Double.parseDouble(RegexUtils.substr(tmp, header, ATTACKER_KW_GROUP));
        double attackerXpMod = Double.parseDouble(
            RegexUtils.substr(tmp, header, ATTACKER_XPMOD_GROUP));
        double defenderkw = Double.parseDouble(RegexUtils.substr(tmp, header, DEFENDER_KW_GROUP));
        double defenderXpMod = Double.parseDouble(
            RegexUtils.substr(tmp, header, DEFENDER_XPMOD_GROUP));
        
        // KB attacker fleet
        tmp = it.next();
        Matcher fleet = FLEET_NAME_PATTERN.matcher(tmp);
        if (!fleet.find()) {
            throw new ParseException("Expected attacker fleet name: " + tmp);
        }
        
        String attackerVenad = RegexUtils.substr(tmp, fleet, VENAD_NAME_GROUP);
        String attackerClan = "";
        String attackerFleetName = RegexUtils.substr(tmp, fleet, FLEET_NAME_GROUP);
        i = attackerVenad.indexOf("[");
        if (i != -1) {
            attackerClan = attackerVenad.substring(i + 1, attackerVenad.length() - 1);
            attackerVenad = attackerVenad.substring(0, attackerVenad.length() - attackerClan.length() - 2);
        }
        
        StringBuilder b = new StringBuilder();
        while (it.hasNext() && !it.peekNext().startsWith("Verteidiger Flotte")) {
            b.append(it.next());
            b.append('\n');
        }
        
        // KB defender fleet
        tmp = it.next();
        fleet = FLEET_NAME_PATTERN.matcher(tmp);
        if (!fleet.find()) {
            throw new ParseException("Expected defender fleet name:" + tmp);
        }
        String defenderVenad = RegexUtils.substr(tmp, fleet, VENAD_NAME_GROUP);
        String defenderFleetName = RegexUtils.substr(tmp, fleet, FLEET_NAME_GROUP);
        String defenderClan = "";
        i = defenderVenad.indexOf("[");
        if (i != -1) {
            defenderClan = defenderVenad.substring(i + 1, defenderVenad.length() - 1);
            defenderVenad = defenderVenad.substring(0, defenderVenad.length() - defenderClan.length() - 2);
        }
        List<BattleReportShip> attackerShips = parseShips(b.toString());
        
        if (attackerShips.isEmpty()) {
            throw new ParseException("No attacker ships");
        }
        
        b = new StringBuilder();
        while (it.hasNext()) {
            b.append(it.next());
            b.append('\n');
        }
        List<BattleReportShip> defenderShips = parseShips(b.toString());
        
        if (defenderShips.isEmpty()) {
            throw new ParseException("No defender ships");
        }
        
        return new BattleReport(submitter.getId(), quadrant, x, y, drops, artifact, date, 
            tactic, attackerBonus, defenderBonus, attackerKw, attackerXpMod, defenderkw, 
            defenderXpMod, attackerFleetName, attackerVenad, defenderFleetName, 
            defenderVenad, attackerClan, defenderClan, attackerShips, defenderShips);
    }
    
    
    
    public static double parseBonus(String bonus) {
        int b = Integer.parseInt(bonus);
        return 1.0 + ((double)b / 100.0);
    }
    
    
    
    private static List<BattleReportShip> parseShips(String paste) {
        Matcher ships = SHIP_PATTERN.matcher(paste);
        List<BattleReportShip> result = new LinkedList<BattleReportShip>();
        
        while (ships.find()) {
            String shipName = RegexUtils.substr(paste, ships, SHIP_NAME_GROUP);
            String capiName = RegexUtils.substr(paste, ships, CAPI_NAME_GROUP);
            int aw = RegexUtils.subint(paste, ships, AW_GROUP);
            int awDmg = RegexUtils.subint(paste, ships, AW_DMG_GROUP);
            int hp = RegexUtils.subint(paste, ships, HP_GROUP);
            int hpDmg = RegexUtils.subint(paste, ships, HP_DMG_GROUP);
            int shields = RegexUtils.subint(paste, ships, SHIELDS_GROUP);
            int shieldsDmg = RegexUtils.subint(paste, ships, SHIELDS_DMG_GROUP);
            int minCrew = RegexUtils.subint(paste, ships, MIN_CREW_GROUP);
            int maxCrew = RegexUtils.subint(paste, ships, MAX_CREW_GROUP);
            int crewDmg = RegexUtils.subint(paste, ships, CREW_DMG_GROUP);
            int xpCapi = RegexUtils.subint(paste, ships, XP_CAPI_GROUP);
            int xpCrew = RegexUtils.subint(paste, ships, XP_CREW_GROUP);
            int pz = RegexUtils.subint(paste, ships, PZ_GROUP);
            int currentPz = RegexUtils.subint(paste, ships, CURRENT_PZ_GROUP);
            int pzDmg = RegexUtils.subint(paste, ships, PZ_DMG_GROUP);
            int systems = RegexUtils.subint(paste, ships, SYSTEMS_GROUP);
            int systemsDmg = RegexUtils.subint(paste, ships, SYSTEMS_DMG_GROUP);
            int structure = RegexUtils.subint(paste, ships, STRUCTURE_GROUP);
            int structureDmg = RegexUtils.subint(paste, ships, STRUCTURE_DMG_GROUP);
            int rxId = RegexUtils.subint(paste, ships, ID_GROUP);
            
            BattleReportShip ship = new BattleReportShip(rxId, shipName, capiName, aw, 
                shields, pz, structure, minCrew, maxCrew, systems, xpCapi, xpCrew, 
                shieldsDmg, currentPz, pzDmg, structureDmg, systemsDmg, hp, hpDmg, awDmg, crewDmg);
            System.out.println(ship);
            result.add(ship);
        }
        
        return result;
    }
}
