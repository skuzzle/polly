package core;

import de.skuzzle.polly.sdk.FormatManager;


public class PatternTVProgramFormatter implements TVProgramFormatter {
    
    private String pattern;
    private boolean escape;
    
    
    
    public PatternTVProgramFormatter(String pattern, boolean escape) {
        this.pattern = escape ? this.escape(pattern) : pattern;
    }
    
    

    @Override
    public String format(TVProgram input, FormatManager formatter) {
        return this.parse(input, formatter);
    }

    /*
     * %c = channel
     * %n = name
     * %d = duration
     * %t = time
     * %g = genre
     */
    
    private String escape(String other) {
        if (!this.escape) {
            return other;
        }
        return other.replaceAll("%c%|%d%|%f%|%t%|%g%", "");
    }

    
    private String parse(TVProgram program, FormatManager formatter) {
        String tmp = this.pattern.replaceAll("%c%", this.escape(program.getChannel()));
        tmp = tmp.replaceAll("%n%", this.escape(program.getName()));
        tmp = tmp.replaceAll("%d%", this.escape(program.getDuration()));
        tmp = tmp.replaceAll("%t%", formatter.formatDate(program.getTime()));
        tmp = tmp.replaceAll("%lg%", this.escape(program.getGenre()));
        return tmp;
    }
}

