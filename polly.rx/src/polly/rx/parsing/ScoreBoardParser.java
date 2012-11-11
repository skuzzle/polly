package polly.rx.parsing;

import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.tools.iterators.ArrayIterator;

import polly.rx.entities.ScoreBoardEntry;


public class ScoreBoardParser {

    public Collection<ScoreBoardEntry> parse(String paste) throws ParseException {
        String[] lines = paste.split("[\n\r]+");
        
        ArrayIterator<String> it = ArrayIterator.get(lines);
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
                throw new ParseException();
            }
            int rank = Integer.parseInt(parts[0]);
            String venad = parts[1];
            int points = Integer.parseInt(parts[2]);
            result.add(new ScoreBoardEntry(venad, rank, points));
        }
        return result;
    }
}