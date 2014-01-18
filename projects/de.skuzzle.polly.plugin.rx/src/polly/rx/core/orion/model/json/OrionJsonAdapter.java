package polly.rx.core.orion.model.json;

import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;



public final class OrionJsonAdapter {

    private final static ProductionJsonHandler PRODUCTION_HANDLER;
    private final static SectorJsonHandler SECTOR_HANDLER;
    private final static QuadrantJsonHandler QUADRANT_HANDLER;
    private final static Gson GSON;
    
    static {
        PRODUCTION_HANDLER = new ProductionJsonHandler();
        SECTOR_HANDLER  = new SectorJsonHandler(PRODUCTION_HANDLER);
        QUADRANT_HANDLER = new QuadrantJsonHandler(SECTOR_HANDLER);
        
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SectorJsonHandler.SECTOR_TYPE, SECTOR_HANDLER);
        builder.registerTypeAdapter(SectorJsonHandler.CLIENT_SECTOR_TYPE, SECTOR_HANDLER);
        builder.registerTypeAdapter(ProductionJsonHandler.PRODUCTION_TYPE, PRODUCTION_HANDLER);
        builder.registerTypeAdapter(QuadrantJsonHandler.QUADRANT_TYPE, QUADRANT_HANDLER);
        
        GSON = builder.create();
    }
    
    
    
    public static FromClientSector readSectorFromClient(String json) {
        return GSON.fromJson(json, SectorJsonHandler.CLIENT_SECTOR_TYPE);
        
    }
    
    
    
    public static HttpAnswer sectorAnswer(Sector s) {
        final String json = GSON.toJson(s, SectorJsonHandler.SECTOR_TYPE);
        return HttpAnswers.newStringAnswer(json);
    }
    
    
    
    public static HttpAnswer quadrantAnswer(Quadrant q) {
        final String json = GSON.toJson(q, QuadrantJsonHandler.QUADRANT_TYPE);
        return HttpAnswers.newStringAnswer(json);
    }
    
    
    
    private OrionJsonAdapter() {}
}
