package de.skuzzle.polly.sdk.paste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;


/**
 * TODO: comment
 * @author Simon
 * @since 0.7
 */
public abstract class AbstractPasteService implements PasteService {

    private String name;
    private Date lastPasteTime;
    
    
    public AbstractPasteService(String name) {
        this.name = name;
    }
    
    
    
    @Override
    public Date getLastPasteTime() {
        return this.lastPasteTime;
    }
    
    
   
    
    @Override
    public final String paste(String message) throws Exception {
        this.lastPasteTime = new Date();
        return this.doPaste(message);
    }
    
    
    
    public abstract String doPaste(String message) throws Exception;
    
    
    
    protected PostResult postRequest(URL url, Map<String, String> properties) 
            throws IOException {
        
        URLConnection connection = null;
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        
        try {
            connection = url.openConnection();
            connection.setDoOutput(true);
            StringWriter string = new StringWriter();
            PrintWriter request = new PrintWriter(string);
            
            Iterator<Map.Entry<String, String>> it = properties.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> property = it.next();
                
                request.print(URLEncoder.encode(property.getKey(), "UTF-8"));
                request.print("=");
                request.print(URLEncoder.encode(property.getValue(), "UTF-8"));
                if (it.hasNext()) {
                    request.print("&");
                }
            }
            
            writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(string.toString());
            writer.flush();
            writer.close();
            
            // fetch result
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            string = new StringWriter();
            PrintWriter result = new PrintWriter(string);
            
            String line = null;
            while ((line = reader.readLine()) != null) {
                result.println(line);
            }
            reader.close();
            
            return new PostResult(connection.getURL().toString(), string.toString());
            
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    
    
    @Override
    public String getName() {
        return this.name;
    }
    
}