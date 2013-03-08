package de.skuzzle.polly.core.internal.paste;

import de.skuzzle.polly.core.internal.paste.services.GBPasteService;
import de.skuzzle.polly.core.internal.paste.services.NoPastePasteService;
import de.skuzzle.polly.core.internal.paste.services.PHCNPasteService;
import de.skuzzle.polly.sdk.exceptions.PasteException;
import polly.moduleloader.AbstractProvider;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Provide;;

@Module(
    provides = @Provide(component = PasteServiceManagerImpl.class)
)
public class PasteServiceManagerProvider extends AbstractProvider {

    public PasteServiceManagerProvider(ModuleLoader loader) {
        super("PASTE_MANAGER_PROVIDER", loader, true);
    }

    
    
    @Override
    public void setup() throws SetupException {
        PasteServiceManagerImpl pasteManager = new PasteServiceManagerImpl();
        
        try {
            pasteManager.addService(new GBPasteService());
            pasteManager.addService(new NoPastePasteService());
            //pasteManager.addService(new PasteBinPasteService()); due to captcha deactivated
            pasteManager.addService(new PHCNPasteService());
            
            this.provideComponent(pasteManager);
        } catch (PasteException e) {
            throw new SetupException(e);
        }
    }
}
