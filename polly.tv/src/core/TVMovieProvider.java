package core;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.exceptions.PluginException;


public class TVMovieProvider implements TVProgramProvider {
    
    private final static String API_URL = "http://flomi.dyn.pl:4567/";
    
    
    private interface NodeVisitor {
        public abstract boolean visit(Element e);
    }
    
    
    
    private interface ResultNodeVisitor<T> extends NodeVisitor {
        public T getResult();
    }
    

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, PluginException {
        TVMovieProvider tvmp = new TVMovieProvider(API_URL);
        
        System.out.println("Current");
        System.out.println(new PatternTVProgramFormatter("%c%: %n% (%t%, %d%)", false).format(tvmp.getCurrent("Pro 7"), new FormatManager() {
            
            @Override
            public String formatTimeSpan(long span) {
                // TODO Auto-generated method stub
                return null;
            }
            
            
            
            @Override
            public String formatNumber(double number) {
                // TODO Auto-generated method stub
                return null;
            }
            
            
            
            @Override
            public String formatDate(Date date) {
                return date.toString();
            }
        }));
        System.out.println("Next");
        System.out.println(tvmp.getNext("Pro 7"));
    }
    
    private Map<String, TVChannel> channelMap;
    private String apiUrl;
    
    public TVMovieProvider(String apiUrl) throws PluginException {
        this.apiUrl = apiUrl.endsWith("/") ? apiUrl : apiUrl + "/";
        try {
            this.initChannelMap();
        } catch (Exception e) {
            throw new PluginException("Unable to init channel map.", e);
        }
        
    }
    
    
    
    public void initChannelMap() throws Exception {
        this.channelMap = new HashMap<String, TVChannel>();

        Element c = this.formRequest("channels");
        this.iterateNodes(c, "channel", new NodeVisitor() {
            @Override
            public boolean visit(Element e) {
                TVChannel channel = new TVChannel(e);
                //System.out.println(channel.getName());
                TVMovieProvider.this.channelMap.put(
                        channel.getName().toLowerCase(), channel);
                return true;
            }
        });
    }
    
    
    
    private Element formRequest(String requestString) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document dom = db.parse(new URL(this.apiUrl + requestString).openStream());
        
        return dom.getDocumentElement();
    }
    
    

    @Override
    public TVProgram getProgram(final String channel, final Date date) {
        TVChannel c = this.channelMap.get(channel.toLowerCase());
        if (c == null) {
            return null;
        }
        
        try {
            Element e = this.formRequest(c.getRequestString());
            

            ResultNodeVisitor<TVProgram> v = new ResultNodeVisitor<TVProgram>() {
                private TVProgram result;
                
                @Override
                public boolean visit(Element e) {
                    this.result = new TVProgram(e, date, channel);
                    
                    if (this.result.getTime().getTime() >= date.getTime()) {
                        return false;
                    }
                    return true;
                }

                @Override
                public TVProgram getResult() {
                    return this.result;
                }
            };
            
            this.iterateNodes(e, "program", v);
            
            return v.getResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    
    @Override
    public TVProgram getCurrent(final String channel) {
        TVChannel c = this.channelMap.get(channel.toLowerCase());
        if (c == null) {
            return null;
        }
        
        try {
            Element e = this.formRequest(c.getRequestString());
            
            final Date date = new Date();
            ResultNodeVisitor<TVProgram> v = new ResultNodeVisitor<TVProgram>() {
                private TVProgram result;
                
                @Override
                public boolean visit(Element e) {
                    TVProgram program = new TVProgram(e, date, channel);
                    
                    if (program.getTime().getTime() >= date.getTime()) {
                        return false;
                    }
                    
                    this.result = program;
                    return true;
                }

                @Override
                public TVProgram getResult() {
                    return this.result;
                }
            };
            
            
            this.iterateNodes(e, "program", v);
            
            return v.getResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    
    @Override
    public TVProgram getNext(String channel) {
        return this.getProgram(channel, new Date());
    }
    
    

    @Override
    public boolean isChannelSupported(String channel) {
        return this.channelMap.containsKey(channel.toLowerCase());
    }

    
    
    private boolean iterateNodes(Element root, String tagName, NodeVisitor v) {
        NodeList nl = root.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); ++i) {
                if (!v.visit((Element) nl.item(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
