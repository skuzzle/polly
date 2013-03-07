package polly.rx.entities;


public enum BattleTactic {
    NORMAL, RAUBZUG, MACHTDEMO, TPT, SCHNITT, NAHKAMPF, SYSTEM, AUSWEICH, TT, ZANGE, 
    ABGESICHERT, SONDIERUNG, FERN, MULTIVEKTOR, KONZENTRIERT, VERTREIBEN, KRALLE, SICHEL, 
    HITNRUN, HINTERHALT, STURM, DAUERBESCHUSS, ALIEN;
    
    public static BattleTactic parseTactic(String tactic) {
        tactic = tactic.trim();
        
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
        else if(tactic.equals("Alien Angriff")) { return BattleTactic.ALIEN; }
        
        throw new IllegalArgumentException("invalid tactic: " + tactic);
    }
    
    
    
    @Override
    public String toString() {
        switch (this) {
        default:
        case NORMAL: return "Normaler Angriff";
        case RAUBZUG: return "Raubzug";
        case MACHTDEMO: return "Machtdemonstration";
        case TPT: return "Taktisches Profi Training";
        case SCHNITT: return "Chirurgischer Schnitt";
        case NAHKAMPF:  return "Nahkampf";
        case SYSTEM:  return "Kritischer Systemangriff";
        case AUSWEICH:  return "Ausweichmanöver";
        case TT:  return "Taktisches Training";
        case ZANGE:  return "Zangenangriff";
        case ABGESICHERT:  return "abgesicherter Angriff";
        case SONDIERUNG:  return "Sondierung";
        case FERN:  return "Fernangriff";
        case MULTIVEKTOR:  return "Multivektor Angriff";
        case KONZENTRIERT:  return "Konzentrierter Angriff";
        case VERTREIBEN:  return "Vertreiben";
        case KRALLE:  return "Todeskralle";
        case SICHEL:  return "Sichelangriff";
        case HITNRUN:  return "Hit-and-Run";
        case HINTERHALT:  return "Hinterhalt";
        case STURM:  return "Sturmangriff";
        case DAUERBESCHUSS: return "Dauerbeschuss";
        case ALIEN: return "Alien Angriff";
        }
    }
}