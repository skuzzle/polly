package de.skuzzle.polly.sdk.httpv2.html;

import org.apache.commons.lang.StringEscapeUtils;

import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;

@Module
public class EscapeProvider extends AbstractProvider {

    public EscapeProvider(ModuleLoader loader) {
        super("ESCAPE_PROVIDER", loader, false);
    }

    
    
    @Override
    public void setup() throws SetupException {
        Escape.ESCAPER = new Escape.EscapeUtil() {
            @Override
            public String escape(String s) {
                return StringEscapeUtils.escapeHtml(s);
            }
        };
    }
}
