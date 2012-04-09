package core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;


public class Html2Text {
    
    private StringBuilder s;
    private Reader reader;

    
    public Html2Text(InputStream input) {
        this.reader = new InputStreamReader(input);
    }
    
    
    
    public Html2Text(String string) {
        this.reader = new StringReader(string);
    }
    

    
    public void parse() throws IOException {
        s = new StringBuilder();
        ParserDelegator delegator = new ParserDelegator();
        // the third parameter is TRUE to ignore charset directive
        delegator.parse(this.reader, new ParserCallback() {
            public void handleText(char[] data, int pos) {
                s.append(data);
            };
            
        }, Boolean.TRUE);
    }
    
    

    public String getText() {
        return s.toString();
    }
}
