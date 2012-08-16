package polly.rx.parsing;

import java.text.DateFormat;
import java.text.ParsePosition;
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
import polly.rx.entities.Drop;
import polly.rx.entities.RxRessource;


public class BattleReportParser {
    
    
    public static void main(String[] args) throws ParseException {
        String paste = " 12:17 16-08-2012\n" + 
        		"Zurückgelassene Ressourcen\n" + 
        		"0   0   0   1990    770     444     1114    788     769     0   35  33  0   0\n" + 
        		"\n" + 
        		"Gefecht bei Neu Kaledonien 9,10\n" + 
        		"Die Angreifer waren siegreich   Gefechtstaktik  Raubzug\n" + 
        		"Bonus Angreifer 4%  Bonus Verteidiger   -3%\n" + 
        		"Kampfwert Angreifer/XP-Mod  79697.65/0.63   Kampfwert Verteidiger/XP-Mod    49892.92/1\n" + 
        		"\n" + 
        		"Angreifer Flotte: AdventurousSquall (C0mb4t:Inc[Loki])\n" + 
        		"Begleitschiff (LIII) Herxosi    Fleet Marshall Jan Martin Jacobs\n" + 
        		"Angriffswert    194 / 194   Captain 15 / 15 XPs 10\n" + 
        		"Schild  1417 / 2070(-653)   Crew    137 / 137   XPs 5\n" + 
        		"Panzerung   12749 / 13680(-17)  Systeme 3938 / 3938\n" + 
        		"Struktur    494 / 494   ID:11107040 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Lentra     Galaxy Marshall Meike Ehler\n" + 
        		"Angriffswert    195 / 195   Captain 15 / 15 XPs 16\n" + 
        		"Schild  1257 / 2006(-749)   Crew    137 / 137   XPs 8\n" + 
        		"Panzerung   12968 / 13675(-77)  Systeme 3841 / 3841\n" + 
        		"Struktur    519 / 519   ID:11142075 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Nemsek     Recruit Bodo Greene\n" + 
        		"Angriffswert    191 / 191   Captain 10 / 10 XPs 32\n" + 
        		"Schild  1355 / 2061(-706)   Crew    137 / 137   XPs 16\n" + 
        		"Panzerung   11953 / 13712(-252)     Systeme 3883 / 3883\n" + 
        		"Struktur    517 / 517   ID:11116300 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Othrax     Fleet Admiral Gaynor Lanlermann\n" + 
        		"Angriffswert    191 / 191   Captain 21 / 21 XPs 25\n" + 
        		"Schild  1452 / 1997(-545)   Crew    137 / 137   XPs 12\n" + 
        		"Panzerung   12153 / 13522(-177)     Systeme 3788 / 3788\n" + 
        		"Struktur    497 / 497   ID:11212243 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Sikerier   Star Marshall Gaby Kalckberner\n" + 
        		"Angriffswert    198 / 198   Captain 15 / 15 XPs 8\n" + 
        		"Schild  1029 / 2038(-1009)  Crew    137 / 137   XPs 4\n" + 
        		"Panzerung   12620 / 13887   Systeme 3905 / 3905\n" + 
        		"Struktur    506 / 506   ID:11161983 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Zertrax    Star Marshall Lidie Zapata\n" + 
        		"Angriffswert    196 / 196   Captain 21 / 21 XPs 28\n" + 
        		"Schild  1449 / 2038(-589)   Crew    137 / 137   XPs 14\n" + 
        		"Panzerung   12700 / 13800(-210)     Systeme 3873 / 3873\n" + 
        		"Struktur    517 / 517   ID:11142076 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Chantaus     Venad Sub Commander Shalyna Seehaus\n" + 
        		"Angriffswert    169 / 169   Captain 15 / 15 XPs 32\n" + 
        		"Schild  986 / 1440(-454)    Crew    30 / 32 XPs 16\n" + 
        		"Panzerung   6756 / 8243(-252)   Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10385501 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Funga    Star Marshall Rieke Roughley\n" + 
        		"Angriffswert    169 / 169   Captain 15 / 15 XPs 25\n" + 
        		"Schild  728 / 1440(-712)    Crew    32 / 32 XPs 13\n" + 
        		"Panzerung   6977 / 8243(-177)   Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10296537 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Netarus  Sub Marshall Tobi Piaget\n" + 
        		"Angriffswert    166 / 166   Captain 21 / 21 XPs 38\n" + 
        		"Schild  872 / 1418(-546)    Crew    32 / 32 XPs 19\n" + 
        		"Panzerung   6839 / 8119(-316)   Systeme 2843 / 2843\n" + 
        		"Struktur    152 / 152   ID:10778297 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Novalis  Group Marshall Zina Ahlers\n" + 
        		"Angriffswert    169 / 169   Captain 15 / 15 XPs 18\n" + 
        		"Schild  812 / 1440(-628)    Crew    30 / 32 XPs 9\n" + 
        		"Panzerung   6872 / 8243(-107)   Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10384757 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Quasolas     Fleet Marshall Emmi Bamberg\n" + 
        		"Angriffswert    169 / 169   Captain 21 / 21 XPs 25\n" + 
        		"Schild  728 / 1440(-712)    Crew    30 / 32 XPs 13\n" + 
        		"Panzerung   7442 / 8243(-177)   Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10320935 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Tridona  Fleet Marshall Shanice Nienhaus\n" + 
        		"Angriffswert    169 / 169   Captain 16 / 16 XPs 8\n" + 
        		"Schild  431 / 1440(-1009)   Crew    30 / 32 XPs 4\n" + 
        		"Panzerung   6920 / 8243     Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10341934 \n" + 
        		"\n" + 
        		"Kommandoschiff (XLII) Xontrapit     Vice Venad Commander Tasso Hilyard\n" + 
        		"Angriffswert    58 / 58     Captain 20 / 20 XPs 33\n" + 
        		"Schild  1089 / 1453(-364)   Crew    118 / 118   XPs 16\n" + 
        		"Panzerung   5114 / 6847(-258)   Systeme 3421 / 3421\n" + 
        		"Struktur    260 / 260   ID:8643857  \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Degerox    Galaxy Marshall Basilius Heegmann\n" + 
        		"Angriffswert    8826 / 8826     Captain 16 / 16 XPs 17\n" + 
        		"Schild  1030 / 1928(-898)   Crew    191 / 246   XPs 9\n" + 
        		"Panzerung   6267 / 7621(-93)    Systeme 3337 / 3337\n" + 
        		"Struktur    169 / 169   ID:12479998 \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Moluga     Vice Venad Commander Cilia Albert\n" + 
        		"Angriffswert    9321 / 9321     Captain 20 / 20 XPs 10\n" + 
        		"Schild  1211 / 2035(-824)   Crew    191 / 246   XPs 5\n" + 
        		"Panzerung   6889 / 8048(-14)    Systeme 3523 / 3523\n" + 
        		"Struktur    178 / 178   ID:10162025 \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Nesono     Vice Venad Commander Hedda Troost\n" + 
        		"Angriffswert    9321 / 9321     Captain 17 / 17 XPs 25\n" + 
        		"Schild  1323 / 2035(-712)   Crew    191 / 246   XPs 13\n" + 
        		"Panzerung   6713 / 8048(-177)   Systeme 3523 / 3523\n" + 
        		"Struktur    178 / 178   ID:10126012 \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Riwe   Vice Venad Commander Marko Kniess\n" + 
        		"Angriffswert    9321 / 9321     Captain 20 / 20 XPs 31\n" + 
        		"Schild  1573 / 2035(-462)   Crew    191 / 246   XPs 16\n" + 
        		"Panzerung   7441 / 8048(-246)   Systeme 3523 / 3523\n" + 
        		"Struktur    178 / 178   ID:10178200 \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Xemsial    Vice Venad Commander Jerry Jakobson\n" + 
        		"Angriffswert    9321 / 9321     Captain 20 / 20 XPs 25\n" + 
        		"Schild  1323 / 2035(-712)   Crew    191 / 246   XPs 13\n" + 
        		"Panzerung   6286 / 8048(-177)   Systeme 3523 / 3523\n" + 
        		"Struktur    178 / 178   ID:10177075 \n" + 
        		"\n" + 
        		"\n" + 
        		"Verteidiger Flotte: Reg-Nr. 206 (Dharr-Grosch)\n" + 
        		"Sikrelias   Lieutenant Junior Grade Captain\n" + 
        		"Angriffswert    16700 / 16700   Captain 13 / 13     XPs 190\n" + 
        		"Schild  8108 / 14000(-5892)     Crew    165 / 165   XPs 95\n" + 
        		"Panzerung   2117 / 2700(-583)   Systeme 15615 / 15615\n" + 
        		"Struktur    480 / 480   \n" + 
        		"\n" + 
        		"Sikrelias   Space Officer Captain\n" + 
        		"Angriffswert    16700 / 16700   Captain 18 / 18     XPs 263\n" + 
        		"Schild  10212 / 14000(-3788)    Crew    165 / 165   XPs 131\n" + 
        		"Panzerung   1632 / 2700(-1068)  Systeme 15615 / 15615\n" + 
        		"Struktur    480 / 480   \n" + 
        		"\n" + 
        		"Sikrelias   Orbit Officer Captain\n" + 
        		"Angriffswert    16700 / 16700   Captain 26 / 26     XPs 175\n" + 
        		"Schild  9090 / 14000(-4910)     Crew    165 / 165   XPs 88\n" + 
        		"Panzerung   2214 / 2700(-486)   Systeme 15615 / 15615\n" + 
        		"Struktur    480 / 480   \n" + 
        		"\n" + 
        		"";
        
        parse(paste);
    }
    
    

    private final static Pattern DROP_PATTERN = Pattern.compile(
        "Zurückgelassene Ressourcen\\s+((\\d+\\s*)+)");
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
    
    private final static Pattern SHIP_PATTERN = Pattern.compile("(.*)\\s{2,}(.*)\\s+" + 
        "Angriffswert\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+Captain\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))? XPs (\\d+)\\s+" + 
        "Schild\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+Crew\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))? XPs (\\d+)\\s+" +
        "Panzerung\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+Systeme\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+" + 
        "Struktur\\s+(\\d+) / (\\d+)(\\(-(\\d+)\\))?\\s+ID:(\\d+)");
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
        List<Drop> drops = new ArrayList<Drop>(14);
        String dropString = substr(paste, drop, DROP_GROUP);
        String[] parts = dropString.split("\\s+");
        
        int i = 0;
        for (String part : parts) {
            int amount = Integer.parseInt(part);
            RxRessource ress = RxRessource.byOrdinal(i++);
            drops.add(new Drop(ress, amount));
        }
        
        // Parse Battle location
        Matcher where = WHERE_PATTERN.matcher(paste);
        if (!where.find()) {
            parseException();
        }
        
        String quadrant = substr(paste, where, QUADRANT_GROUP);
        int x = subint(paste, where, X_GROUP);
        int y = subint(paste, where, Y_GROUP);
        
        System.out.println("Gefecht bei " + quadrant + " " + x + "," + y);
        
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
        
        Matcher fleet = FLEET_NAME_PATTERN.matcher(paste);
        while (fleet.find()) {
            if (fleet.group().startsWith("Angreifer")) {
                attackerVenad = substr(paste, fleet, VENAD_NAME_GROUP);
                attackerFleetName = substr(paste, fleet, FLEET_NAME_GROUP);
            } else {
                defenderVenad = substr(paste, fleet, VENAD_NAME_GROUP);
                defenderFleetName = substr(paste, fleet, FLEET_NAME_GROUP);
                defenderPos = fleet.end(VENAD_NAME_GROUP);
            }
        }
        
        List<BattleReportShip> attackerFleet = parseShips(substr(paste, 0, defenderPos));
        List<BattleReportShip> defenderFleet = 
            parseShips(substr(paste, defenderPos, paste.length()));
        
        BattleReport result = new BattleReport(quadrant, x, y, drops, date, tactic, 
            attackerBonus, defenderBonus, attackerKw, attackerXpMod, defenderkw, 
            defenderXpMod, attackerFleetName, attackerVenad, defenderFleetName, 
            defenderVenad, attackerFleet, defenderFleet);
        
        return result;
    }
    
    
    
    private static List<BattleReportShip> parseShips(String paste) {
        System.out.println(paste);
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
