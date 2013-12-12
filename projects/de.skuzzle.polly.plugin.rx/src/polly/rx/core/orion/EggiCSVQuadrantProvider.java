package polly.rx.core.orion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polly.rx.core.orion.model.Production;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorType;
import polly.rx.entities.RxRessource;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;


public class EggiCSVQuadrantProvider implements QuadrantProvider {
    
    private static class EggiSector implements Sector {
        
        private String quadName;
        private int x;
        private int y;
        private int attackerBonus;
        private int defenderBonus;
        private int sectorGuardBonus;
        private Date date;
        private SectorType type;
        private Collection<Production> ressources;
        
        @Override
        public String getQuadName() {
            return this.quadName;
        }
        
        @Override
        public int getX() {
            return this.x;
        }
        
        @Override
        public int getY() {
            return this.y;
        }
        
        @Override
        public int getAttackerBonus() {
            return this.attackerBonus;
        }
        
        @Override
        public int getDefenderBonus() {
            return this.defenderBonus;
        }
        
        @Override
        public int getSectorGuardBonus() {
            return this.sectorGuardBonus;
        }
        
        @Override
        public Date getDate() {
            return this.date;
        }

        @Override
        public SectorType getType() {
            return this.type;
        }
        
        @Override
        public Collection<Production> getRessources() {
            return this.ressources;
        }
        
        @Override
        public boolean equals(Object obj) {
            return EqualsHelper.testEquality(this, obj);
        }

        @Override
        public Class<?> getEquivalenceClass() {
            return Sector.class;
        }

        @Override
        public boolean actualEquals(Equatable o) {
            final Sector other = (Sector) o;
            return this.x == other.getX() && this.getY() == other.getY() && 
                    this.quadName.equals(other.getQuadName());
        }
        
        @Override
        public String toString() {
            return String.format("%s %d, %d", this.quadName, this.x, this.y); //$NON-NLS-1$
        }
    }
    
    
    
    private static class EggiQuadrant implements Quadrant {
        
        private final String name;
        private final Map<String, Sector> sectors;
        private final int maxX;
        private final int maxY;
        
        public EggiQuadrant(String name, Map<String, Sector> sectors, 
                int maxX, int maxY) {
            this.name = name;
            this.sectors = sectors;
            this.maxX = maxX;
            this.maxY = maxY;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        @Override
        public Sector getSector(int x, int y) {
            final String key = createKey(x, y);
            Sector qs = this.sectors.get(key);
            if (qs == null) {
                final EggiSector es = new EggiSector();
                es.quadName = name;
                es.x = x;
                es.y = y;
                es.type = SectorType.NONE;
                es.ressources = new ArrayList<>();
                return es;
            }
            return qs;
        }
        
        @Override
        public Collection<Sector> getSectors() {
            return this.sectors.values();
        }
        
        @Override
        public int getMaxX() {
            return this.maxX;
        }
        
        @Override
        public int getMaxY() {
            return this.maxY;
        }

        @Override
        public Class<?> getEquivalenceClass() {
            return Quadrant.class;
        }

        @Override
        public boolean actualEquals(Equatable o) {
            return this.name.equals(((Quadrant) o).getName());
        }
    }
    
    
    
    private static String createKey(int x, int y) {
        return x + "_" + y; //$NON-NLS-1$
    }
    
    
    
    private Map<Integer, String> nameMap;
    private Collection<Sector> allSectors;
    private Map<String, Quadrant> quadMap;
    
    
    
    public EggiCSVQuadrantProvider() {
        this.nameMap = this.readQuadrants();
        this.quadMap = new HashMap<>();
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
                final EggiSector sector = new EggiSector();
                
                final int quadId = Integer.parseInt(parts[1]);
                sector.quadName = nameMap.get(quadId);
                sector.x = Integer.parseInt(parts[2]);
                sector.y = Integer.parseInt(parts[3]);
                sector.attackerBonus = Integer.parseInt(parts[5]);
                sector.defenderBonus = Integer.parseInt(parts[6]);
                sector.sectorGuardBonus = Integer.parseInt(parts[7]);
                
                sector.ressources = new ArrayList<>();
                for (int i = 0; i < 14; ++i) {
                    final RxRessource ress = RxRessource.values()[i];
                    final float rate = Float.parseFloat(parts[i + 8]);
                    if (rate > 0.f) {
                        sector.ressources.add(new Production(ress, rate));
                    }
                }
                
                final int type = Integer.parseInt(parts[4]);
                sector.type = this.findType(type);
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
        Quadrant quad = this.quadMap.get(name);
        if (quad == null) {
            final Map<String, Sector> sectors = new HashMap<>();
            int maxX = 0;
            int maxY = 0;
            for (final Sector sector : this.allSectors) {
                if (sector.getQuadName().equals(name)) {
                    maxX = Math.max(maxX, sector.getX());
                    maxY = Math.max(maxY, sector.getY());
                    sectors.put(createKey(sector.getX(), sector.getY()), sector);
                }
            }
            quad = new EggiQuadrant(name, sectors, maxX, maxY);
        }
        return quad;
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
