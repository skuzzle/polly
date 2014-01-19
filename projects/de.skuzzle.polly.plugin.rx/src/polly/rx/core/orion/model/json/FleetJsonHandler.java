package polly.rx.core.orion.model.json;

import java.lang.reflect.Type;

import polly.rx.core.orion.model.Fleet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;


class FleetJsonHandler implements JsonSerializer<Fleet> {

    private final static String REVORIX_ID = "fleetId"; //$NON-NLS-1$
    private final static String FLEET_NAME = "fleetName"; //$NON-NLS-1$
    private final static String OWNER_NAME = "ownerName"; //$NON-NLS-1$
    private final static String OWNER_CLAN = "ownerClan"; //$NON-NLS-1$

    final static Type FLEET_TYPE = new TypeToken<Fleet>() {}.getType();
    
    
    
    @Override
    public JsonElement serialize(Fleet src, Type typeOfSrc,
            JsonSerializationContext context) {
        final JsonObject result = new JsonObject();
        result.addProperty(REVORIX_ID, src.getRevorixId());
        result.addProperty(FLEET_NAME, src.getName());
        result.addProperty(OWNER_NAME, src.getOwnerName());
        result.addProperty(OWNER_CLAN, src.getOwnerClan());
        return result;
    }

}
