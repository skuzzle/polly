package polly.rx.parsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import de.skuzzle.polly.tools.iterators.ArrayIterator;

import polly.rx.entities.ScoreBoardEntry;


public class ScoreBoardParser {

    public static Collection<ScoreBoardEntry> parse(String paste, Date date) 
                throws ParseException {
        if (paste == null) {
            throw new ParseException();
        }
        
        String[] lines = paste.split("[\n\r]+"); //$NON-NLS-1$
        
        ArrayIterator<String> it = ArrayIterator.get(lines);
        while (it.peekNext().equals("")) { //$NON-NLS-1$
            it.next();
        }
        
        if (it.peekNext().equals("Umgebungsliste")) { //$NON-NLS-1$
            it.next();
        }
        if (it.peekNext().startsWith("Rang")) { //$NON-NLS-1$
            it.next();
        }
        
        Collection<ScoreBoardEntry> result = new ArrayList<ScoreBoardEntry>();
        while (it.hasNext()) {
            String line = it.next();
            
            String[] parts = line.split("\\s+"); //$NON-NLS-1$
            
            if (parts.length != 3) {
                continue;
            }
            int rank = Integer.parseInt(parts[0]);
            String venad = parts[1];
            String clan = ""; //$NON-NLS-1$
            int i = venad.indexOf("["); //$NON-NLS-1$
            if (i != -1) {
                clan = venad.substring(i + 1, venad.length() - 1);
                venad = venad.substring(0, venad.length() - clan.length() - 2);
            }
            int points = Integer.parseInt(parts[2]);
            result.add(new ScoreBoardEntry(venad, clan, rank, points, date));
        }
        return result;
    }
}