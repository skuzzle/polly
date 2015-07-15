package polly.rx.entities;

import polly.rx.MSG;


public enum BattleTactic {
    NORMAL, RAUBZUG, MACHTDEMO, TPT, SCHNITT, NAHKAMPF, SYSTEM, AUSWEICH, TT, ZANGE,
    ABGESICHERT, SONDIERUNG, FERN, MULTIVEKTOR, KONZENTRIERT, VERTREIBEN, KRALLE, SICHEL,
    HITNRUN, HINTERHALT, STURM, DAUERBESCHUSS, ALIEN, SEKTOR_WACHE;

    public static BattleTactic parseTactic(String tactic) {
        tactic = tactic.trim();

        if (tactic.equals("Normaler Angriff")) { return BattleTactic.NORMAL; } //$NON-NLS-1$
        else if(tactic.equals("Raubzug")) { return BattleTactic.RAUBZUG; } //$NON-NLS-1$
        else if(tactic.equals("Machtdemonstration")) { return BattleTactic.MACHTDEMO; } //$NON-NLS-1$
        else if(tactic.equals("Taktisches Profi Training")) { return BattleTactic.TPT; } //$NON-NLS-1$
        else if(tactic.equals("Chirurgischer Schnitt")) { return BattleTactic.SCHNITT; } //$NON-NLS-1$
        else if(tactic.equals("Nahkampf")) { return BattleTactic.NAHKAMPF; } //$NON-NLS-1$
        else if(tactic.equals("Kritischer Systemangriff")) { return BattleTactic.SYSTEM; } //$NON-NLS-1$
        else if(tactic.equals("Ausweichman�ver")) { return BattleTactic.AUSWEICH; } //$NON-NLS-1$
        else if(tactic.equals("Taktisches Training")) { return BattleTactic.TT; } //$NON-NLS-1$
        else if(tactic.equals("Zangenangriff")) { return BattleTactic.ZANGE; } //$NON-NLS-1$
        else if(tactic.equals("abgesicherter Angriff")) { return BattleTactic.ABGESICHERT; }         //$NON-NLS-1$
        else if(tactic.equals("Sondierung")) { return BattleTactic.SONDIERUNG; } //$NON-NLS-1$
        else if(tactic.equals("Fernangriff")) { return BattleTactic.FERN; } //$NON-NLS-1$
        else if(tactic.equals("Multivektor Angriff")) { return BattleTactic.MULTIVEKTOR; } //$NON-NLS-1$
        else if(tactic.equals("Konzentrierter Angriff")) { return BattleTactic.KONZENTRIERT; } //$NON-NLS-1$
        else if(tactic.equals("Vertreiben")) { return BattleTactic.VERTREIBEN; }         //$NON-NLS-1$
        else if(tactic.equals("Todeskralle")) { return BattleTactic.KRALLE; } //$NON-NLS-1$
        else if(tactic.equals("Sichelangriff")) { return BattleTactic.SICHEL; } //$NON-NLS-1$
        else if(tactic.equals("Hit-and-Run")) { return BattleTactic.HITNRUN; } //$NON-NLS-1$
        else if(tactic.equals("Hinterhalt")) { return BattleTactic.HINTERHALT; } //$NON-NLS-1$
        else if(tactic.equals("Sturmangriff")) { return BattleTactic.STURM; } //$NON-NLS-1$
        else if(tactic.equals("Dauerbeschuss")) { return BattleTactic.DAUERBESCHUSS; } //$NON-NLS-1$
        else if(tactic.equals("Alien Angriff")) { return BattleTactic.ALIEN; } //$NON-NLS-1$
        else if (tactic.startsWith("Sprungkampf - Eindringlingsgefecht")) { return BattleTactic.SEKTOR_WACHE; } //$NON-NLS-1$
        else if (tactic.startsWith("Eindringen - Normaler Angriff")) { return BattleTactic.SEKTOR_WACHE; } //$NON-NLS-1$

        throw new IllegalArgumentException("invalid tactic: " + tactic); //$NON-NLS-1$
    }



    @Override
    public String toString() {
        switch (this) {
        default:
        case NORMAL: return MSG.tacticNormal;
        case RAUBZUG: return MSG.tacticRaubzug;
        case MACHTDEMO: return MSG.tacticMachtDemo;
        case TPT: return MSG.tacticTPT;
        case SCHNITT: return MSG.tacticSchnitt;
        case NAHKAMPF:  return MSG.tacticNahkampf;
        case SYSTEM:  return MSG.tacticSystem;
        case AUSWEICH:  return MSG.tacticAusweich;
        case TT:  return MSG.tacticTT;
        case ZANGE:  return MSG.tacticZange;
        case ABGESICHERT:  return MSG.tacticAbgesichert;
        case SONDIERUNG:  return MSG.tacticSondierung;
        case FERN:  return MSG.tacticFern;
        case MULTIVEKTOR:  return MSG.tacticMultivektor;
        case KONZENTRIERT:  return MSG.tacticKonzentriert;
        case VERTREIBEN:  return MSG.tacticVertreiben;
        case KRALLE:  return MSG.tacticKralle;
        case SICHEL:  return MSG.tacticSichel;
        case HITNRUN:  return MSG.tacticHitAndRun;
        case HINTERHALT:  return MSG.tacticHinterhalt;
        case STURM:  return MSG.tacticSturm;
        case DAUERBESCHUSS: return MSG.tacticDauerbeschuss;
        case ALIEN: return MSG.tacticAlien;
        case SEKTOR_WACHE: return MSG.tacticSekWache;
        }
    }
}