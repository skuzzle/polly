package polly.core.formatting;

import polly.configuration.PollyConfiguration;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;;

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
        FormatManagerImpl formatter = new FormatManagerImpl(config);
        this.provideComponent(formatter);
    }

}
