package polly.mud.connection;


public class MudMessageEvent extends MudEvent {

    private final String message;
    private final static String ALLOWED = ",;.#^?!$%/+-*(){}[]~";
    
    public MudMessageEvent(MudTCPConnection source, String message) {
        super(source);
        this.message = message;
    }
    
    
    
    public String getMessage() {
        return this.message;
    }
    
    
    
    public String strip() {
        final StringBuilder b = new StringBuilder(this.message.length());
        for (final char c : this.message.toCharArray()) {
            if (!Character.isAlphabetic(c) || 
                    !Character.isDigit(c) || 
                    !Character.isWhitespace(c) || 
                    !ALLOWED.contains("" + c)) {
                continue;
            }
            b.append(c);
        }
        return b.toString();
    }
}
