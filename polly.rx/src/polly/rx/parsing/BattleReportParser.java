package polly.rx.parsing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import polly.rx.ParseException;

import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.BattleTactic;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.RxRessource;


public class BattleReportParser {
    
    
    public static void main(String[] args) throws ParseException {
        String paste = " 14:44 16-08-2012\n" + 
        		"Zurückgelassene Ressourcen\n" + 
        		"0   0   0   4614    443     145     696     694     481     158     73  19  0   0\n" + 
        		"1 Artefakt(e) erbeutet\n" + 
        		"\n" + 
        		"Gefecht bei Neu Kaledonien 8,12\n" + 
        		"Die Angreifer waren siegreich   Gefechtstaktik  Raubzug\n" + 
        		"Bonus Angreifer 32%     Bonus Verteidiger   -19%\n" + 
        		"Kampfwert Angreifer/XP-Mod  98217.52/0.26   Kampfwert Verteidiger/XP-Mod    45978.82/1\n" + 
        		"\n" + 
        		"Angreifer Flotte: RiskyChaser (C0mb4t:Inc[Loki])\n" + 
        		"Begleitschiff (LIII) Herxosi    Fleet Marshall Jan Martin Jacobs\n" + 
        		"Angriffswert    194 / 194   Captain 15 / 15 XPs 14\n" + 
        		"Schild  1569 / 2070(-501)   Crew    137 / 137   XPs 7\n" + 
        		"Panzerung   12178 / 13680(-196)     Systeme 3938 / 3938\n" + 
        		"Struktur    494 / 494   ID:11107040 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Lentra     Galaxy Marshall Meike Ehler\n" + 
        		"Angriffswert    195 / 195   Captain 15 / 15 XPs 15\n" + 
        		"Schild  1378 / 2006(-628)   Crew    137 / 137   XPs 7\n" + 
        		"Panzerung   12480 / 13675(-214)     Systeme 3841 / 3841\n" + 
        		"Struktur    519 / 519   ID:11142075 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Nemsek     Recruit Bodo Greene\n" + 
        		"Angriffswert    191 / 191   Captain 10 / 10 XPs 9\n" + 
        		"Schild  1316 / 2061(-745)   Crew    137 / 137   XPs 4\n" + 
        		"Panzerung   11339 / 13712(-54)  Systeme 3883 / 3883\n" + 
        		"Struktur    517 / 517   ID:11116300 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Othrax     Fleet Admiral Gaynor Lanlermann\n" + 
        		"Angriffswert    191 / 191   Captain 21 / 21 XPs 11\n" + 
        		"Schild  1543 / 1997(-454)   Crew    137 / 137   XPs 5\n" + 
        		"Panzerung   11951 / 13522(-113)     Systeme 3788 / 3788\n" + 
        		"Struktur    497 / 497   ID:11212243 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Sikerier   Star Marshall Gaby Kalckberner\n" + 
        		"Angriffswert    198 / 198   Captain 15 / 15 XPs 18\n" + 
        		"Schild  1499 / 2038(-539)   Crew    137 / 137   XPs 9\n" + 
        		"Panzerung   11683 / 13887(-287)     Systeme 3905 / 3905\n" + 
        		"Struktur    506 / 506   ID:11161983 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Zertrax    Star Marshall Lidie Zapata\n" + 
        		"Angriffswert    196 / 196   Captain 21 / 21 XPs 12\n" + 
        		"Schild  1534 / 2038(-504)   Crew    137 / 137   XPs 6\n" + 
        		"Panzerung   12199 / 13800(-133)     Systeme 3873 / 3873\n" + 
        		"Struktur    517 / 517   ID:11142076 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Chantaus     Venad Sub Commander Shalyna Seehaus\n" + 
        		"Angriffswert    169 / 169   Captain 15 / 15 XPs 15\n" + 
        		"Schild  961 / 1440(-479)    Crew    30 / 32 XPs 7\n" + 
        		"Panzerung   6093 / 8243(-215)   Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10385501 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Funga    Star Marshall Rieke Roughley\n" + 
        		"Angriffswert    169 / 169   Captain 15 / 15 XPs 13\n" + 
        		"Schild  768 / 1440(-672)    Crew    32 / 32 XPs 7\n" + 
        		"Panzerung   6488 / 8243(-177)   Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10296537 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Netarus  Sub Marshall Tobi Piaget\n" + 
        		"Angriffswert    166 / 166   Captain 21 / 21 XPs 8\n" + 
        		"Schild  807 / 1418(-611)    Crew    32 / 32 XPs 4\n" + 
        		"Panzerung   6410 / 8119(-44)    Systeme 2843 / 2843\n" + 
        		"Struktur    152 / 152   ID:10778297 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Novalis  Group Marshall Zina Ahlers\n" + 
        		"Angriffswert    169 / 169   Captain 15 / 15 XPs 11\n" + 
        		"Schild  688 / 1440(-752)    Crew    30 / 32 XPs 5\n" + 
        		"Panzerung   6477 / 8243(-111)   Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10384757 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Quasolas     Fleet Marshall Emmi Bamberg\n" + 
        		"Angriffswert    169 / 169   Captain 21 / 21 XPs 19\n" + 
        		"Schild  936 / 1440(-504)    Crew    30 / 32 XPs 9\n" + 
        		"Panzerung   6703 / 8243(-317)   Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10320935 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Tridona  Fleet Marshall Shanice Nienhaus\n" + 
        		"Angriffswert    169 / 169   Captain 16 / 16 XPs 15\n" + 
        		"Schild  897 / 1440(-543)    Crew    30 / 32 XPs 8\n" + 
        		"Panzerung   6166 / 8243(-223)   Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10341934 \n" + 
        		"\n" + 
        		"Kommandoschiff (XLII) Xontrapit     Vice Venad Commander Tasso Hilyard\n" + 
        		"Angriffswert    58 / 58     Captain 20 / 20 XPs 12\n" + 
        		"Schild  737 / 1453(-716)    Crew    118 / 118   XPs 6\n" + 
        		"Panzerung   4484 / 6847(-140)   Systeme 3421 / 3421\n" + 
        		"Struktur    260 / 260   ID:8643857  \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Degerox    Galaxy Marshall Basilius Heegmann\n" + 
        		"Angriffswert    8826 / 8826     Captain 16 / 16 XPs 10\n" + 
        		"Schild  1457 / 1928(-471)   Crew    191 / 246   XPs 5\n" + 
        		"Panzerung   5834 / 7621(-98)    Systeme 3337 / 3337\n" + 
        		"Struktur    169 / 169   ID:12479998 \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Moluga     Vice Venad Commander Cilia Albert\n" + 
        		"Angriffswert    9321 / 9321     Captain 20 / 20 XPs 10\n" + 
        		"Schild  1338 / 2035(-697)   Crew    191 / 246   XPs 5\n" + 
        		"Panzerung   6299 / 8048(-94)    Systeme 3523 / 3523\n" + 
        		"Struktur    178 / 178   ID:10162025 \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Nesono     Vice Venad Commander Hedda Troost\n" + 
        		"Angriffswert    9321 / 9321     Captain 17 / 17 XPs 16\n" + 
        		"Schild  1451 / 2035(-584)   Crew    191 / 246   XPs 8\n" + 
        		"Panzerung   5968 / 8048(-251)   Systeme 3523 / 3523\n" + 
        		"Struktur    178 / 178   ID:10126012 \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Riwe   Vice Venad Commander Marko Kniess\n" + 
        		"Angriffswert    9321 / 9321     Captain 20 / 20 XPs 9\n" + 
        		"Schild  1387 / 2035(-648)   Crew    191 / 246   XPs 5\n" + 
        		"Panzerung   7099 / 8048(-74)    Systeme 3523 / 3523\n" + 
        		"Struktur    178 / 178   ID:10178200 \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Xemsial    Vice Venad Commander Jerry Jakobson\n" + 
        		"Angriffswert    9321 / 9321     Captain 20 / 20 XPs 9\n" + 
        		"Schild  1372 / 2035(-663)   Crew    191 / 246   XPs 4\n" + 
        		"Panzerung   5834 / 8048(-61)    Systeme 3523 / 3523\n" + 
        		"Struktur    178 / 178   ID:10177075 \n" + 
        		"\n" + 
        		"\n" + 
        		"Verteidiger Flotte: Reg-Nr. 116 (Dharr-Grosch)\n" + 
        		"Kereliem    1st Lieutenant Captain\n" + 
        		"Angriffswert    16200 / 16200   Captain 15 / 15     XPs 380\n" + 
        		"Schild  8869 / 15000(-6131)     Crew    170 / 170   XPs 190\n" + 
        		"Panzerung   369 / 2200(-1831)   Systeme 16949 / 16949\n" + 
        		"Struktur    600 / 600   \n" + 
        		"\n" + 
        		"Kereliem    Lance Corporal Captain\n" + 
        		"Angriffswert    16200 / 16200   Captain 27 / 27     XPs 332\n" + 
        		"Schild  8551 / 15000(-6449)     Crew    170 / 170   XPs 166\n" + 
        		"Panzerung   687 / 2200(-1513)   Systeme 16949 / 16949\n" + 
        		"Struktur    600 / 600   \n" + 
        		"\n" + 
        		"Sikrelias   Lieutenant Junior Grade Captain\n" + 
        		"Angriffswert    16700 / 16700   Captain 12 / 12     XPs 201\n" + 
        		"Schild  9329 / 14000(-4671)     Crew    165 / 165   XPs 100\n" + 
        		"Panzerung   2063 / 2700(-637)   Systeme 15615 / 15615\n" + 
        		"Struktur    480 / 480   \n" + 
        		"\n" + 
        		"";
    
        BattleReport rep = BattleReportParser.parse(paste);
    }
    

    private final static Pattern DROP_PATTERN = Pattern.compile(
        "Zurückgelassene Ressourcen\\s+((\\d+\\s*?)+)(1 Artefakt)?");
    private final static int DROP_GROUP = 1;
    
    private final static Pattern WHERE_PATTERN = Pattern.compile(
        "Gefecht bei (.*) (\\d+),(\\d+)");
    private final static int QUADRANT_GROUP = 1;
    private final static int X_GROUP = 2;
    private final static int Y_GROUP = 3;
    
    private final static Pattern HEADER_PATTERN = Pattern.compile(
        "Die (Angreifer|Verteidiger) waren siegreich\\s+Gefechtstaktik\\s+([a-zA-Z ]+).*Bonus Angreifer\\s+(-?\\d+)%\\s+Bonus Verteidiger\\s+(-?\\d+)%.*Kampfwert Angreifer/XP-Mod\\s+(\\d+\\.\\d+)/(\\d+(\\.\\d+)?)\\s+Kampfwert Verteidiger/XP-Mod\\s+(\\d+\\.\\d+)/(\\d+(\\.\\d+)?)", Pattern.DOTALL);
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
        "Angriffswert\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+Captain\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+XPs\\s+(\\d+)\\s+" + 
        "Schild\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+Crew\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+XPs\\s+(\\d+)\\s+" +
        "Panzerung\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+Systeme\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+" + 
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
    private final static int PZ_GROUP = 22;
    private final static int PZ_DMG_GROUP = 24;
    private final static int SYSTEMS_GROUP = 26;
    private final static int SYSTEMS_DMG_GROUP = 28;
    private final static int STRUCTURE_GROUP = 30;
    private final static int STRUCTURE_DMG_GROUP = 32;
    private final static int ID_GROUP = 33;
    
    
    private final static DateFormat DATE_FORMAT;
    static {
        DATE_FORMAT = new SimpleDateFormat("HH:mm dd-MM-yyyy");
    }
    
    
    public static BattleReport parse(String paste) throws ParseException {
        Date date = null;
        try {
            date = DATE_FORMAT.parse(paste.trim());
        } catch (java.text.ParseException e) {
            parseException(e);
        }
        
        // Parse Ress drop
        Matcher drop = DROP_PATTERN.matcher(paste);
        if (!drop.find()) {
            parseException();
        }
        List<BattleDrop> battleDrops = new ArrayList<BattleDrop>(14);
        String dropString = substr(paste, drop, DROP_GROUP);
        System.out.println(dropString);
        String[] parts = dropString.split("\\s+");
        
        int i = 0;
        for (String part : parts) {
            int amount = Integer.parseInt(part);
            RxRessource ress = RxRessource.byOrdinal(i++);
            battleDrops.add(new BattleDrop(ress, amount));
        }
        
        // Parse Battle location
        Matcher where = WHERE_PATTERN.matcher(paste);
        if (!where.find()) {
            parseException();
        }
        
        String quadrant = substr(paste, where, QUADRANT_GROUP);
        int x = subint(paste, where, X_GROUP);
        int y = subint(paste, where, Y_GROUP);
        
        Matcher header = HEADER_PATTERN.matcher(paste);
        if (!header.find()) {
            parseException();
        }
        BattleTactic tactic = BattleTactic.parseTactic(
            substr(paste, header, TACTIC_GROUP));
        double attackerBonus = parseBonus(substr(paste, header, ATTACKER_BONUS_GROUP));
        double defenderBonus = parseBonus(substr(paste, header, DEFENDER_BONUS_GROUP));
        double attackerKw = Double.parseDouble(substr(paste, header, ATTACKER_KW_GROUP));
        double attackerXpMod = Double.parseDouble(
            substr(paste, header, ATTACKER_XPMOD_GROUP));
        double defenderkw = Double.parseDouble(substr(paste, header, DEFENDER_KW_GROUP));
        double defenderXpMod = Double.parseDouble(
            substr(paste, header, DEFENDER_XPMOD_GROUP));
        
        String attackerVenad = "";
        String defenderVenad = "";
        String attackerFleetName = "";
        String defenderFleetName = "";
        int defenderPos = 0;
        int attackerPos = 0;
        
        Matcher fleet = FLEET_NAME_PATTERN.matcher(paste);
        while (fleet.find()) {
            if (fleet.group().startsWith("Angreifer")) {
                attackerVenad = substr(paste, fleet, VENAD_NAME_GROUP);
                attackerFleetName = substr(paste, fleet, FLEET_NAME_GROUP);
                attackerPos = fleet.end(VENAD_NAME_GROUP) + 2;
            } else {
                defenderVenad = substr(paste, fleet, VENAD_NAME_GROUP);
                defenderFleetName = substr(paste, fleet, FLEET_NAME_GROUP);
                defenderPos = fleet.end(VENAD_NAME_GROUP) + 2;
            }
        }
        
        List<BattleReportShip> attackerFleet = parseShips(
            substr(paste, attackerPos, defenderPos));
        List<BattleReportShip> defenderFleet = 
            parseShips(substr(paste, defenderPos, paste.length()));
        
        BattleReport result = new BattleReport(quadrant, x, y, battleDrops, date, tactic, 
            attackerBonus, defenderBonus, attackerKw, attackerXpMod, defenderkw, 
            defenderXpMod, attackerFleetName, attackerVenad, defenderFleetName, 
            defenderVenad, attackerFleet, defenderFleet);
        return result;
    }
    
    
    
    private static List<BattleReportShip> parseShips(String paste) {
        Matcher ships = SHIP_PATTERN.matcher(paste);
        List<BattleReportShip> result = new LinkedList<BattleReportShip>();
        
        while (ships.find()) {
            String shipName = substr(paste, ships, SHIP_NAME_GROUP);
            String capiName = substr(paste, ships, CAPI_NAME_GROUP);
            int aw = subint(paste, ships, AW_GROUP);
            int awDmg = subint(paste, ships, AW_DMG_GROUP);
            int hp = subint(paste, ships, HP_GROUP);
            int hpDmg = subint(paste, ships, HP_DMG_GROUP);
            int shields = subint(paste, ships, SHIELDS_GROUP);
            int shieldsDmg = subint(paste, ships, SHIELDS_DMG_GROUP);
            int minCrew = subint(paste, ships, MIN_CREW_GROUP);
            int maxCrew = subint(paste, ships, MAX_CREW_GROUP);
            int crewDmg = subint(paste, ships, CREW_DMG_GROUP);
            int xpCapi = subint(paste, ships, XP_CAPI_GROUP);
            int xpCrew = subint(paste, ships, XP_CREW_GROUP);
            int pz = subint(paste, ships, PZ_GROUP);
            int pzDmg = subint(paste, ships, PZ_DMG_GROUP);
            int systems = subint(paste, ships, SYSTEMS_GROUP);
            int systemsDmg = subint(paste, ships, SYSTEMS_DMG_GROUP);
            int structure = subint(paste, ships, STRUCTURE_GROUP);
            int structureDmg = subint(paste, ships, STRUCTURE_DMG_GROUP);
            int rxId = subint(paste, ships, ID_GROUP);
            
            BattleReportShip ship = new BattleReportShip(rxId, shipName, capiName, aw, 
                shields, pz, structure, minCrew, maxCrew, systems, xpCapi, xpCrew, 
                shieldsDmg, pzDmg, structureDmg, systemsDmg, hp, hpDmg, awDmg, crewDmg);
            System.out.println(ship);
            result.add(ship);
        }
        
        return result;
    }
    
    
    
    private final static int subint(String orig, Matcher m, int groupId) {
        if (m.start(groupId) == -1) {
            return 0;
        }
        return Integer.parseInt(substr(orig, m, groupId));
    }
    
    
    
    private static double parseBonus(String bonus) {
        int b = Integer.parseInt(bonus);
        return 1.0 + ((double)b / 100.0);
    }
    
    
    private final static void parseException() throws ParseException {
        throw new ParseException("ungültiger Kampfbericht");
    }
    
    
    
    private final static void parseException(Throwable cause) throws ParseException {
        throw new ParseException("ungültiger Kampfbericht", cause);
    }
    
    
    
    private final static String substr(String orig, int beginIndex, int endIndex) {
        return new String(orig.substring(beginIndex, endIndex));
    }
    
    
    
    private final static String substr(String orig, Matcher m, int groupId) {
        if (m.start(groupId) == -1) {
            return "";
        }
        return substr(orig, m.start(groupId), m.end(groupId));
    }
}
