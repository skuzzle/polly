package polly.core.formatting;

import polly.configuration.PollyConfiguration;
import polly.core.AbstractModule;
import polly.core.ModuleLoader;


public class FormatterModule extends AbstractModule {


    public FormatterModule(ModuleLoader loader) {
        super("MODULE_FORMATTER", loader, true);
        this.requireBeforeSetup(PollyConfiguration.class);
        this.willProvideDuringSetup(FormatManagerImpl.class);
    }

    
    
    @Override
    public void setup() {
        PollyConfiguration config = this.requireNow(PollyConfiguration.class);
        
        FormatManagerImpl formatter = new FormatManagerImpl(
            config.getDateFormatString(), 
            config.getNumberFormatString());
        
        this.provideComponent(formatter);
    }

}
