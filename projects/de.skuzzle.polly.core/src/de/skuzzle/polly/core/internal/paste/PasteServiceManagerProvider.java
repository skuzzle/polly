package de.skuzzle.polly.core.internal.paste;

import de.skuzzle.polly.core.internal.paste.services.GBPasteService;
import de.skuzzle.polly.core.internal.paste.services.NoPastePasteService;
import de.skuzzle.polly.core.internal.paste.services.PHCNPasteService;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.sdk.exceptions.PasteException;

@Module(
    provides = @Provide(component = PasteServiceManagerImpl.class)
)
public class PasteServiceManagerProvider extends AbstractProvider {

    public PasteServiceManagerProvider(ModuleLoader loader) {
        super("PASTE_MANAGER_PROVIDER", loader, true); //$NON-NLS-1$
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
