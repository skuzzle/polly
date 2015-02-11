package polly.rx.entities;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public enum ShipType {
    ZERRI("Zerstörer"), //$NON-NLS-1$
    KREUZER("Kreuzer"), //$NON-NLS-1$
    KORVETTE("Korvette"), //$NON-NLS-1$
    FREGATTE("Fregatte"), //$NON-NLS-1$
    SCHLACHTKREUZER("Schlachtkreuzer"), //$NON-NLS-1$
    FRACHTER("Frachtschiff"), //$NON-NLS-1$
    TANKER("Tankschiff"), //$NON-NLS-1$
    BEGLEITER("Begleitschiff"), //$NON-NLS-1$
    KOMMANDO("Kommandoschiff"), //$NON-NLS-1$
    VERSORGER("Versorgungsschiff"), //$NON-NLS-1$
    TRANSPORTER("Transportschiff"), //$NON-NLS-1$
    UNKNOWN("Unbekannt"), //$NON-NLS-1$
    GEFECHTSSTATION("Gefechtsstation"); //$NON-NLS-1$

    public static ShipType byPrefix(String shipName) {
        try {
            shipName = URLDecoder.decode(shipName, "ISO-8859-1"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            return UNKNOWN;
        }
        // HACK: Evilest encoding hack of all times
        if (shipName.startsWith("bäähhhh")) { //$NON-NLS-1$
            return KREUZER;
        } else if (shipName.contains("bäähhhh")) { //$NON-NLS-1$
            return SCHLACHTKREUZER;
        } else if (shipName.startsWith("Zerst")) { //$NON-NLS-1$
            return ZERRI;
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