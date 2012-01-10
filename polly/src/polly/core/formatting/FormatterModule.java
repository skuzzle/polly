package polly.core.formatting;

import polly.configuration.PollyConfiguration;
import polly.core.AbstractModule;
import polly.core.ModuleLoader;
import polly.core.annotation.Module;
import polly.core.annotation.Provide;
import polly.core.annotation.Require;


@Module(
    requires = 
        @Require(component = PollyConfiguration.class),
    provides = 
        @Provide(component = FormatManagerImpl.class)
    )
public class FormatterModule extends AbstractModule {


    public FormatterModule(ModuleLoader loader) {
        super("MODULE_FORMATTER", loader, true);
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
