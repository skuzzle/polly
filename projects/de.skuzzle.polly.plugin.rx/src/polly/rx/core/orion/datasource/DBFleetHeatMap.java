package polly.rx.core.orion.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polly.rx.core.orion.FleetHeatMap;
import polly.rx.core.orion.QuadrantProvider;
import polly.rx.core.orion.model.Fleet;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.entities.DBHeatMapEntry;
import polly.rx.entities.DBSector;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.time.Time;

public class DBFleetHeatMap implements FleetHeatMap {

    private final PersistenceManagerV2 persistence;
    private final QuadrantProvider quadrantProvider;

    public DBFleetHeatMap(PersistenceManagerV2 persistence, QuadrantProvider quadProvider) {
        this.persistence = persistence;
        this.quadrantProvider = quadProvider;
    }

    @Override
    public int getTimes(String venadName, Sector sector) {
        return findEntries(venadName, sector).size();
    }

    @Override
    public Map<Quadrant, Map<Sector, Integer>> getUserHeatMaps(String venad) {
        final Map<Quadrant, Map<Sector, Integer>> result = new HashMap<>();
        try (final Read read = this.persistence.read()) {
            for (final Quadrant quad : this.quadrantProvider.getAllQuadrants()) {
                final Map<Sector, Integer> heatMap = getSectorHeatMap(venad, quad);
                if (!heatMap.isEmpty()) {
                    result.put(quad, heatMap);
                }
            }

        }
        return result;
    }

    @Override
    public Map<Sector, Integer> getSectorHeatMap(String venadName, Quadrant quadrant) {
        final Map<Sector, Integer> result = new HashMap<>();
        final List<DBHeatMapEntry> all;
        try (final Read read = this.persistence.read()) {
            all = read.findList(DBHeatMapEntry.class,
                    DBHeatMapEntry.BY_VENAD_AND_QUADRANT,
                    new Param(venadName, quadrant.getName()));
        }

        for (final DBHeatMapEntry entry : all) {
            result.compute(entry.getSector(), (s, i) -> i == null
                    ? 1
                    : i + 1);
        }
        return result;
    }

    @Override
    public void update(Fleet fleet) {
        try (final Write write = this.persistence.write()) {
            final Read read = write.read();

            final DBSector sector = read.findSingle(DBSector.class,
                    DBSector.QUERY_FIND_SECTOR,
                    new Param(fleet.getSector().getQuadName(),
                            fleet.getSector().getX(),
                            fleet.getSector().getY()));

            final DBHeatMapEntry newEntry = new DBHeatMapEntry();
            newEntry.setDate(Time.currentTime());
            newEntry.setOwnerVenadName(fleet.getOwnerName());
            newEntry.setSector(sector);
            write.single(newEntry);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    private List<DBHeatMapEntry> findEntries(String venadName, Sector s) {
        try (final Read read = this.persistence.read()) {
            final DBSector sector = read.findSingle(DBSector.class,
                DBSector.QUERY_FIND_SECTOR,
                new Param(s.getQuadName(), s.getX(), s.getY()));

            return read.findList(DBHeatMapEntry.class, DBHeatMapEntry.BY_VENAD_AND_SECTOR,
                    new Param(venadName, sector));
        }

    }

}
