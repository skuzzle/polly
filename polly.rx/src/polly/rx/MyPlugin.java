package polly.rx;

import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.BattleDrop;
import polly.rx.parsing.BattleReportParser;
import polly.rx.http.BattleReportHttpAction;
import polly.rx.http.BattleReportInfosHttpAction;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import de.skuzzle.polly.sdk.exceptions.PluginException;


public class MyPlugin extends PollyPlugin {

    
    private BattleReportManager battleReportManager;
    
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException {
        super(myPolly);
        
        this.battleReportManager = new BattleReportManager(myPolly.persistence());
        
        myPolly.persistence().registerEntity(BattleReport.class);
        myPolly.persistence().registerEntity(BattleReportShip.class);
        myPolly.persistence().registerEntity(BattleDrop.class);
        
        myPolly.web().addHttpAction(new BattleReportInfosHttpAction(
            myPolly, this.battleReportManager));
        myPolly.web().addHttpAction(new BattleReportHttpAction(
            myPolly, this.battleReportManager));
        myPolly.web().addMenuUrl("Revorix", "Kampfberichte");
    }

    
    
    @Override
    public void onLoad() throws PluginException {
        super.onLoad();
        
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
    
        try {
            BattleReport rep = BattleReportParser.parse(paste);
            
            System.out.println(rep.getAttackerShips());
            System.out.println(rep.getDefenderShips());
            
            this.getMyPolly().persistence().atomicPersist(rep);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
}
