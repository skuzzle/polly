package polly.core.formatting;

import polly.configuration.PollyConfiguration;
import polly.util.AbstractPollyModule;
import polly.util.ModuleBlackboard;


public class FormatterModule extends AbstractPollyModule {

    private PollyConfiguration config;
    
    public FormatterModule(ModuleBlackboard initializer) {
        super("FORMATTER", initializer, true);
    }
    
    
    
    @Override
    public void require() {
        this.config = this.requireComponent(PollyConfiguration.class);
    }

    
    
    @Override
    public boolean doSetup() throws Exception {
        FormatManagerImpl formatter = new FormatManagerImpl(
            this.config.getDateFormatString(), 
            this.config.getNumberFormatString());
        
        this.provideComponent(FormatManagerImpl.class, formatter);
        return true;
    }

    
    @Override
    public void doRun() throws Exception {}

}
