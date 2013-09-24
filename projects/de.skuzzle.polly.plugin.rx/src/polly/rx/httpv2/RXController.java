package polly.rx.httpv2;

import java.util.Map;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.FleetScanShip;
import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;


public class RXController extends PollyController {

    private final FleetDBManager fleetDb;
    
    public RXController(MyPolly myPolly, FleetDBManager fleetDb) {
        super(myPolly);
        this.fleetDb = fleetDb;
    }
    
    

    @Override
    protected Controller createInstance() {
        return new RXController(this.getMyPolly(), this.fleetDb);
    }
    
    
    
    @Get(value = "/pages/fleetScanPage", name = "Fleet Scans")
    @OnRegister({ WebinterfaceManager.ADD_MENU_ENTRY, "Revorix", "List all fleet scans",
        FleetDBManager.VIEW_FLEET_SCAN_PERMISSION })
    public HttpAnswer fleetScanPage() {
        return this.makeAnswer(this.createContext("http/view/fleetscans.overview.html"));
    }
    
    
    @Get(value = "/pages/fleetScanShips", name = "Scanned Ships")
    @OnRegister({ WebinterfaceManager.ADD_MENU_ENTRY, "Revorix", "List all scanned  ships",
        FleetDBManager.VIEW_FLEET_SCAN_PERMISSION })
    public HttpAnswer fleetScanShipsage() {
        return this.makeAnswer(this.createContext("http/view/scanships.overview.html"));
    }
    
    
    
    @Get("/pages/scanShipDetails")
    public HttpAnswer scanShipDetails(@Param("shipId") int id) 
            throws AlternativeAnswerException {
        this.requirePermissions(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
        final FleetScanShip ship = this.fleetDb.getShipByRevorixId(id);
        final Map<String, Object> c = this.createContext("http/view/scanship.details.html");
        c.put("ship", ship);
        return this.makeAnswer(c);
    }
}
