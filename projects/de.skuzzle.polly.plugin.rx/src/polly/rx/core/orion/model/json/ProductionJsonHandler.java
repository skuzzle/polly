package polly.rx.core.orion.model.json;

import java.lang.reflect.Type;

import polly.rx.core.orion.model.DefaultProduction;
import polly.rx.core.orion.model.Production;
import polly.rx.entities.RxRessource;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;


class ProductionJsonHandler extends AbstractJsonHandler implements 
        JsonDeserializer<Production>, JsonSerializer<Production>{

    private final static String RESS_TYPE = "ress"; //$NON-NLS-1$
    private final static String RESS_ID = "ressId"; //$NON-NLS-1$
    private final static String RATE = "rate"; //$NON-NLS-1$
    
    
    final static Type PRODUCTION_TYPE = new TypeToken<Production>() {}.getType();

    
    
    @Override
    public Production deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        
        final JsonObject obj = json.getAsJsonObject();
        final int ressId = this.getMemberOrThrow(obj, RESS_ID).getAsInt() - 1; 
        final float rate = this.getMemberOrThrow(obj, RATE).getAsFloat(); 
        return new DefaultProduction(RxRessource.values()[ressId], rate);
    }

    
    
    @Override
    public JsonElement serialize(Production src, Type typeOfSrc,
            JsonSerializationContext context) {
        final JsonObject obj = new JsonObject();
        obj.addProperty(RESS_TYPE, src.getRess().toString());
        obj.addProperty(RATE, src.getRate());
        return obj;
    }

}
