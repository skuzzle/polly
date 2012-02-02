package polly.core.paste;

import de.skuzzle.polly.sdk.exceptions.PasteException;
import polly.core.paste.services.GBPasteService;
import polly.core.paste.services.NoPastePasteService;
import polly.core.paste.services.PHCNPasteService;
import polly.core.paste.services.PasteBinPasteService;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Provide;;

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