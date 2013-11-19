package polly.rx.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanShip;
import de.skuzzle.polly.tools.FileUtil;

public class QFleetScanParser {

    // TEST
    public static void main(String[] args) throws IOException, ParseException {
        final InputStream s = QFleetScanParser.class
                .getResourceAsStream("flottenscan.txt"); //$NON-NLS-1$
        final String scan = FileUtil.readIntoString(s, "UTF-8"); //$NON-NLS-1$
        // System.out.println("'" + scan + "'");
        parseFleetScan(scan);
    }



    public final static FleetScan parseFleetScan(String paste) throws ParseException {
        try (final Scanner s = new Scanner(paste)) {
            s.useDelimiter(" X:\\d+"); //$NON-NLS-1$
            final String quad = s.next();
            s.useDelimiter("\\D+"); //$NON-NLS-1$
            final int x = s.nextInt();
            final int y = s.nextInt();
            final int sens = s.nextInt();

            while (s.hasNext() && !s.nextLine().equals("Flotten Daten")); //$NON-NLS-1$
            final String fleetName = s.nextLine();
            final String name = s.nextLine();
            final String venadName = VenadHelper.getName(name);
            final String clan = VenadHelper.getClan(name);

            while (s.hasNext() && !s.nextLine().equals("NameTechlevelBesitzer")); //$NON-NLS-1$

            final List<FleetScanShip> ships = new ArrayList<>();
            while (s.hasNext()) {
                final StringBuilder b = new StringBuilder();
                b.append(s.nextLine().trim());
                b.append(" "); //$NON-NLS-1$
                b.append(s.nextLine().trim());

                final String sl = b.toString();
                // openening '(' of ship id
                int i = sl.lastIndexOf('(');
                final String shipName = sl.substring(0, i).trim();

                i += 4; // skip "(ID:"
                int j = sl.lastIndexOf(')');
                final int rxId = Integer.parseInt(sl.substring(i, j));

                j += 1; // skip ")"
                final int tl = Integer.parseInt(sl.substring(j));

                final String owner = s.nextLine().trim();
                final String ownerVenadName = VenadHelper.getName(owner);
                final String ownerClan = VenadHelper.getClan(owner);

                ships.add(new FleetScanShip(rxId, shipName, tl, ownerVenadName,
                        ownerClan, quad, x, y));
            }

            return new FleetScan(sens, fleetName, venadName, 
                    clan, "", ships, quad, x, y, ""); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
