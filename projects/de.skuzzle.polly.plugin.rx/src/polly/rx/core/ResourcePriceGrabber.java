package polly.rx.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import polly.rx.entities.RxRessource;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.NumberType;


public final class ResourcePriceGrabber {

    private final static String API_URL = "http://qzone.servebbs.net/pollyPriceDaily.php"; //$NON-NLS-1$

    
    private String ts;
    private Date lastRefreshDate;
    private Map<String, Types> prices;
    private int refreshCounter;
    private final int refreshThreshold;
    
    
    
    public ResourcePriceGrabber(int refreshThreshold) {
        this.refreshThreshold = refreshThreshold;
        this.refreshCounter = refreshThreshold;
    }
    
    
    
    public synchronized Map<String, Types> getPrices() {
        if (this.refreshCounter++ % this.refreshThreshold == 0) {
            try {
                this.refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this.prices;
    }
    
    
    
    public synchronized double[] getPricesAsArray() {
        final Map<String, Types> prices = this.getPrices();
        final double[] result = new double[RxRessource.values().length];
        result[0] = 1.0;
        for (final Entry<String, Types> e : prices.entrySet()) {
            final RxRessource ress = RxRessource.parseRessource(e.getKey());
            final double amount = ((NumberType) e.getValue()).getValue();
            result[ress.ordinal()] = amount;
        }
        return result;
    }
    
    
    
    public synchronized Date getlastRefreshDate() {
        return this.lastRefreshDate;
    }
    
    
    
    private void refresh() throws IOException {
        final URL url = new URL(API_URL);
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = null;
            int i = 0;
            final Map<String, Types> result = new LinkedHashMap<String, Types>();
            while ((line = r.readLine()) != null) {
                ++i;
                switch (i) {
                case 1: break;
                case 2: break;
                case 3: break;
                case 4:
                    if (line.equals(this.ts)) {
                        result.putAll(this.prices);
                        return;
                    }
                    this.ts = line;
                    final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
                    this.lastRefreshDate = df.parse(line.split(";")[1]); //$NON-NLS-1$
                case 5: break;
                default:
                    final String[] parts = line.split(";"); //$NON-NLS-1$
                    result.put(parts[0], new Types.NumberType(
                        Double.parseDouble(parts[1])));
                    break;
                }
            }
            this.prices = result;
            
        } catch (ParseException e) {
            throw new IOException(e);
        } finally {
            if (r != null) {
                r.close();
            }
        }
    }
}
