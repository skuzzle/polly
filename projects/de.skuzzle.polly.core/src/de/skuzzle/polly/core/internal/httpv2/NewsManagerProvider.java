package de.skuzzle.polly.core.internal.httpv2;

import de.skuzzle.polly.core.internal.persistence.PersistenceManagerV2Impl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;
import de.skuzzle.polly.sdk.PersistenceManagerV2;

@Module(
        requires = {
            @Require(component = PersistenceManagerV2Impl.class),
        },
        provides = @Provide(component = NewsManager.class)
    )
public class NewsManagerProvider extends AbstractProvider {

    public NewsManagerProvider(ModuleLoader loader) {
        super("NEWS_MANAGER_PROVIDER", loader, false); //$NON-NLS-1$
    }

    
    
    @Override
    public void setup() throws SetupException {
        final PersistenceManagerV2 persistence = this.requireNow(
                PersistenceManagerV2Impl.class, true);
        persistence.registerEntity(NewsEntry.class);
        final NewsManager nm = new NewsManager(persistence);
        this.provideComponent(nm);
    }

}
