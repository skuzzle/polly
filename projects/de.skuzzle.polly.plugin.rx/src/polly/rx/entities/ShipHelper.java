package polly.rx.entities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.skuzzle.polly.tools.math.MathUtil;


public class ShipHelper {
    
    private final static Pattern CLASS_PATTERN = Pattern.compile("\\(([XVICML]+)\\)");

    public static String getSimpleName(String shipName) {
        final int i = shipName.lastIndexOf(' ');
        if (i != -1) {
            return shipName.substring(i);
        } else {
            return shipName;
        }
    }
    
    
    
    public static int getShipClass(String shipName) {
        final Matcher m = CLASS_PATTERN.matcher(shipName);
        if (m.find()) {
            final String roman = shipName.substring(m.start(1), m.end(1));
            return MathUtil.parseRoman(roman);
        } else {
            return 0;
        }
    }
    
    
    
    public static ShipType getShipType(String shipName) {
        return ShipType.byPrefix(shipName);
    }
}
