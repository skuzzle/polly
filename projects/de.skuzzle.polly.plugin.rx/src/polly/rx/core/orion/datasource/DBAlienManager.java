package polly.rx.core.orion.datasource;

import java.util.Collections;
import java.util.List;

import polly.rx.core.orion.AlienManager;
import polly.rx.core.orion.OrionException;
import polly.rx.core.orion.model.AlienRace;
import polly.rx.core.orion.model.AlienSpawn;
import polly.rx.core.orion.model.DefaultAlienRace;
import polly.rx.core.orion.model.Sector;
import polly.rx.entities.DBAlienRace;
import polly.rx.entities.DBAlienSpawn;
import polly.rx.entities.DBSector;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;

public class DBAlienManager implements AlienManager {

    private final PersistenceManagerV2 persistence;



    public DBAlienManager(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
    }



    private DBAlienRace findRace(Read read, AlienRace r) {
        if (r instanceof DBAlienRace) {
            return (DBAlienRace) r;
        }
        return read.findSingle(DBAlienRace.class, DBAlienRace.FIND_RACE,
                new Param(r.getName(), r.getSubName(), r.isAggressive()));
    }



    private DBSector findSector(Read read, Sector s) {
        if (s instanceof DBSector) {
            return (DBSector) s;
        }
        return read.findSingle(DBSector.class, DBSector.QUERY_FIND_SECTOR,
                new Param(s.getQuadName(), s.getX(), s.getY()));
    }



    private DBAlienSpawn findSpawn(Read read, AlienSpawn spawn) {
        if (spawn instanceof DBAlienSpawn) {
            return (DBAlienSpawn) spawn;
        }
        final DBSector dbs = findSector(read, spawn.getSector());
        final DBAlienRace dbr = findRace(read, spawn.getRace());
        return read.findSingle(DBAlienSpawn.class, DBAlienSpawn.FIND_SPAWN, new Param(
                spawn.getName(), dbr, dbs));
    }



    @Override
    public AlienRace addRace(String name, String subName, boolean aggressive)
            throws OrionException {
        try (final Write w = this.persistence.write()) {
            final DBAlienRace check = this.findRace(w.read(), new DefaultAlienRace(name,
                    subName, aggressive));
            if (check != null) {
                return check;
            }
            final DBAlienRace race = new DBAlienRace(name, subName, aggressive);
            w.single(race);
            return race;
        } catch (DatabaseException e) {
            throw new OrionException(e);
        }
    }



    @Override
    public AlienRace getRaceById(int id) {
        return this.persistence.atomic().find(DBAlienRace.class, id);
    }



    @Override
    public AlienSpawn getSpawnById(int spawnId) {
        return this.persistence.atomic().find(DBAlienSpawn.class, spawnId);
    }



    @Override
    public AlienSpawn addSpawn(String name, AlienRace race, Sector sector)
            throws OrionException {
        try (final Write w = this.persistence.write()) {
            final Read read = w.read();

            final DBSector dbs = this.findSector(read, sector);
            if (dbs == null) {
                throw new OrionException(); // TODO: message
            }
            final DBAlienRace dbr = this.findRace(read, race);
            if (dbr == null) {
                throw new OrionException();
            }
            final DBAlienSpawn dbas = new DBAlienSpawn(name, dbr, dbs);
            w.single(dbas);
            return dbas;
        } catch (DatabaseException e) {
            throw new OrionException(e);
        }
    }



    @Override
    public List<DBAlienSpawn> getSpawnsByQuadrant(String quadName) {
        return this.persistence.atomic().findList(DBAlienSpawn.class,
                DBAlienSpawn.SPAWN_BY_QUADRANT, new Param(quadName));
    }



    @Override
    public List<DBAlienSpawn> getSpawnsBySector(Sector sector) {
        try (final Read r = this.persistence.read()) {
            final DBSector dbs = this.findSector(r, sector);
            if (dbs == null) {
                return Collections.emptyList();
            }
            return r.findList(DBAlienSpawn.class, DBAlienSpawn.SPAWN_BY_SECTOR,
                    new Param(dbs));
        }
    }



    @Override
    public void removeAlienSpawn(AlienSpawn spawn) throws OrionException {
        try (final Write w = this.persistence.write()) {
            final Read r = w.read();
            final DBAlienSpawn dbas = this.findSpawn(r, spawn);

            if (dbas != null) {
                w.remove(dbas);
            }
        } catch (DatabaseException e) {
            throw new OrionException(e);
        }
    }



    @Override
    public void removeRace(AlienRace race) throws OrionException {
        try (final Write w = this.persistence.write()) {
            final Read r = w.read();
            final DBAlienRace dbas = this.findRace(r, race);

            final List<DBAlienSpawn> spawns = r.findList(DBAlienSpawn.class,
                    DBAlienSpawn.FIND_SPAWN_BY_RACE, new Param(dbas));

            if (!spawns.isEmpty()) {
                throw new OrionException("Please delete spawns for this race first");
            }

            if (dbas != null) {
                w.remove(dbas);
            }
        } catch (DatabaseException e) {
            throw new OrionException(e);
        }
    }



    @Override
    public List<DBAlienRace> getAllRaces() {
        return this.persistence.atomic().findList(DBAlienRace.class,
                DBAlienRace.ALL_RACES);
    }



    @Override
    public List<DBAlienSpawn> getAllSpawns() {
        return this.persistence.atomic().findList(DBAlienSpawn.class,
                DBAlienSpawn.ALL_SPAWNS);
    }
}
