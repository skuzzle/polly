package core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class WikiReader {
    
    private final static Pattern PARAGRAPH = Pattern.compile("<p>.*</p>");
    
    
    public String readFirstParagraph(String article, String lang) throws IOException {
        String address = "http://" + lang + ".wikipedia.org/wiki/" + article;
        URL url = new URL(address);
        
        URLConnection connection = url.openConnection();
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(connection.getInputStream(), 
                    Charset.forName("UTF-8")));
            String line = null;
            StringBuilder b = new StringBuilder();
            while ((line = r.readLine()) != null) {
                b.append(line);
                b.append("\n");
            }
            
            String wikiText = b.toString();
            Matcher m = PARAGRAPH.matcher(wikiText);
            if (!m.find()) {
                throw new IOException("Error while reading the first paragraph");
            }
            String result = wikiText.substring(m.start(), m.end());
            return this.removeHtml(result);
        } finally {
            if (r != null) { try { r.close(); } catch (IOException e) {}; }
        }
    }
    
    
    
    private String removeHtml(String s) throws IOException {
        Html2Text h2t = new Html2Text(s);
        h2t.parse();
        return h2t.getText();
    }
    
    
}