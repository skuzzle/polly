package polly.rx.parsing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.skuzzle.polly.sdk.model.User;
import de.skuzzle.polly.tools.iterators.ArrayIterator;

import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.BattleTactic;
import polly.rx.entities.RxRessource;


public class BattleReportParser {
    
    private final static Pattern NUBER_PATTERN = Pattern.compile("\\d+"); 
    
    private final static Pattern WHERE_PATTERN = Pattern.compile(
        "Gefecht bei (.*) (\\d+),(\\d+)");
    private final static int QUADRANT_GROUP = 1;
    private final static int X_GROUP = 2;
    private final static int Y_GROUP = 3;
    
    
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
    
    
    
    public static void main(String[] args) throws ParseException {
        String paste = " 17:59 26-08-2012\n" + 
        		"Zurückgelassene Ressourcen\n" + 
        		"Cr0     Nrg0    Rek0    Erz2415     Org129  Synt439     Fe368   LM566   SM395   EM23    Rad0    ES0     EG5     Iso6\n" + 
        		"1 Artefakt(e) erbeutet\n" + 
        		"\n" + 
        		"Gefecht bei Minos Fatalis 15,4\n" + 
        		"Die Angreifer waren siegreich   Gefechtstaktik  Raubzug\n" + 
        		"Bonus Angreifer 2%  Bonus Verteidiger   -2%\n" + 
        		"Kampfwert Angreifer/XP-Mod  75742.01/0.63   Kampfwert Verteidiger/XP-Mod    47628/0.7\n" + 
        		"\n" + 
        		"Angreifer Flotte: MightyBetty (smash[Loki])\n" + 
        		"Begleitschiff (LIII) Herxosi    Fleet Marshall Jan Martin Jacobs\n" + 
        		"Angriffswert    194 / 194   Captain 15 / 15 XPs 18\n" + 
        		"Schild  1631 / 2070(-439)   Crew    137 / 137   XPs 5\n" + 
        		"Panzerung   11634 / 13680(-50)  Systeme 3938 / 3938\n" + 
        		"Struktur    494 / 494   ID:11107040 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Lentra     Galaxy Marshall Meike Ehler\n" + 
        		"Angriffswert    195 / 195   Captain 15 / 15 XPs 27\n" + 
        		"Schild  1498 / 2006(-508)   Crew    137 / 137   XPs 10\n" + 
        		"Panzerung   10900 / 13675(-149)     Systeme 3841 / 3841\n" + 
        		"Struktur    519 / 519   ID:11142075 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Nemsek     Lance Corporal Bodo Greene\n" + 
        		"Angriffswert    191 / 191   Captain 10 / 10 XPs 29\n" + 
        		"Schild  1702 / 2061(-359)   Crew    137 / 137   XPs 11\n" + 
        		"Panzerung   11288 / 13712(-168)     Systeme 3883 / 3883\n" + 
        		"Struktur    517 / 517   ID:11116300 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Othrax     Fleet Admiral Gaynor Lanlermann\n" + 
        		"Angriffswert    191 / 191   Captain 21 / 21 XPs 21\n" + 
        		"Schild  1536 / 1997(-461)   Crew    137 / 137   XPs 7\n" + 
        		"Panzerung   10976 / 13522(-84)  Systeme 3788 / 3788\n" + 
        		"Struktur    497 / 497   ID:11212243 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Sikerier   Star Marshall Gaby Kalckberner\n" + 
        		"Angriffswert    198 / 198   Captain 15 / 15 XPs 26\n" + 
        		"Schild  1645 / 2038(-393)   Crew    137 / 137   XPs 10\n" + 
        		"Panzerung   11642 / 13887(-140)     Systeme 3905 / 3905\n" + 
        		"Struktur    506 / 506   ID:11161983 \n" + 
        		"\n" + 
        		"Begleitschiff (LIII) Zertrax    Star Marshall Lidie Zapata\n" + 
        		"Angriffswert    196 / 196   Captain 21 / 21 XPs 28\n" + 
        		"Schild  1728 / 2038(-310)   Crew    137 / 137   XPs 11\n" + 
        		"Panzerung   11027 / 13800(-158)     Systeme 3873 / 3873\n" + 
        		"Struktur    517 / 517   ID:11142076 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Chantaus     Venad Sub Commander Shalyna Seehaus\n" + 
        		"Angriffswert    169 / 169   Captain 15 / 15 XPs 34\n" + 
        		"Schild  1021 / 1440(-419)   Crew    30 / 32 XPs 14\n" + 
        		"Panzerung   5164 / 8243(-223)   Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10385501 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Funga    Star Marshall Rieke Roughley\n" + 
        		"Angriffswert    169 / 169   Captain 15 / 15 XPs 19\n" + 
        		"Schild  957 / 1440(-483)    Crew    32 / 32 XPs 6\n" + 
        		"Panzerung   6432 / 8243(-66)    Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10296537 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Netarus  Sub Marshall Tobi Piaget\n" + 
        		"Angriffswert    166 / 166   Captain 21 / 21 XPs 30\n" + 
        		"Schild  1006 / 1418(-412)   Crew    32 / 32 XPs 11\n" + 
        		"Panzerung   6154 / 8119(-177)   Systeme 2843 / 2843\n" + 
        		"Struktur    152 / 152   ID:10778297 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Novalis  Group Marshall Zina Ahlers\n" + 
        		"Angriffswert    169 / 169   Captain 15 / 15 XPs 16\n" + 
        		"Schild  912 / 1440(-528)    Crew    30 / 32 XPs 4\n" + 
        		"Panzerung   6083 / 8243(-28)    Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10384757 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Quasolas     Fleet Marshall Emmi Bamberg\n" + 
        		"Angriffswert    169 / 169   Captain 21 / 21 XPs 16\n" + 
        		"Schild  976 / 1440(-464)    Crew    30 / 32 XPs 4\n" + 
        		"Panzerung   6211 / 8243(-29)    Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10320935 \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Tridona  Fleet Marshall Shanice Nienhaus\n" + 
        		"Angriffswert    169 / 169   Captain 16 / 16 XPs 32\n" + 
        		"Schild  1053 / 1440(-387)   Crew    30 / 32 XPs 12\n" + 
        		"Panzerung   5727 / 8243(-198)   Systeme 2887 / 2887\n" + 
        		"Struktur    154 / 154   ID:10341934 \n" + 
        		"\n" + 
        		"Kommandoschiff (XLII) Xontrapit     Vice Venad Commander Tasso Hilyard\n" + 
        		"Angriffswert    58 / 58     Captain 20 / 20 XPs 32\n" + 
        		"Schild  1198 / 1453(-255)   Crew    118 / 118   XPs 13\n" + 
        		"Panzerung   4425 / 6847(-204)   Systeme 3421 / 3421\n" + 
        		"Struktur    260 / 260   ID:8643857  \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Degerox    Galaxy Marshall Basilius Heegmann\n" + 
        		"Angriffswert    8826 / 8826     Captain 16 / 16 XPs 18\n" + 
        		"Schild  1310 / 1928(-618)   Crew    191 / 246   XPs 6\n" + 
        		"Panzerung   5911 / 7621(-57)    Systeme 3337 / 3337\n" + 
        		"Struktur    169 / 169   ID:12479998 \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Moluga     Vice Venad Commander Cilia Albert\n" + 
        		"Angriffswert    9321 / 9321     Captain 20 / 20 XPs 25\n" + 
        		"Schild  1443 / 2035(-592)   Crew    191 / 246   XPs 9\n" + 
        		"Panzerung   5928 / 8048(-131)   Systeme 3523 / 3523\n" + 
        		"Struktur    178 / 178   ID:10162025 \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Nesono     Vice Venad Commander Hedda Troost\n" + 
        		"Angriffswert    9321 / 9321     Captain 17 / 17 XPs 24\n" + 
        		"Schild  1676 / 2035(-359)   Crew    191 / 246   XPs 9\n" + 
        		"Panzerung   5656 / 8048(-116)   Systeme 3523 / 3523\n" + 
        		"Struktur    178 / 178   ID:10126012 \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Riwe   Vice Venad Commander Marko Kniess\n" + 
        		"Angriffswert    9321 / 9321     Captain 20 / 20 XPs 23\n" + 
        		"Schild  1413 / 2035(-622)   Crew    191 / 246   XPs 8\n" + 
        		"Panzerung   5774 / 8048(-106)   Systeme 3523 / 3523\n" + 
        		"Struktur    178 / 178   ID:10178200 \n" + 
        		"\n" + 
        		"Zerstörer (XXXV) Xemsial    Vice Venad Commander Jerry Jakobson\n" + 
        		"Angriffswert    9321 / 9321     Captain 20 / 20 XPs 26\n" + 
        		"Schild  1696 / 2035(-339)   Crew    191 / 246   XPs 9\n" + 
        		"Panzerung   5648 / 8048(-133)   Systeme 3523 / 3523\n" + 
        		"Struktur    178 / 178   ID:10177075 \n" + 
        		"\n" + 
        		"Frachtschiff (LIII) Trakon  Recruit Michael Schumacher\n" + 
        		"Angriffswert    43 / 43     Captain 17 / 17 XPs 18\n" + 
        		"Schild  1064 / 1619(-555)   Crew    26 / 31 XPs 6\n" + 
        		"Panzerung   3779 / 5577(-57)    Systeme 3137 / 3137\n" + 
        		"Struktur    193 / 193   ID:8732586  \n" + 
        		"\n" + 
        		"Frachtschiff (LIII) Vistren     Space Guard 3rd Class Agnes Narjes\n" + 
        		"Angriffswert    45 / 45     Captain 13 / 13 XPs 29\n" + 
        		"Schild  1097 / 1644(-547)   Crew    26 / 31 XPs 11\n" + 
        		"Panzerung   3603 / 5548(-168)   Systeme 3179 / 3179\n" + 
        		"Struktur    195 / 195   ID:8779387  \n" + 
        		"\n" + 
        		"Tankschiff (LI) Lopatrak    Recruit Bertha Erbacher\n" + 
        		"Angriffswert    44 / 44     Captain 8 / 8   XPs 28\n" + 
        		"Schild  997 / 1368(-371)    Crew    30 / 35 XPs 11\n" + 
        		"Panzerung   4396 / 6263(-159)   Systeme 2793 / 2793\n" + 
        		"Struktur    273 / 273   ID:8659217  \n" + 
        		"\n" + 
        		"Tankschiff (LI) Maxerus     Star Marshall Seamus Lierhammer\n" + 
        		"Angriffswert    46 / 46     Captain 17 / 17 XPs 17\n" + 
        		"Schild  675 / 1371(-696)    Crew    30 / 35 XPs 5\n" + 
        		"Panzerung   3953 / 6161(-44)    Systeme 2804 / 2804\n" + 
        		"Struktur    285 / 285   ID:8638682  \n" + 
        		"\n" + 
        		"Tankschiff (LI) Telaros     Chief Commander Cheyenne Riedel\n" + 
        		"Angriffswert    47 / 47     Captain 11 / 11 XPs 19\n" + 
        		"Schild  966 / 1390(-424)    Crew    30 / 35 XPs 6\n" + 
        		"Panzerung   3733 / 6294(-62)    Systeme 2796 / 2796\n" + 
        		"Struktur    277 / 277   ID:8659216  \n" + 
        		"\n" + 
        		"Frachtschiff (LIX) Tengal   Ship Marshall Carmen Hagbaum\n" + 
        		"Angriffswert    169 / 169   Captain 10 / 10 XPs 32\n" + 
        		"Schild  1167 / 1427(-260)   Crew    30 / 32 XPs 12\n" + 
        		"Panzerung   7284 / 8334(-200)   Systeme 2883 / 2883\n" + 
        		"Struktur    156 / 156   ID:10338782 \n" + 
        		"\n" + 
        		"Frachtschiff (LVIII) Okanis     1st Lieutenant Chiara Sander\n" + 
        		"Angriffswert    169 / 169   Captain 3 / 3   XPs 34\n" + 
        		"Schild  1194 / 1486(-292)   Crew    32 / 32 XPs 14\n" + 
        		"Panzerung   5601 / 7018(-225)   Systeme 3091 / 3091\n" + 
        		"Struktur    249 / 249   ID:10376111 \n" + 
        		"\n" + 
        		"\n" + 
        		"Verteidiger Flotte: Reg-Nr. 133 (Grazet)\n" + 
        		"Kir'Shara   Lieutenant Junior Grade Captain\n" + 
        		"Angriffswert    5500 / 5500     Captain 16 / 16     XPs 92\n" + 
        		"Schild  2236 / 5000(-2764)  Crew    41 / 41 XPs 46\n" + 
        		"Panzerung   355 / 500(-145)     Systeme 7795 / 7795\n" + 
        		"Struktur    510 / 510   \n" + 
        		"\n" + 
        		"En'Takam    Orbit Officer Captain\n" + 
        		"Angriffswert    24829 / 25000(-171)     Captain -19 / 12(-31)   XPs 70\n" + 
        		"Schild  21992 / 24000(-2008)    Crew    0 / 314(-314)   XPs 35\n" + 
        		"Panzerung   0 / 1000(-1000)     Systeme 25624 / 26380(-756)\n" + 
        		"Struktur    0 / 600(-600)   \n" + 
        		"zerstört\n" + 
        		"\n" + 
        		"Kir'Shara   Lt. Commander 3rd Class Captain\n" + 
        		"Angriffswert    5500 / 5500     Captain 14 / 14     XPs 75\n" + 
        		"Schild  2119 / 5000(-2881)  Crew    41 / 41 XPs 37\n" + 
        		"Panzerung   471 / 500(-29)  Systeme 7795 / 7795\n" + 
        		"Struktur    510 / 510   \n" + 
        		"\n" + 
        		"Kir'Shara   Lance Corporal Captain\n" + 
        		"Angriffswert    5352 / 5500(-148)   Captain 2 / 19(-17)     XPs 355\n" + 
        		"Schild  1999 / 5000(-3001)  Crew    0 / 41(-41) XPs 148\n" + 
        		"Panzerung   0 / 500(-500)   Systeme 7594 / 7795(-201)\n" + 
        		"Struktur    9 / 510(-501)   \n" + 
        		"\n" + 
        		"Kir'Shara   Chief Space Guard Captain\n" + 
        		"Angriffswert    5489 / 5500(-11)    Captain 10 / 13(-3)     XPs 189\n" + 
        		"Schild  2701 / 5000(-2299)  Crew    2 / 41(-39) XPs 89\n" + 
        		"Panzerung   0 / 500(-500)   Systeme 7753 / 7795(-42)\n" + 
        		"Struktur    399 / 510(-111) \n" + 
        		"\n" + 
        		"";
        
        parseReportHelper(paste, null);
    }
    
    
    
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
            date = DATE_FORMAT.parse(it.next());
        } catch (Exception e) {
            throw new ParseException();
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
            throw new ParseException();
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
            throw new ParseException();
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
            throw new ParseException();
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
            throw new ParseException();
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
        
        b = new StringBuilder();
        while (it.hasNext()) {
            b.append(it.next());
            b.append('\n');
        }
        List<BattleReportShip> defenderShips = parseShips(b.toString());
        
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
            int pzDmg = RegexUtils.subint(paste, ships, PZ_DMG_GROUP);
            int systems = RegexUtils.subint(paste, ships, SYSTEMS_GROUP);
            int systemsDmg = RegexUtils.subint(paste, ships, SYSTEMS_DMG_GROUP);
            int structure = RegexUtils.subint(paste, ships, STRUCTURE_GROUP);
            int structureDmg = RegexUtils.subint(paste, ships, STRUCTURE_DMG_GROUP);
            int rxId = RegexUtils.subint(paste, ships, ID_GROUP);
            
            BattleReportShip ship = new BattleReportShip(rxId, shipName, capiName, aw, 
                shields, pz, structure, minCrew, maxCrew, systems, xpCapi, xpCrew, 
                shieldsDmg, pzDmg, structureDmg, systemsDmg, hp, hpDmg, awDmg, crewDmg);
            System.out.println(ship);
            result.add(ship);
        }
        
        return result;
    }
}
