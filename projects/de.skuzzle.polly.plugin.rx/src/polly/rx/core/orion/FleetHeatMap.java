package polly.rx.core.orion;

import java.util.Map;

import polly.rx.core.orion.model.Fleet;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;

public interface FleetHeatMap {

    int getTimes(String venadName, Sector sector);

    void update(Fleet fleet);

    Map<Sector, Integer> getSectorHeatMap(String venadName, Quadrant quadrant);

    Map<Quadrant, Map<Sector, Integer>> getUserHeatMaps(String venad);
}
