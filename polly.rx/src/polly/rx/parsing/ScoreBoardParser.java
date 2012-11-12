package polly.rx.parsing;

import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.tools.iterators.ArrayIterator;

import polly.rx.entities.ScoreBoardEntry;


public class ScoreBoardParser {

    public static Collection<ScoreBoardEntry> parse(String paste) throws ParseException {
        if (paste == null) {
            throw new ParseException();
        }
        
        String[] lines = paste.split("[\n\r]+");
        
        ArrayIterator<String> it = ArrayIterator.get(lines);
        while (it.peekNext().equals("")) {
            it.next();
        }
        
        if (it.peekNext().equals("Umgebungsliste")) {
            it.next();
        }
        if (it.peekNext().startsWith("Rang")) {
            it.next();
        }
        
        Collection<ScoreBoardEntry> result = new ArrayList<ScoreBoardEntry>();
        while (it.hasNext()) {
            String line = it.next();
            
            String[] parts = line.split("\\s+");
            
            if (parts.length != 3) {
                continue;
            }
            int rank = Integer.parseInt(parts[0]);
            String venad = parts[1];
            String clan = "";
            int i = venad.indexOf("[");
            if (i != -1) {
                clan = venad.substring(i + 1, venad.length() - 1);
                venad = venad.substring(0, venad.length() - clan.length() - 2);
            }
            int points = Integer.parseInt(parts[2]);
            result.add(new ScoreBoardEntry(venad, clan, rank, points));
        }
        return result;
    }
}