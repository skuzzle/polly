package core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;



public class WikiReader {
    
    private final static Pattern PARAGRAPH = Pattern.compile("<p>.*</p>"); //$NON-NLS-1$
    
    
    public String getWikiLink(String article, String lang) {
        article = article.replaceAll(" ", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        return "http://" + lang + ".wikipedia.org/wiki/" + article; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    
    
    public String readFirstParagraph(String article, String lang) throws IOException {
        URL url = new URL(this.getWikiLink(article, lang));
        
        URLConnection connection = url.openConnection();
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(connection.getInputStream(), 
                    Charset.forName("UTF-8"))); //$NON-NLS-1$
            String line = null;
            StringBuilder b = new StringBuilder();
            while ((line = r.readLine()) != null) {
                b.append(line);
                b.append("\n"); //$NON-NLS-1$
                
                if (b.indexOf("</p>") != -1) { //$NON-NLS-1$
                    // we read everything we needed
                    break;
                }
            }
            
            String wikiText = b.toString();
            Matcher m = PARAGRAPH.matcher(wikiText);
            if (!m.find()) {
                throw new IOException("Error while reading the first paragraph"); //$NON-NLS-1$
            }
            String result = wikiText.substring(m.start(), m.end());
            return this.removeHtml(result);
        } finally {
            if (r != null) { try { r.close(); } catch (IOException e) {}; }
        }
    }
    
    
    
    public String removeHtml(String s) throws IOException {
        final StringBuilder b = new StringBuilder();
        ParserDelegator delegator = new ParserDelegator();
        // the third parameter is TRUE to ignore charset directive
        delegator.parse(new StringReader(s), new ParserCallback() {
            public void handleText(char[] data, int pos) {
                b.append(data);
            };
            
        }, true);
        return b.toString();
    }
    
    
}