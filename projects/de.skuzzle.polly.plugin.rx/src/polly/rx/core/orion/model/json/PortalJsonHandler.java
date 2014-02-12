package polly.rx.core.orion.model.json;

import java.lang.reflect.Type;

import polly.rx.core.orion.model.Portal;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class PortalJsonHandler extends AbstractJsonHandler implements
        JsonSerializer<Portal> {

    private final static String OWNER = "ownerName"; //$NON-NLS-1$
    private final static String OWNER_CLAN = "ownerClan"; //$NON-NLS-1$
    private final static String DATE = "date"; //$NON-NLS-1$
    private final static String SECTOR = "sector"; //$NON-NLS-1$
    private final static String TYPE = "type"; //$NON-NLS-1$
    
    final static Type PORTAL_TYPE = new TypeToken<Portal>() {}.getType();
    
    
    private final SectorJsonHandler sectorHandler;



    public PortalJsonHandler(SectorJsonHandler sectorHandler) {
        this.sectorHandler = sectorHandler;
    }



    @Override
    public JsonElement serialize(Portal src, Type typeOfSrc,
            JsonSerializationContext context) {
        
        final JsonObject result = new JsonObject();
        result.addProperty(OWNER, src.getOwnerName());
        result.addProperty(OWNER_CLAN, src.getOwnerClan());
        result.addProperty(DATE, src.getDate().toString());
        
        final JsonElement sector = this.sectorHandler.serialize(src.getSector(), 
                SectorJsonHandler.SECTOR_TYPE, context);
        result.add(SECTOR, sector);
        result.addProperty(TYPE, src.getType().toString());
        
        return result;
    }

}
