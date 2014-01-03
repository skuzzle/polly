package polly.rx.core.orion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import polly.rx.core.orion.model.Production;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.QuadrantUtils;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorType;
import polly.rx.entities.RxRessource;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;


public class EggiCSVQuadrantProvider implements QuadrantProvider, QuadrantUpdater {
    
    
    
    private static class EggiProduction implements Production {

        private final RxRessource ress;
        private final float rate;

        public EggiProduction(RxRessource ress, float rate) {
            this.ress = ress;
            this.rate = rate;
        }

        @Override
        public RxRessource getRess() {
            return this.ress;
        }

        @Override
        public float getRate() {
            return this.rate;
        }
    }
    
    
    
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
        
        private final int id;
        private final String name;
        private final Map<String, EggiSector> sectors;
        private final int maxX;
        private final int maxY;
        
        public EggiQuadrant(int id, String name, Map<String, EggiSector> sectors, 
                int maxX, int maxY) {
            this.id = id;
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
        public EggiSector getSector(int x, int y) {
            final String key = QuadrantUtils.createMapKey(x, y);
            EggiSector qs = this.sectors.get(key);
            if (qs == null) {
                qs = new EggiSector();
                qs.type = SectorType.NONE;
                qs.x = x;
                qs.y = y;
                qs.quadName = this.name;
                qs.ressources = new ArrayList<>();
            }
            return qs;
        }
        
        @Override
        public Collection<EggiSector> getSectors() {
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
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    
    private final static String QUADRANTS_FILE = "quadrants.csv"; //$NON-NLS-1$
    private final static String SECTORS_FILE = "sectors.csv"; //$NON-NLS-1$
    
    private Map<Integer, String> idToName;
    private Map<String, Integer> nameToId;
    private Collection<EggiSector> allSectors;
    private Map<String, EggiQuadrant> quadMap;
    private final File pluginFolder;
    
    
    public EggiCSVQuadrantProvider(File pluginFolder) {
        this.pluginFolder = pluginFolder;
                
        this.idToName = new HashMap<>();
        this.nameToId = new HashMap<>();
        this.quadMap = new HashMap<>();
        this.allSectors = new ArrayList<>();
        
        this.readQuadrants(this.idToName, this.nameToId);
        this.readSectors(this.allSectors);
        this.store();
    }
    
    
    
    private String[] splitAndStrip(String line) {
        return line.replaceAll("\"", "").split(";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    
    
    private InputStream searchCsv(File pluginFolder, String fileName) throws FileNotFoundException {
        final File test = new File(pluginFolder, fileName);
        if (test.exists()) {
            return new FileInputStream(test);
        } else {
            return this.getClass().getResourceAsStream(fileName);
        }
    }
    
    
    
    private void readQuadrants(Map<Integer, String> idToName, 
            Map<String, Integer> nameToId) {
        
        idToName.clear();
        nameToId.clear();
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(this.searchCsv(this.pluginFolder, QUADRANTS_FILE)))) {
            
            String line = null;
            while ((line = r.readLine()) != null) {
                final String[] parts = this.splitAndStrip(line);
                final int id = Integer.parseInt(parts[0]);
                final String name = parts[1];
                
                idToName.put(id, name);
                nameToId.put(name, id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    private void readSectors(Collection<EggiSector> sectors) {
        sectors.clear();
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(this.searchCsv(this.pluginFolder, SECTORS_FILE)))) {
            
            String line = null;
            while ((line = r.readLine()) != null) {
                final String[] parts = this.splitAndStrip(line);
                final EggiSector sector = new EggiSector();
                
                final int quadId = Integer.parseInt(parts[1]);
                sector.quadName = this.idToName.get(quadId);
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
                        sector.ressources.add(new EggiProduction(ress, rate));
                    }
                }
                
                final int type = Integer.parseInt(parts[4]);
                sector.type = this.findType(type);
                sectors.add(sector);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    private void store() {
        // Store quadrants
        final File quads = new File(this.pluginFolder, QUADRANTS_FILE);
        try (PrintWriter w = new PrintWriter(quads)) {
            for (final String quadName : this.idToName.values()) {
                final EggiQuadrant eq = this.getQuadrant(quadName);
                w.format("\"%d\";\"%s\";\"%d\";\"%d\"", eq.id, eq.name, eq.maxX, eq.maxY); //$NON-NLS-1$
                w.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        // store sectors
        final File sectors = new File(this.pluginFolder, SECTORS_FILE);
        try (PrintWriter w = new PrintWriter(sectors)) {
            for (final EggiSector es : this.allSectors) {
                // "1";"10";"18";"11";"13";"-13";"-5";"-8";"0";"49.9";"0";"0";"0";"0";"0";"0";"0";"0";"4.09";"0";"0";"0"
                final int quadId = this.nameToId.get(es.getQuadName());
                w.format("\"0\";\"%d\";\"%d\";\"%d\";\"%d\";\"%d\";\"%d\";\"%d\";", quadId, es.getX(),  //$NON-NLS-1$
                        es.getY(), es.getType().getId(), es.getAttackerBonus(), es.getDefenderBonus(), 
                        es.getSectorGuardBonus());
                
                for (int i = 0; i < 14; ++i) {
                    final RxRessource ress = RxRessource.values()[i];
                    boolean didit = false;
                    for (final Production prod : es.getRessources()) {
                        if (prod.getRess() == ress) {
                            w.format(Locale.ENGLISH, "\"%.2f\"", prod.getRate()); //$NON-NLS-1$
                            didit = true;
                            break;
                        }
                    }
                    if (!didit) {
                        w.print("\"0\""); //$NON-NLS-1$
                    }
                    
                    if (i != 13) {
                        w.print(";"); //$NON-NLS-1$
                    }
                }
                w.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        return this.idToName.values();
    }
    
    

    @Override
    public EggiQuadrant getQuadrant(Sector sector) {
        return this.getQuadrant(sector.getQuadName());
    }
    
    

    @Override
    public EggiQuadrant getQuadrant(String name) {
        if (!this.nameToId.containsKey(name)) {
            return new EggiQuadrant(-1, name, 
                    Collections.<String, EggiSector>emptyMap(), 1, 1);
        }
        EggiQuadrant quad = this.quadMap.get(name);
        if (quad == null) {
            final Map<String, EggiSector> sectors = new HashMap<>();
            int maxX = 0;
            int maxY = 0;
            for (final EggiSector sector : this.allSectors) {
                if (sector.getQuadName().equals(name)) {
                    maxX = Math.max(maxX, sector.getX());
                    maxY = Math.max(maxY, sector.getY());
                    sectors.put(QuadrantUtils.createMapKey(sector), sector);
                }
            }
            final int id = this.nameToId.get(name);
            quad = new EggiQuadrant(id, name, sectors, maxX, maxY);
            this.quadMap.put(name, quad);
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
    
    
    
    @Override
    public void updateSectorInformation(Sector sector) {
        final EggiSector newEggiSector = new EggiSector();
        newEggiSector.attackerBonus = sector.getAttackerBonus();
        newEggiSector.defenderBonus = sector.getDefenderBonus();
        newEggiSector.sectorGuardBonus = sector.getSectorGuardBonus();
        newEggiSector.date = Time.currentTime();
        newEggiSector.type = sector.getType();
        newEggiSector.quadName = sector.getQuadName();
        newEggiSector.x = sector.getX();
        newEggiSector.y = sector.getY();
        
        final EggiQuadrant quadrant = this.getQuadrant(sector);
        final EggiSector existing = quadrant.getSector(
                sector.getX(), sector.getY());
        
        if (sector.getType() == SectorType.UNKNOWN && existing.getType() != SectorType.NONE) {
            // do not update existing sectors with unknown sectors
            return;
        }
        this.allSectors.remove(existing);
        this.allSectors.add(newEggiSector);
        quadrant.sectors.put(QuadrantUtils.createMapKey(newEggiSector), newEggiSector);
        this.store();
    }
}
