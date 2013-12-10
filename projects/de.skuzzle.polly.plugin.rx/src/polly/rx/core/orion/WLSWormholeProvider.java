package polly.rx.core.orion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import polly.rx.core.orion.model.LoadRequired;
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
    
    
    private WeakReference<List<WLSQuadrant>> quadrants;
    private final Map<String, List<WLSWormHole>> holeCache;
    
    
    public WLSWormholeProvider() {
        this.holeCache = new HashMap<>();
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
    
    
    
    private List<WLSQuadrant> getQuadrants() {
        synchronized (this) {
            if (this.quadrants == null) {
                final String QUERY = "quadranten/json"; //$NON-NLS-1$
                final String result = this.performRequest(QUERY);
                if (result.equals("")) { //$NON-NLS-1$
                    return Collections.emptyList();
                }
                final Gson gson = new GsonBuilder().create();
                final WLSQuadrant[] quads = gson.fromJson(result, WLSQuadrant[].class);
                this.quadrants = new WeakReference<List<WLSQuadrant>>(Arrays.asList(quads));
            }
            return this.quadrants.get();
        }
    }
    

    
    private WLSQuadrant getQuadrantByName(String name) {
        for (final WLSQuadrant quad : this.getQuadrants()) {
            if (quad.name.equalsIgnoreCase(name)) {
                return quad;
            }
        }
        return null;
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
    
    
    
    private String createCacheKey(Sector sector) {
        return sector.getQuadName() + "_" +  //$NON-NLS-1$
                sector.getX() + "_" + sector.getY(); //$NON-NLS-1$
    }
    
    
    
    private List<WLSWormHole> cache(Sector sector, WLSQuadrant quad, 
            boolean forceUpdate) {
        synchronized (this.holeCache) {
            final String cacheKey = this.createCacheKey(sector);
            List<WLSWormHole> cached = this.holeCache.get(cacheKey);
            if (cached == null || forceUpdate) {
                cached = this.findWormholesFrom(quad, sector.getX(), sector.getY());
                this.holeCache.put(cacheKey, cached);
            }
            return cached;
        }
    }
    
    
    
    private List<WLSWormHole> findWormholesFrom(WLSQuadrant quad, int x, int y) {
        final String direction = "v"; //$NON-NLS-1$
        final String QUERY = String.format("%s/%s/%s/json", direction,  //$NON-NLS-1$
                quad.id, quad.url);
        final String result = this.performRequest(QUERY);
        if (result == "") { //$NON-NLS-1$
            return Collections.emptyList();
        }
        final Gson gson = new GsonBuilder().setDateFormat(API_DATE_FORMAT).create();
        final WLSWormHole[] holes = gson.fromJson(result, WLSWormHole[].class);
        return Arrays.asList(holes);
    }
    
    
    
    @Override
    public List<Wormhole> getWormholes(Sector sector, 
            QuadrantProvider quads) {
        final WLSQuadrant quad = this.getQuadrantByName(sector.getQuadName());
        if (quad == null) {
            return Collections.emptyList();
        }

        final List<WLSWormHole> wlsHoles = this.cache(sector, quad, false);
        final List<Wormhole> wormholes = new ArrayList<>(wlsHoles.size());
        
        for (final WLSWormHole hole : wlsHoles) {
            if (hole.von_x == sector.getX() && hole.von_y == sector.getY()) {
                wormholes.add(this.convert(sector, hole, quads));
            }
        }
        return wormholes;
    }

}
