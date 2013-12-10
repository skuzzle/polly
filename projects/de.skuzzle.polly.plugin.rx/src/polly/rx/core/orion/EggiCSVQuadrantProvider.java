package polly.rx.core.orion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polly.rx.core.orion.model.Production;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorType;
import polly.rx.entities.RxRessource;


public class EggiCSVQuadrantProvider implements QuadrantProvider {
    
    private Map<Integer, String> nameMap;
    private Collection<Sector> allSectors;
    
    
    public EggiCSVQuadrantProvider() {
        this.nameMap = this.readQuadrants();
        this.allSectors = this.readSectors(this.nameMap);
    }
    
    
    private String[] splitAndStrip(String line) {
        return line.replaceAll("\"", "").split(";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    
    private Map<Integer, String> readQuadrants() {
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(
                        this.getClass().getResourceAsStream("quadrants.csv")))) { //$NON-NLS-1$
            
            String line = null;
            final Map<Integer, String> nameMap = new HashMap<>();
            while ((line = r.readLine()) != null) {
                final String[] parts = this.splitAndStrip(line);
                final int id = Integer.parseInt(parts[0]);
                final String name = parts[1];
                
                nameMap.put(id, name);
            }
            return nameMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
    
    
    
    private Collection<Sector> readSectors(Map<Integer, String> nameMap) {
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(
                        this.getClass().getResourceAsStream("sectors.csv")))) { //$NON-NLS-1$
            
            String line = null;
            final List<Sector> sectors = new ArrayList<>();
            while ((line = r.readLine()) != null) {
                final String[] parts = this.splitAndStrip(line);
                final Sector sector = new Sector();
                
                final int quadId = Integer.parseInt(parts[1]);
                sector.setQuadName(nameMap.get(quadId));
                
                sector.setX(Integer.parseInt(parts[2]));
                sector.setY(Integer.parseInt(parts[3]));
                sector.setAttackerBonus(Integer.parseInt(parts[5]));
                sector.setDefenderBonus(Integer.parseInt(parts[6]));
                sector.setSectorGuardBonus(Integer.parseInt(parts[7]));
                
                final List<Production> production = new ArrayList<>();
                for (int i = 0; i < 14; ++i) {
                    final RxRessource ress = RxRessource.values()[i];
                    final float rate = Float.parseFloat(parts[i + 8]);
                    if (rate != 0.f) {
                        production.add(new Production(ress, rate));
                    }
                }
                
                final int type = Integer.parseInt(parts[4]);
                sector.setType(this.findType(type));
                sectors.add(sector);
            }
            return sectors;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    
    
    
    
    private SectorType findType(int id) {
        for (final SectorType type : SectorType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return SectorType.NONE;
    }
    

    
    @Override
    public Collection<String> getAllQuadrantNames() {
        return this.nameMap.values();
    }
    
    

    @Override
    public Quadrant getQuadrant(Sector sector) {
        return this.getQuadrant(sector.getQuadName());
    }
    
    

    @Override
    public Quadrant getQuadrant(String name) {
        final Map<String, Sector> sectors = new HashMap<>();
        int maxX = 0;
        int maxY = 0;
        for (final Sector sector : this.allSectors) {
            if (sector.getQuadName().equals(name)) {
                maxX = Math.max(maxY, sector.getX());
                maxY = Math.max(maxY, sector.getY());
                sectors.put(DatabaseQuadrantProvider.createKey(sector.getX(), sector.getY()), sector);
            }
        }
        return new Quadrant(name, sectors, maxX, maxY);
    }

    
    
    @Override
    public Collection<Quadrant> getAllQuadrants() {
        final List<Quadrant> result = new ArrayList<>();
        for (final String quadName : this.getAllQuadrantNames()) {
            result.add(this.getQuadrant(quadName));
        }
        return result;
    }

}
