package polly.rx.entities;

import java.util.NoSuchElementException;


public enum TrainType {
    INTELLIGENCE, BODY, MODULE, CREW, TECH, COMMANDO, PAYMENT,
    EXTENDED_INTELLIGENCE, EXTENDED_BODY, EXTENDED_MODULE, EXTENDED_CREW, EXTENDED_TECH,
    EXTENDED_COMMANDO,
    INTESIVE_INTELLIGENCE, INTESIVE_BODY, INTESIVE_MODULE, INTESIVE_CREW, INTESIVE_TECH,
    INTESIVE_COMMANDO;
    
    
    public static TrainType parse(String s) {
        if (s.contains("intensives")) {
            if (s.contains("Intelligenz")) {
                return INTESIVE_INTELLIGENCE;
            } else if (s.contains("Kommandolimit")) {
                return INTESIVE_COMMANDO;
            } else if (s.contains("Modullimit")) {
                return INTESIVE_MODULE;
            } else if (s.contains("Körper")) {
                return INTESIVE_BODY;
            } else if (s.contains("Crewlimit")) {
                return INTESIVE_CREW;
            } else if (s.contains("Techlimit")) {
                return INTESIVE_TECH;
            }
        } else if (s.contains("erweitertes")) {
            if (s.contains("Intelligenz")) {
                return EXTENDED_INTELLIGENCE;
            } else if (s.contains("Kommandolimit")) {
                return EXTENDED_COMMANDO;
            } else if (s.contains("Modullimit")) {
                return EXTENDED_MODULE;
            } else if (s.contains("Körper")) {
                return EXTENDED_BODY;
            } else if (s.contains("Crewlimit")) {
                return EXTENDED_CREW;
            } else if (s.contains("Techlimit")) {
                return EXTENDED_TECH;
            }
        } else if (s.contains("Anzahlung")) {
            return PAYMENT;
        } else {
            if (s.contains("Intelligenz")) {
                return INTELLIGENCE;
            } else if (s.contains("Kommandolimit")) {
                return COMMANDO;
            } else if (s.contains("Modullimit")) {
                return MODULE;
            } else if (s.contains("Körper")) {
                return BODY;
            } else if (s.contains("Crewlimit")) {
                return CREW;
            } else if (s.contains("Techlimit")) {
                return TECH;
            }
        }
        throw new NoSuchElementException("unknown train type: " + s);
    }
    
    
    
    @Override
    public String toString() {
        switch (this) {
        default:
        case INTELLIGENCE: return "Intelligenz Training";
        case BODY: return "Körper Training";
        case COMMANDO: return "Kommandolimit Training";
        case MODULE: return "Modullimit Training";
        case CREW: return "Crewlimit Training";
        case TECH: return "Techlimit Training";
        case PAYMENT: return "Anzahlung";
        
        case EXTENDED_INTELLIGENCE: return "erweitertes Intelligenz Training";
        case EXTENDED_BODY: return "erweitertes Körper Training";
        case EXTENDED_COMMANDO: return "erweitertes Kommandolimit Training";
        case EXTENDED_MODULE: return "erweitertes Modullimit Training";
        case EXTENDED_CREW: return "erweitertes Crewlimit Training";
        case EXTENDED_TECH: return "erweitertes Techlimit Training";
        
        case INTESIVE_INTELLIGENCE: return "intensives Intelligenz Training";
        case INTESIVE_BODY: return "intensives Körper Training";
        case INTESIVE_COMMANDO: return "intensives Kommandolimit Training";
        case INTESIVE_MODULE: return "intensives Modullimit Training";
        case INTESIVE_CREW: return "intensives Crewlimit Training";
        case INTESIVE_TECH: return "intensives Techlimit Training";
        }
    }
}