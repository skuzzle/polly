package de.skuzzle.polly.sdk.httpv2.html;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.app.FieldMethodizer;

import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;

@Module
public class EscapeProvider extends AbstractProvider {

    public EscapeProvider(ModuleLoader loader) {
        super("ESCAPE_PROVIDER", loader, false); //$NON-NLS-1$
    }

    
    
    @Override
    public void setup() throws SetupException {
        // HACK: this is a hack to expose html escaping from apache
        //       commons to all plugins with avoiding a new dependency 
        HTMLTools.UTIL = new HTMLTools.HTMLToolsUtil() {
            @Override
            public String escape(String s) {
                return StringEscapeUtils.escapeHtml(s);
            }
            
            @Override
            public String unsecape(String s) {
                return StringEscapeUtils.unescapeHtml(s);
            }
            
            @Override
            public void gainFieldAccess(Map<String, Object> targetContext,
                    Class<?> container, String key) {
                
                targetContext.put(key, new FieldMethodizer(container.getName()));
            }
        };
    }
}
