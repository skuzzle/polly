package polly.rx.core.orion;

import java.util.List;

import polly.rx.core.orion.model.AlienRace;
import polly.rx.core.orion.model.AlienSpawn;
import polly.rx.core.orion.model.Sector;


public interface AlienManager {

    public AlienRace addRace(String name, String subName, boolean aggressive) 
            throws OrionException;
    
    public AlienSpawn addSpawn(String name, AlienRace race, Sector sector) throws OrionException;
    
    public List<? extends AlienSpawn> getSpawnsByQuadrant(String quadName);
    
    public List<? extends AlienSpawn> getSpawnsBySector(Sector sector);
    
    public void removeAlienSpawn(AlienSpawn spawn) throws OrionException;
    
    public void removeRace(AlienRace race) throws OrionException;
    
    public List<? extends AlienRace> getAllRaces();
    
    public List<? extends AlienSpawn> getAllSpawns();
}
