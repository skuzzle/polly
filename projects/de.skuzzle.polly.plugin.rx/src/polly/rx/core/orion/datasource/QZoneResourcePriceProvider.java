package polly.rx.core.orion.datasource;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import polly.rx.core.orion.ResourcePriceProvider;
import polly.rx.core.orion.model.DefaultProduction;
import polly.rx.core.orion.model.Production;
import polly.rx.entities.RxRessource;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.io.WebUtils;


public class QZoneResourcePriceProvider implements ResourcePriceProvider {
    
    // TEST
    public static void main(String[] args) {
        System.out.println(new QZoneResourcePriceProvider().getAllPrices());
    }
    
    
    
    private static Map<RxRessource, Float> zeroMap() {
        final Map<RxRessource, Float> result = new EnumMap<>(RxRessource.class);
        for (final RxRessource ress : RxRessource.values()) {
            result.put(ress, 0.f);
        }
        result.put(RxRessource.CR, 1.f);
        return result;
    }
    
    
    
    private final static String API_URL = "http://qzone.servebbs.net/pollyPriceDaily.php"; //$NON-NLS-1$
    private Date refreshTime;
    
    
    
    public QZoneResourcePriceProvider() {
        this.refreshTime = Time.currentTime();
    }
    
    
    
    @Override
    public Date getRefreshTime() {
        return this.refreshTime;
    }
    
    
    
    @Override
    public float getPrice(RxRessource resource) {
        return this.getPrice(resource, null);
    }

    
    
    @Override
    public float getPrice(RxRessource resource, Date time) {
        return this.safeRequestPrices(time).get(resource);
    }
    
    

    @Override
    public List<? extends Production> getAllPrices() {
        return this.getAllPrices(null);
    }

    
    
    @Override
    public List<? extends Production> getAllPrices(Date time) {
        final Map<RxRessource, Float> prices = this.safeRequestPrices(time);
        final List<DefaultProduction> result = new ArrayList<>(prices.size());
        
        for (final Entry<RxRessource, Float> e : prices.entrySet()) {
            result.add(new DefaultProduction(e.getKey(), e.getValue()));
        }
        
        return result;
    }

    
    
    private String formatDate(Date date) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
        return df.format(date);
    }
    
    
    
    private Map<RxRessource, Float> safeRequestPrices(Date date) {
        try {
            return this.requestPrices(date);
        } catch (IOException e) {
            return zeroMap();
        }
    }
    
    
    
    private Date parseTimeStamp(String stamp) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
        try {
            return df.parse(stamp);
        } catch (ParseException e) {
            return Time.currentTime();
        }
    }
    
    
    
    private Map<RxRessource, Float> requestPrices(Date date) throws IOException {
        final String requestUrl;
        final Map<RxRessource, Float> result = zeroMap();
        
        if (date != null) {
            requestUrl = API_URL + "?date=" + this.formatDate(date); //$NON-NLS-1$
        } else {
            requestUrl = API_URL;
        }
        final StringBuilder response = WebUtils.getString(requestUrl);
        
        int i = 0;
        final String rows[] = response.toString().split(System.lineSeparator());
        for (final String row : rows) {
            if (!this.interpretResponseLine(row, i++, result, date != null)) {
                return result;
            }
        }
        return result;
    }
    
    
    
    private boolean interpretResponseLine(String line, int lineNr, Map<RxRessource, Float> result, boolean dateGiven) {
        final String[] parts = line.split(";"); //$NON-NLS-1$
        
        if (lineNr == 0) {
            final int id = Integer.parseInt(parts[1]);
            if (id == 0) {
                return false;
            }
        } else if (lineNr == 3 && !dateGiven) {
            this.refreshTime = parseTimeStamp(parts[1]);
        } else if (lineNr > 4 && !dateGiven) {
            //
            final int ordinal = lineNr - 4;
            result.put(RxRessource.values()[ordinal], Float.parseFloat(parts[1]));
        } else if (lineNr > 1 && dateGiven) {
            // -2 would be Cr, so we start at nrg with -1
            final int ordinal = lineNr - 1;
            result.put(RxRessource.values()[ordinal], Float.parseFloat(parts[1]));
        }
        return true;
    }
}
