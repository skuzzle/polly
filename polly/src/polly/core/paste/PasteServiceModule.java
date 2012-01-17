package polly.core.paste;

import de.skuzzle.polly.sdk.exceptions.PasteException;
import polly.core.AbstractModule;
import polly.core.ModuleLoader;
import polly.core.SetupException;
import polly.core.annotation.Module;
import polly.core.annotation.Provide;
import polly.core.paste.services.GBPasteService;
import polly.core.paste.services.NoPastePasteService;
import polly.core.paste.services.PHCNPasteService;
import polly.core.paste.services.PasteBinPasteService;

@Module(
    provides = @Provide(component = PasteServiceManagerImpl.class)
)
public class PasteServiceModule extends AbstractModule {

    public PasteServiceModule(ModuleLoader loader) {
        super("PASTE_MODULE", loader, true);
    }

    
    
    @Override
    public void setup() throws SetupException {
        PasteServiceManagerImpl pasteManager = new PasteServiceManagerImpl();
        
        try {
            pasteManager.addService(new GBPasteService());
            pasteManager.addService(new NoPastePasteService());
            pasteManager.addService(new PasteBinPasteService());
            pasteManager.addService(new PHCNPasteService());
            
            this.provideComponent(pasteManager);
        } catch (PasteException e) {
            throw new SetupException(e);
        }
    }
}
