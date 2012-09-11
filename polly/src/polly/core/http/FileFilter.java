package polly.core.http;

import java.io.IOException;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

public class FileFilter extends Filter {

    private String prefix;



    public FileFilter(String prefix) {
        this.prefix = prefix;
    }
    
    
    
    @Override
    public String description() {
        return "Filters file requests from the action response handler";
    }
    
    

    @Override
    public void doFilter(HttpExchange t, Chain chain) throws IOException {
        String uri = t.getRequestURI().toString();
        if (!uri.startsWith(this.prefix)) {
            chain.doFilter(t);
        }
    }

}
