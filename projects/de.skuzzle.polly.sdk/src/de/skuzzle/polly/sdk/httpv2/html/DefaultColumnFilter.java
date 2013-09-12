package de.skuzzle.polly.sdk.httpv2.html;


public class DefaultColumnFilter implements HTMLColumnFilter {
    
    
    public final Acceptor TO_STRING_ACCEPTOR = new Acceptor() {

        @Override
        public boolean accept(String filter, Object cellValue) {
            final String comp = cellValue == null 
                ? "" : cellValue.toString().toLowerCase();
            return comp.matches(".*" + filter.toLowerCase() + ".*");
        }
    }; 
    
    
    
    @Override
    public Acceptor getAcceptor(int column) {
        return TO_STRING_ACCEPTOR;
    }
}
