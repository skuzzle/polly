package polly.rx.core.orion;

import java.util.List;

import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.PortalType;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;

public class PortalProviderDecorator implements PortalProvider {

    private final PortalProvider wrapped;



    public PortalProviderDecorator(PortalProvider wrapped) {
        this.wrapped = wrapped;
    }



    @Override
    public List<? extends Portal> getPortals(Sector sector) {
        return this.wrapped.getPortals(sector);
    }



    public List<? extends Portal> getPortals(Sector sector, PortalType type) {
        return this.wrapped.getPortals(sector, type);
    }



    @Override
    public Portal getPersonalPortal(String ownerName) {
        return this.wrapped.getPersonalPortal(ownerName);
    }



    @Override
    public Portal getClanPortal(String nameOrTag) {
        return this.wrapped.getClanPortal(nameOrTag);
    }



    @Override
    public List<? extends Portal> getPortals(Quadrant quadrant, PortalType type) {
        return this.wrapped.getPortals(quadrant, type);
    }

}
