package polly.rx.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import polly.rx.entities.BattleReport;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanHistoryEntry;
import polly.rx.entities.FleetScanShip;

import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;


public class FleetDBManager {

    
    public final static String ADD_FLEET_SCAN_PERMISSION = "polly.permission.ADD_FLEET_SCAN";
    public final static String VIEW_FLEET_SCAN_PERMISSION = "polly.permission.VIEW_FLEET_SCAN";
    public final static String ADD_BATTLE_REPORT_PERMISSION = "polly.permission.ADD_BATTLE_REPORT";
    public final static String VIEW_BATTLE_REPORT_PERMISSION = "polly.permission.VIEW_BATTLE_REPORT";
    public final static String DELETE_BATTLE_REPORT_PERMISSION = "polly.permission.DELETE_BATTLE_REPORT";
    public static final String DELETE_FLEET_SCAN_PERMISSION = "plly.permission.DELETE_FLEET_SCAN";
    
    
    private PersistenceManager persistence;
    
    
    
    public FleetDBManager(PersistenceManager persistence) {
        this.persistence = persistence;
    }
    
    
    
    public void cleanInvalidBattleReports() throws DatabaseException {
        final List<BattleReport> allReports = this.getAllReports();
        
        this.persistence.atomicWriteOperation(new WriteAction() {
            @Override
            public void performUpdate(PersistenceManager persistence) {
                for (BattleReport report : allReports) {
                    if (report.getDefenderShips().isEmpty() || 
                            report.getAttackerShips().isEmpty()) {
                        persistence.remove(report);
                    }
                }
            }
        });
    }
    
    
    
    public synchronized void addBattleReport(BattleReport report) 
            throws DatabaseException {
        try {
            this.persistence.readLock();
            BattleReport rp = this.persistence.findSingle(
                BattleReport.class, BattleReport.UNIQUE_CHECK, 
                report.getQuadrant(), 
                report.getX(), report.getY(), 
                report.getAttackerVenadName(),
                report.getDefenderVenadName(),
                report.getAttackerFleetName(),
                report.getDefenderFleetName(),
                report.getDate());
            
            if (rp != null) {
                throw new DatabaseException(
                    "It seems like this Battlereport already exists.");
            }
        } finally {
            this.persistence.readUnlock();
        }
        
        this.persistence.atomicPersist(report);
    }
    
    
    
    public void deleteFleetScan(int scanId) throws DatabaseException {
        final FleetScan scan = this.getScanById(scanId);
        final List<FleetScanShip> deleteMe = new LinkedList<FleetScanShip>();
        
        this.persistence.atomicWriteOperation(new WriteAction() {
            
            @Override
            public void performUpdate(PersistenceManager persistence) {
        
                // only remove ships that do not belong to another scan
                for (FleetScanShip ship : scan.getShips()) {
                    List<FleetScan> other = getScanWithShip(ship.getRxId());
                    if (other.isEmpty() || (other.size() == 1 && other.get(0).getId() == scan.getId())) {
                        deleteMe.add(ship);
                    } else {
                        FleetScanHistoryEntry e = new FleetScanHistoryEntry();
                        e.getChanges().add("Deleted Fleet Scan that contained this ship");
                        ship.getHistory().add(e);
                    }
                }
        

                scan.getShips().clear();
                persistence.removeList(deleteMe);
                persistence.remove(scan);
            }
        });
    }
    
    
    
    public void addFleetScan(final FleetScan scan) throws DatabaseException {
        this.persistence.atomicWriteOperation(new WriteAction() {
            
            @Override
            public void performUpdate(PersistenceManager persistence) {
                List<FleetScanShip> newShips = new ArrayList<FleetScanShip>(
                    scan.getShips().size());
                
                // try to find existing ships. If a ship is spotted twice, its
                // dependent in the database will be updated with the new information
                // found
                for (FleetScanShip ship : scan.getShips()) {
                    FleetScanShip found = fleetScanShipById(ship.getRxId());
                    if (found != null) {
                        found.update(ship);
                        newShips.add(found);
                    } else {
                        newShips.add(ship);
                    }
                }
                
                scan.setShips(newShips);
                persistence.persist(scan);
            }
        });
    }
    
    
    
    public List<FleetScanShip> getShipsByOwner(String ownerName) {
        return this.persistence.atomicRetrieveList(FleetScanShip.class, 
            FleetScanShip.BY_OWNER, ownerName);
    }
    
    
    
    public List<FleetScan> getScansWithOwner(String ownerName) {
        return this.persistence.atomicRetrieveList(FleetScan.class, 
            FleetScan.CONTAINING_OWNER, ownerName);
    }
    
    
    
    public FleetScanShip getShipByRevorixId(int rxId) {
        return this.persistence.findSingle(
            FleetScanShip.class, FleetScanShip.BY_REVORIX_ID, rxId);
    }
    
    
    
    public List<FleetScan> getScanWithShip(int rxId) {
        return this.persistence.findList(
            FleetScan.class, FleetScan.CONTAINING_SHIP, new Object[] { rxId } );
    }
    
    
    
    private FleetScanShip fleetScanShipById(int rxId) {
        return this.persistence.findSingle(FleetScanShip.class, 
            FleetScanShip.BY_REVORIX_ID, rxId);
    }
    
    
    
    public List<FleetScan> getAllScans() {
        return this.persistence.atomicRetrieveList(FleetScan.class, 
            FleetScan.ALL_SCANS);
    }
    
    
    
    public List<BattleReport> getAllReports() {
        return this.persistence.atomicRetrieveList(BattleReport.class, 
            BattleReport.ALL_REPORTS);
    }
    
    
    public List<FleetScanShip> getAllScannedShips() {
        return this.persistence.atomicRetrieveList(FleetScanShip.class, 
            FleetScanShip.All_SHIPS);
    }
    
    
    
    
    public BattleReport getReportById(int id) {
        return this.persistence.atomicRetrieveSingle(BattleReport.class, id);
    }
    
    
    
    public FleetScan getScanById(int id) {
        return this.persistence.atomicRetrieveSingle(FleetScan.class, id);
    }
    
    
    
    public List<BattleReport> getReportByIdList(Integer...ids) {
        List<BattleReport> result = new ArrayList<BattleReport>(ids.length);
        try {
            this.persistence.readLock();
            for (int id : ids) {
                BattleReport rp = this.persistence.find(BattleReport.class, id);
                result.add(rp);
            }
        } finally {
            this.persistence.readUnlock();
        }
        return result;
    }
    
    
    
    public List<BattleReport> getReportsWithVenad(String venad) {
        return this.persistence.atomicRetrieveList(BattleReport.class, 
            BattleReport.WITH_NAME, venad);
    }
    
    
    
    public List<BattleReport> getReportsWithClan(String clan) {
        return this.persistence.atomicRetrieveList(BattleReport.class, 
            BattleReport.WITH_CLAN, clan);
    }
    
    
    
    public List<BattleReport> getReportsByLocation(String location) {
        return this.persistence.atomicRetrieveList(BattleReport.class, 
            BattleReport.BY_LOCATION, location);
    }
    
    
    
    public void deleteReportByIdList(final Integer...ids) throws DatabaseException {
        this.persistence.atomicWriteOperation(new WriteAction() {
            
            @Override
            public void performUpdate(PersistenceManager persistence) {
                List<BattleReport> deleteMe = getReportByIdList(ids);
                persistence.removeList(deleteMe);
            }
        });
    }
    
    
    
    public void deleteReportById(int id) throws DatabaseException {
        final BattleReport report = this.getReportById(id);
        
        this.persistence.atomicWriteOperation(new WriteAction() {
            
            @Override
            public void performUpdate(PersistenceManager persistence) {
                persistence.removeList(report.getDrop());
                persistence.removeList(report.getAttackerShips());
                persistence.removeList(report.getDefenderShips());
                report.getDrop().clear();
                report.getAttackerShips().clear();
                report.getDefenderShips().clear();
                persistence.remove(report);
            }
        });
    }



    public List<FleetScan> getScansWithClan(String clanName) {
        return this.persistence.atomicRetrieveList(FleetScan.class, 
            FleetScan.SCANS_BY_CLAN, clanName);
    }



    public List<FleetScanShip> getShipsByClan(String clanName) {
        return this.persistence.atomicRetrieveList(FleetScanShip.class, 
            FleetScanShip.SHIPS_BY_CLAN, clanName);
    }



    public List<FleetScan> getScansWithLocation(String quadrant) {
        return this.persistence.atomicRetrieveList(FleetScan.class, 
            FleetScan.SCANS_BY_LOCATION, quadrant);
    }



    public List<FleetScanShip> getShipsWithLocation(String quadrant) {
        return this.persistence.atomicRetrieveList(FleetScanShip.class, 
            FleetScanShip.SHIPS_BY_LOCATION, quadrant, "%" + quadrant + "%");
    }



    public List<BattleReport> getReportByUserId(int id) {
        return this.persistence.atomicRetrieveList(
            BattleReport.class, BattleReport.BY_USER_ID, id);
    }
    
    
}