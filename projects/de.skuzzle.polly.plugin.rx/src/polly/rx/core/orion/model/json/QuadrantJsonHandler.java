package polly.rx.core.orion.model.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import polly.rx.core.orion.model.DefaultQuadrant;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import de.skuzzle.polly.tools.Check;

class QuadrantJsonHandler extends AbstractJsonHandler implements 
        JsonDeserializer<Quadrant>, JsonSerializer<Quadrant>{

    private final static String NAME = "name"; //$NON-NLS-1$
    private final static String MAX_X = "maxX"; //$NON-NLS-1$
    private final static String MAX_Y = "maxY"; //$NON-NLS-1$
    private final static String SECTORS = "sectors"; //$NON-NLS-1$
    
    final static Type QUADRANT_TYPE = new TypeToken<Quadrant>() {}.getType();
    
    
    
    private final SectorJsonHandler sectorHandler;



    public QuadrantJsonHandler(SectorJsonHandler sectorHandler) {
        Check.objects(sectorHandler).notNull();
        this.sectorHandler = sectorHandler;
    }



    @Override
    public Quadrant deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        final JsonObject obj = json.getAsJsonObject();
        final String name = this.getMemberOrThrow(obj, NAME).getAsString(); 
        final int maxX = this.getMemberOrThrow(obj, MAX_X).getAsInt(); 
        final int maxY = this.getMemberOrThrow(obj, MAX_Y).getAsInt();
        
        final JsonArray sectors = this.getMemberOrDefault(obj, SECTORS, 
                new JsonArray()).getAsJsonArray();
        final Collection<Sector> sectorsC = new ArrayList<>(sectors.size());
        for (int i = 0; i < sectors.size(); ++i) {
            final JsonElement jSector = sectors.get(i);
            final Sector next = this.sectorHandler.deserialize(
                    jSector, SectorJsonHandler.SECTOR_TYPE, context);
            sectorsC.add(next);
        }
        
        return new DefaultQuadrant(name, maxX, maxY, sectorsC);
    }



    @Override
    public JsonElement serialize(Quadrant src, Type typeOfSrc,
            JsonSerializationContext context) {
        final JsonObject result = new JsonObject();
        result.addProperty(NAME, src.getName());
        result.addProperty(MAX_X, src.getMaxX()); 
        result.addProperty(MAX_Y, src.getMaxY());
        final JsonArray sectors = new JsonArray();
        for (final Sector s : src.getSectors()) {
            sectors.add(this.sectorHandler.serialize(s, 
                    SectorJsonHandler.SECTOR_TYPE, context));
        }
        result.add(SECTORS, sectors); 
        return result;
    }
}
