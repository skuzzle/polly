package polly.rx.core.orion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
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
    private class WLSWormHole {
        public int id;
        public String von_quadrant;
        public int von_x;
        public int von_y;
        public String nach_quadrant;
        public int nach_x;
        public int nach_y;
        public String name;
        public String geladen;
        public String entladung;
        public boolean expired;
        public Date date;
    }
    
    
    
    
    private final Map<String, WLSQuadrant> quadrants;
    private final Map<String, List<WLSWormHole>> quadHoleCache;

    
    
    public WLSWormholeProvider() {
        this.quadrants = new HashMap<>();
        this.quadHoleCache = new HashMap<>();
        
        this.findQuadrants(this.quadrants);
        this.findAllWormholes(this.quadHoleCache);
    }
    
    
    
    private String performRequest(String query) {
        try {
            final URL url = new URL(BASE_URL + query);
            
            try (final BufferedReader r = new BufferedReader(
                    new InputStreamReader(url.openStream()))) {
                final StringBuilder b = new StringBuilder();
                String line = null;
                while ((line = r.readLine()) != null) {
                    b.append(line).append("\n"); //$NON-NLS-1$
                }
                
                // remove comments
                final String result = 
                        COMMENT.matcher(b.toString()).replaceAll(""); //$NON-NLS-1$
                return result;
            }
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
    
    
    
    private void findAllWormholes(Map<String, List<WLSWormHole>> r) {
        r.clear();
        final String QUERY = "v/0/alle/json/"; //$NON-NLS-1$
        final String result = this.performRequest(QUERY);
        if (result == "") { //$NON-NLS-1$
            return;
        }
        final Gson gson = new GsonBuilder().setDateFormat(API_DATE_FORMAT).create();
        final WLSWormHole[] holes = gson.fromJson(result, WLSWormHole[].class);
        
        for (final WLSWormHole hole : holes) {
            List<WLSWormHole> list = r.get(hole.von_quadrant);
            if (list == null) {
                list = new ArrayList<>();
                r.put(hole.von_quadrant, list);
            }
            list.add(hole);
        }
    }
    
    
    
    private Wormhole convert(Sector source, WLSWormHole hole, QuadrantProvider quads) {
        final Sector target = quads.getQuadrant(hole.nach_quadrant).getSector(
                hole.nach_x, hole.nach_y);
        final Wormhole result = new Wormhole();
        result.setSource(source);
        result.setName(hole.name);
        result.setRequiresLoad(LoadRequired.parse(hole.geladen));
        result.setDate(hole.date);
        final Matcher m = UNLOAD_PATTERN.matcher(hole.entladung); 
        if (m.matches()) {
            result.setMinUnload(RegexUtils.subint(hole.entladung, m, 1));
            result.setMaxUnload(RegexUtils.subint(hole.entladung, m, 2));
        }
        result.setTarget(target);
        
        return result;
    }

    
    
    @Override
    public List<Wormhole> getWormholes(Quadrant quadrant, QuadrantProvider quads) {
        final List<WLSWormHole> wlsHoles = this.quadHoleCache.get(quadrant.getName());
        final List<Wormhole> wormholes = new ArrayList<>(wlsHoles.size());
        
        for (final WLSWormHole hole : wlsHoles) {
            final Sector source = quadrant.getSector(hole.von_x, hole.von_y);
            wormholes.add(this.convert(source, hole, quads));
        }
        return wormholes;
    }
    
    
    
    @Override
    public List<Wormhole> getWormholes(Sector sector, QuadrantProvider quads) {
        final String quadName = sector.getQuadName();
        final Quadrant quadrant = quads.getQuadrant(sector);
        final List<WLSWormHole> wlsHoles = this.quadHoleCache.get(quadName);
        final List<Wormhole> wormholes = new ArrayList<>(wlsHoles.size());
        
        for (final WLSWormHole hole : wlsHoles) {
            if (hole.von_x == sector.getX() && hole.von_y == sector.getY()) {
                final Sector source = quadrant.getSector(hole.von_x, hole.von_y);
                wormholes.add(this.convert(source, hole, quads));
            }
        }
        return wormholes;
    }

}
