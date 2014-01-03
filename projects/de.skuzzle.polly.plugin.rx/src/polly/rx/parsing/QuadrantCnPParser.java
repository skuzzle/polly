package polly.rx.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import polly.rx.core.orion.model.Production;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorType;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.Equatable;
import de.skuzzle.polly.tools.FileUtil;


public class QuadrantCnPParser {

    private static class CnPSector implements Sector {

        private final String quadName;
        private final int x;
        private final int y;
        private final SectorType type;
        private final Date date;
        
        public CnPSector(String quadName, int x, int y, SectorType type) {
            super();
            this.quadName = quadName;
            this.x = x;
            this.y = y;
            this.type = type;
            this.date = Time.currentTime();
        }

        
        @Override
        public Class<?> getEquivalenceClass() {
            return Sector.class;
        }

        @Override
        public boolean actualEquals(Equatable o) {
            final Sector other = (Sector) o;
            return this.quadName.equals(other.getQuadName()) && this.x == other.getX() && 
                    this.y == other.getY();
        }

        @Override
        public String getQuadName() {
            return this.quadName;
        }

        @Override
        public int getX() {
            return this.x;
        }

        @Override
        public int getY() {
            return this.y;
        }

        @Override
        public int getAttackerBonus() {
            return 0;
        }

        @Override
        public int getDefenderBonus() {
            return 0;
        }

        @Override
        public int getSectorGuardBonus() {
            return 0;
        }

        @Override
        public Date getDate() {
            return this.date;
        }

        @Override
        public SectorType getType() {
            return this.type;
        }

        @Override
        public Collection<? extends Production> getRessources() {
            return Collections.emptyList();
        }
        
        @Override
        public String toString() {
            return String.format("%s %d, %d", this.quadName, this.x, this.y); //$NON-NLS-1$
        }
    }
    
    
    
    private final static Pattern NUMBER = Pattern.compile("\\d+"); //$NON-NLS-1$
    
    // TEST
    public static void main(String[] args) throws IOException, ParseException {
        final InputStream s = QuadrantCnPParser.class
                .getResourceAsStream("quadpaste.txt"); //$NON-NLS-1$
        final String scan = FileUtil.readIntoString(s, "UTF-8"); //$NON-NLS-1$
        // System.out.println("'" + scan + "'");
        final Collection<Sector> sectors = parse(scan, "Bla"); //$NON-NLS-1$
        for (final Sector se : sectors) {
            System.out.println(se);
        }
    }
    
    
    
    public static Collection<Sector> parse(String input, String quadName) 
            throws ParseException {
        try (BufferedReader r = new BufferedReader(new StringReader(input))) {
            String line = null;
            final Collection<Sector> result = new ArrayList<>();
            while ((line = r.readLine()) != null) {
                final Matcher m = NUMBER.matcher(line);
                while (m.find()) {
                    final int x = RegexUtils.subint(line, m, 0);
                    m.find();
                    final int y = RegexUtils.subint(line, m, 0);
                    result.add(new CnPSector(quadName, x, y, SectorType.UNKNOWN));
                }
            }
            return result;
        } catch (IOException e) {
            throw new ParseException();
        }
    }
}
