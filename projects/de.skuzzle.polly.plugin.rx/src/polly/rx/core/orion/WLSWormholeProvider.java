package polly.rx.core.orion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import polly.rx.core.orion.model.LoadRequired;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.Wormhole;
import polly.rx.parsing.RegexUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.skuzzle.polly.tools.io.WebUtils;


public class WLSWormholeProvider implements WormholeProvider {
    
    private final static String BASE_URL = "http://wls.nullpointer.at/"; //$NON-NLS-1$
    private final static String API_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"; //$NON-NLS-1$
    private final static Pattern UNLOAD_PATTERN = Pattern.compile("(\\d+)-(\\d+) Min"); //$NON-NLS-1$
    private final static Pattern COMMENT = Pattern.compile("<!--.*-->"); //$NON-NLS-1$
    
    
    
    @SuppressWarnings("unused")
    private class WLSQuadrant {
        public int id;
        public String name;
        public String url;
    }
    
    
    
    @SuppressWarnings("unused")
    public static class WLSWormhole implements Wormhole {
        
        /* filled by json deserialization */
        private int id;
        private String von_quadrant;
        private int von_x;
        private int von_y;
        private String nach_quadrant;
        private int nach_x;
        private int nach_y;
        private String name;
        private String geladen;
        private String entladung;
        private boolean expired;
        private Date date;
        
        private transient int minUnload;
        private transient int maxUnload;
        private transient LoadRequired loadRequired;
        private transient Sector source;
        private transient Sector target;
        
        @Override
        public String getName() {
            return this.name;
        }
        @Override
        public Date getDate() {
            return date;
        }
        @Override
        public int getMinUnload() {
            return this.minUnload;
        }
        @Override
        public int getMaxUnload() {
            return this.maxUnload;
        }
        @Override
        public Sector getTarget() {
            return this.target;
        }
        @Override
        public Sector getSource() {
            return this.source;
        }
        @Override
        public LoadRequired requiresLoad() {
            return this.loadRequired;
        }
        @Override
        public String toString() {
            return String.format("%s - von: %s %d,%d nach: %s %d, %d. Entladung: %d-%d",  //$NON-NLS-1$
                    this.name, 
                    this.von_quadrant, this.von_x, this.von_y, 
                    this.nach_quadrant, this.nach_x, this.nach_y, 
                    this.minUnload, this.maxUnload);
        }
    }
    
    
    
    
    private final Map<String, WLSQuadrant> quadrants;
    private final Map<String, List<Wormhole>> quadHoleCache;

    
    
    public WLSWormholeProvider() {
        this.quadrants = new HashMap<>();
        this.quadHoleCache = new HashMap<>();
        
        this.findQuadrants(this.quadrants);
    }
    
    
    
    private String performRequest(String query) {
        try {
            final String response = WebUtils.getString(BASE_URL + query).toString();
            final String result = 
                    COMMENT.matcher(response.toString()).replaceAll(""); //$NON-NLS-1$
            return result;
        } catch (IOException e) {
            return ""; //$NON-NLS-1$
        }
    }
    
    
    
    private void findQuadrants(Map<String, WLSQuadrant> r) {
        r.clear();
        final String QUERY = "quadranten/json"; //$NON-NLS-1$
        final String result = this.performRequest(QUERY);
        if (result.equals("")) { //$NON-NLS-1$
            return;
        }
        final Gson gson = new GsonBuilder().create();
        final WLSQuadrant[] quads = gson.fromJson(result, WLSQuadrant[].class);
        for (WLSQuadrant quad : quads) {
            r.put(quad.name, quad);
        }
    }
    
    
    
    private synchronized void refreshWormholes(
            Map<String, List<Wormhole>> r, QuadrantProvider quads) {
        r.clear();
        final String QUERY = "v/0/alle/json/"; //$NON-NLS-1$
        final String result = this.performRequest(QUERY);
        if (result == "") { //$NON-NLS-1$
            return;
        }
        final Gson gson = new GsonBuilder().setDateFormat(API_DATE_FORMAT).create();
        final WLSWormhole[] holes = gson.fromJson(result, WLSWormhole[].class);
        
        for (final WLSWormhole hole : holes) {
            final Quadrant sourceQuadrant = quads.getQuadrant(hole.von_quadrant);
            final Quadrant targetQuadrant = quads.getQuadrant(hole.nach_quadrant);
            
            hole.source = sourceQuadrant.getSector(hole.von_x, hole.von_y);
            hole.target = targetQuadrant.getSector(hole.nach_x, hole.nach_y);
            hole.loadRequired = LoadRequired.parse(hole.geladen);
            final Matcher m = UNLOAD_PATTERN.matcher(hole.entladung); 
            if (m.matches()) {
                hole.minUnload = RegexUtils.subint(hole.entladung, m, 1);
                hole.maxUnload = RegexUtils.subint(hole.entladung, m, 2);
            }
            
            List<Wormhole> list = r.get(hole.von_quadrant);
            if (list == null) {
                list = new ArrayList<>();
                r.put(hole.von_quadrant, list);
            }
            list.add(hole);
        }
    }
    
    
    
    @Override
    public List<Wormhole> getWormholes(Quadrant quadrant, QuadrantProvider quads) {
        if (this.quadHoleCache.isEmpty()) {
            this.refreshWormholes(this.quadHoleCache, quads);
        }
        final List<Wormhole> wormholes = this.quadHoleCache.get(quadrant.getName());
        if (wormholes == null) {
            return Collections.emptyList();
        }
        return wormholes;
    }
    
    
    
    @Override
    public List<Wormhole> getWormholes(Sector sector, QuadrantProvider quads) {
        if (this.quadHoleCache.isEmpty()) {
            this.refreshWormholes(this.quadHoleCache, quads);
        }
        final String quadName = sector.getQuadName();
        final List<Wormhole> wlsHoles = this.quadHoleCache.get(quadName);
        if (wlsHoles == null) {
            return Collections.emptyList();
        }
        final List<Wormhole> result = new ArrayList<>(wlsHoles.size());
        for (final Wormhole hole : wlsHoles) {
            if (hole.getSource().equals(sector)) {
                result.add(hole);
            }
        }
        return result;
    }

}
