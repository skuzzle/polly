package polly.rx.entities;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public enum ShipType {
    ZERRI("Zerstörer"),
    KREUZER("Kreuzer"),
    KORVETTE("Korvette"),
    FREGATTE("Fregatte"),
    SCHLACHTKREUZER("Schlachtkreuzer"),
    FRACHTER("Frachtschiff"),
    TANKER("Tankschiff"),
    BEGLEITER("Begleitschiff"),
    KOMMANDO("Kommandoschiff"),
    VERSORGER("Versorgungsschiff"),
    TRANSPORTER("Transportschiff"),
    UNKNOWN("Unbekannt");
    
    public static ShipType byPrefix(String shipName) {
        try {
            shipName = URLDecoder.decode(shipName, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            return UNKNOWN;
        }
        if (shipName.startsWith("bäähhhh")) {
            return KREUZER;
        } else if (shipName.contains("bäähhhh")) {
            return SCHLACHTKREUZER;
        }
        for (final ShipType st : ShipType.values()) {
            if (shipName.startsWith(st.name)) {
                return st;
            }
        }
        return UNKNOWN;
    }
    private final String name;
    
    private ShipType(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}