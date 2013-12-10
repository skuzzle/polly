package polly.rx.core.orion;


public class WormholeProviderFactory {

    public static WormholeProvider getProvider() {
        return new WLSWormholeProvider();
    }
}