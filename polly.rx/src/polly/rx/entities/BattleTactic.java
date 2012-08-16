package polly.rx.entities;


public enum BattleTactic {
    NORMAL, RAUBZUG, MACHTDEMO, TPT, SCHNITT, NAHKAMPF, SYSTEM, AUSWEICH, TT, ZANGE, 
    ABGESICHERT, SONDIERUNG, FERN, MULTIVEKTOR, KONZENTRIERT, VERTREIBEN, KRALLE, SICHEL, 
    HITNRUN, HINTERHALT, STURM, DAUERBESCHUSS;
    
    public static BattleTactic parseTactic(String tactic) {
        if (tactic.equals("Normaler Angriff")) { return BattleTactic.NORMAL; }
        else if(tactic.equals("Raubzug")) { return BattleTactic.RAUBZUG; }
        else if(tactic.equals("Machtdemonstration")) { return BattleTactic.MACHTDEMO; }
        else if(tactic.equals("Taktisches Profi Training")) { return BattleTactic.TPT; }
        else if(tactic.equals("Chirurgischer Schnitt")) { return BattleTactic.SCHNITT; }
        else if(tactic.equals("Nahkampf")) { return BattleTactic.NAHKAMPF; }
        else if(tactic.equals("Kritischer Systemangriff")) { return BattleTactic.SYSTEM; }
        else if(tactic.equals("Ausweichmanöver")) { return BattleTactic.AUSWEICH; }
        else if(tactic.equals("Taktisches Training")) { return BattleTactic.TT; }
        else if(tactic.equals("Zangenangriff")) { return BattleTactic.ZANGE; }
        else if(tactic.equals("abgesicherter Angriff")) { return BattleTactic.ABGESICHERT; }        
        else if(tactic.equals("Sondierung")) { return BattleTactic.SONDIERUNG; }
        else if(tactic.equals("Fernangriff")) { return BattleTactic.FERN; }
        
        else if(tactic.equals("Multivektor Angriff")) { return BattleTactic.MULTIVEKTOR; }
        else if(tactic.equals("Konzentrierter Angriff")) { return BattleTactic.KONZENTRIERT; }
        else if(tactic.equals("Vertreiben")) { return BattleTactic.VERTREIBEN; }        
        else if(tactic.equals("Todeskralle")) { return BattleTactic.KRALLE; }
        else if(tactic.equals("Sichelangriff")) { return BattleTactic.SICHEL; }
        else if(tactic.equals("Hit-and-Run")) { return BattleTactic.HITNRUN; }
        else if(tactic.equals("Hinterhalt")) { return BattleTactic.HINTERHALT; }
        else if(tactic.equals("Sturmangriff")) { return BattleTactic.STURM; }
        else if(tactic.equals("Dauerbeschuss")) { return BattleTactic.DAUERBESCHUSS; }
        
        throw new IllegalArgumentException("invalid tactic: " + tactic);
    }
}