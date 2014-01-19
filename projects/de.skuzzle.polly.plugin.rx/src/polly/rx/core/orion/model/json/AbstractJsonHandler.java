package polly.rx.core.orion.model.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.skuzzle.polly.tools.Check;


abstract class AbstractJsonHandler {

    protected JsonElement getMemberOrDefault(JsonObject obj, String memberName, 
            JsonElement defaultVal) {
        Check.objects(obj, memberName, defaultVal).notNull();
        final JsonElement member = obj.get(memberName);
        if (member == null) {
            return defaultVal;
        }
        return member;
    }
    
    
    
    protected JsonElement getMemberOrThrow(JsonObject obj, String memberName) 
            throws JsonParseException {
        Check.objects(obj, memberName).notNull();
        final JsonElement member = obj.get(memberName);
        if (member == null) {
            throw new JsonParseException("missing required member: " + memberName); //$NON-NLS-1$
        }
        return member;
    }
}