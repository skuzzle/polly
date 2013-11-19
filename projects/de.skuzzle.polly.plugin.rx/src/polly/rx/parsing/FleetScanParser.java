package polly.rx.parsing;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.skuzzle.polly.tools.iterators.ArrayIterator;
import polly.rx.MSG;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanShip;


public class FleetScanParser {
    
    private final static Pattern CLAN_PATTERN = Pattern.compile("\\[([^\\]]+)\\]"); //$NON-NLS-1$
    private final static int CLAN_GROUP = 1;

    private final static Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)"); //$NON-NLS-1$
    private final static int NUMBER_GROUP = 1;
    
    
    private final static Pattern SHIP_PATTERN = Pattern.compile(
            "(.+?)\\s+\\(ID:(\\d+)\\)\\s+(\\d+)\\s+(.+)"); //$NON-NLS-1$
    private final static int SHIP_NAME_GROUP = 1;
    private final static int SHIP_ID_GROUP = 2;
    private final static int SHIP_TL_GROUP = 3;
    private final static int SHIP_OWNER_GROUP = 4;
    
    
    
    public final static FleetScan parseFleetScan(String paste, String quadrant, 
            int x, int y, String metaData) throws ParseException {
        try {
            return parseFleetScanHelper(paste, quadrant, x, y, metaData);
        } catch (Exception e) {
            throw new ParseException(MSG.fleetScanParserInvalid, e);
        }
    }
    
    
    
    private final static FleetScan parseFleetScanHelper(String paste, String quadrant, 
            int x, int y, String metaData) throws ParseException {
        String[] lines = paste.split("[\n\r]+"); //$NON-NLS-1$
        
        int sens = 0;
        String fleetName = ""; //$NON-NLS-1$
        String owner = ""; //$NON-NLS-1$
        String ownerClan = ""; //$NON-NLS-1$
        String fleetTag = ""; //$NON-NLS-1$
        List<FleetScanShip> ships = new LinkedList<FleetScanShip>();
        
        ArrayIterator<String> it = ArrayIterator.get(lines);
        while (it.hasNext()) {
            String line = it.next();
            
            if (line.startsWith("Lokale Sensor")) { //$NON-NLS-1$
                Matcher m = NUMBER_PATTERN.matcher(line);
                m.find();
                
                sens = RegexUtils.subint(line, m, NUMBER_GROUP);
                
            } else if (line.startsWith("Flotten Daten")) { //$NON-NLS-1$
                line = it.next();
                String parts[] = line.split("\\s+"); //$NON-NLS-1$
                fleetName = parts[0];
                
                Matcher m = CLAN_PATTERN.matcher(parts[1]);
                if (m.find()) {
                    ownerClan = RegexUtils.substr(parts[1], m, CLAN_GROUP);
                    owner = RegexUtils.substr(
                        parts[1], 0, parts[1].length() - (ownerClan.length() + 2));
                } else {
                    owner = parts[1];
                }
                
                String next = it.peekNext();
                if (!next.equals("") && !next.startsWith("Gescannte Schiffe")) { //$NON-NLS-1$ //$NON-NLS-2$
                    fleetTag = it.next();
                }
            } else if (line.startsWith("Gescannte Schiffe")) { //$NON-NLS-1$
                it.next();
                while (it.hasNext()) {
                    line = it.next();
                    Matcher m = SHIP_PATTERN.matcher(line);
                    if (m.matches()) {
                        String shipName = RegexUtils.substr(line, m, SHIP_NAME_GROUP);
                        int shipId = RegexUtils.subint(line, m, SHIP_ID_GROUP);
                        int shipTl = RegexUtils.subint(line, m, SHIP_TL_GROUP);
                        String ownerName = RegexUtils.substr(line, m, SHIP_OWNER_GROUP);
                        String shipOwnerClan = ""; //$NON-NLS-1$
                        
                        Matcher clanMatcher = CLAN_PATTERN.matcher(ownerName);
                        if (clanMatcher.find()) {
                            shipOwnerClan = RegexUtils.substr(
                                ownerName, clanMatcher, CLAN_GROUP);
                            ownerName = RegexUtils.substr(
                                ownerName, 0, 
                                ownerName.length() - (shipOwnerClan.length() + 2));
                        }
                        
                        FleetScanShip ship = new FleetScanShip(shipId, shipName, shipTl, 
                            ownerName, shipOwnerClan, quadrant, x, y);
                        ships.add(ship);
                    }
                }
            }
        }
        
        if (fleetName.equals("") || owner.equals("") || ships.isEmpty()) { //$NON-NLS-1$ //$NON-NLS-2$
            throw new ParseException(MSG.fleetScanParserInvalid);
        }
        
        return new FleetScan(sens, fleetName, owner, ownerClan, fleetTag, ships, 
            quadrant, x, y, metaData);
    }
}