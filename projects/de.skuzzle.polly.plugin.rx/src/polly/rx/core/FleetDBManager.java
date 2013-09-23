package polly.rx.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import polly.rx.entities.BattleReport;
import polly.rx.entities.FleetScan;
import polly.rx.entities.FleetScanHistoryEntry;
import polly.rx.entities.FleetScanShip;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Atomic;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;


public class FleetDBManager {

    
    public final static String ADD_FLEET_SCAN_PERMISSION = "polly.permission.ADD_FLEET_SCAN";
    public final static String VIEW_FLEET_SCAN_PERMISSION = "polly.permission.VIEW_FLEET_SCAN";
    public final static String ADD_BATTLE_REPORT_PERMISSION = "polly.permission.ADD_BATTLE_REPORT";
    public final static String VIEW_BATTLE_REPORT_PERMISSION = "polly.permission.VIEW_BATTLE_REPORT";
    public final static String DELETE_BATTLE_REPORT_PERMISSION = "polly.permission.DELETE_BATTLE_REPORT";
    public static final String DELETE_FLEET_SCAN_PERMISSION = "plly.permission.DELETE_FLEET_SCAN";
    
    
    private final PersistenceManagerV2 persistence;
    
    
    
    public FleetDBManager(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
    }
    
    
    
    public void cleanInvalidBattleReports() throws DatabaseException {
        final List<BattleReport> allReports = this.getAllReports();
        
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) {
                for (BattleReport report : allReports) {
                    if (report.getDefenderShips().isEmpty() || 
                            report.getAttackerShips().isEmpty()) {
                        write.remove(report);
                    }
                }
            }
        });
    }
    
    
    
    public synchronized void addBattleReport(final BattleReport report) 
            throws DatabaseException {
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) throws DatabaseException {
                final BattleReport rp = write.read().findSingle(
                    BattleReport.class, BattleReport.UNIQUE_CHECK, new Param(
                    report.getQuadrant(), 
                    report.getX(), report.getY(), 
                    report.getAttackerVenadName(),
                    report.getDefenderVenadName(),
                    report.getAttackerFleetName(),
                    report.getDefenderFleetName(),
                    report.getDate()));
                
                if (rp != null) {
                    throw new DatabaseException(
                        "It seems like this Battlereport already exists.");
                }
                
                write.single(report);
            }
        });
    }
    
    
    
    public void deleteFleetScan(int scanId) throws DatabaseException {
        final FleetScan scan = this.getScanById(scanId);
        final List<FleetScanShip> deleteMe = new LinkedList<FleetScanShip>();
        
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) {
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
                write.removeAll(deleteMe);
                write.remove(scan);
            }
        });
    }
    
    
    
    public void addFleetScan(final FleetScan scan) throws DatabaseException {
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) {
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
                write.single(scan);
            }
        });
    }
    
    
    
    public List<FleetScanShip> getShipsByOwner(String ownerName) {
        return this.persistence.atomic().findList(FleetScanShip.class, 
            FleetScanShip.BY_OWNER, new Param(ownerName));
    }
    
    
    
    public List<FleetScan> getScansWithOwner(String ownerName) {
        return this.persistence.atomic().findList(FleetScan.class, 
            FleetScan.CONTAINING_OWNER, new Param(ownerName));
    }
    
    
    
    public FleetScanShip getShipByRevorixId(int rxId) {
        return this.persistence.atomic().findSingle(
            FleetScanShip.class, FleetScanShip.BY_REVORIX_ID, new Param(rxId));
    }
    
    
    
    public List<FleetScan> getScanWithShip(int rxId) {
        return this.persistence.atomic().findList(
            FleetScan.class, FleetScan.CONTAINING_SHIP, new Param(rxId));
    }
    
    
    
    public FleetScanShip fleetScanShipById(int rxId) {
        return this.persistence.atomic().findSingle(FleetScanShip.class, 
            FleetScanShip.BY_REVORIX_ID, new Param(rxId));
    }
    
    
    
    public List<FleetScan> getAllScans() {
        return this.persistence.atomic().findList(FleetScan.class, FleetScan.ALL_SCANS);
    }
    
    
    
    public List<BattleReport> getAllReports() {
        return this.persistence.atomic().findList(BattleReport.class, 
            BattleReport.ALL_REPORTS);
    }
    
    
    public List<FleetScanShip> getAllScannedShips() {
        return this.persistence.atomic().findList(FleetScanShip.class, 
            FleetScanShip.All_SHIPS);
    }
    
    
    
    
    public BattleReport getReportById(int id) {
        return this.persistence.atomic().find(BattleReport.class, id);
    }
    
    
    
    public FleetScan getScanById(int id) {
        return this.persistence.atomic().find(FleetScan.class, id);
    }
    
    
    
    public List<BattleReport> getReportByIdList(Integer...ids) {
        List<BattleReport> result = new ArrayList<BattleReport>(ids.length);
        try (final Read r = this.persistence.read()) {
            for (int id : ids) {
                BattleReport rp = r.find(BattleReport.class, id);
                result.add(rp);
            }
        }
        return result;
    }
    
    
    
    public List<BattleReport> getReportsWithVenad(String venad) {
        return this.persistence.atomic().findList(BattleReport.class, 
            BattleReport.WITH_NAME, new Param(venad));
    }
    
    
    
    public List<BattleReport> getReportsWithClan(String clan) {
        return this.persistence.atomic().findList(BattleReport.class, 
            BattleReport.WITH_CLAN, new Param(clan));
    }
    
    
    
    public List<BattleReport> getReportsByLocation(String location) {
        return this.persistence.atomic().findList(BattleReport.class, 
            BattleReport.BY_LOCATION, new Param(location));
    }
    
    
    
    public void deleteReportByIdList(final Integer...ids) throws DatabaseException {
        this.persistence.writeAtomic(new Atomic() {
            
            @Override
            public void perform(Write write) {
                final List<BattleReport> deleteMe = getReportByIdList(ids);
                write.removeAll(deleteMe);
            }
        });
    }
    
    
    
    public void deleteReportById(int id) throws DatabaseException {
        final BattleReport report = this.getReportById(id);
        
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) {
                write.removeAll(report.getDrop());
                write.removeAll(report.getAttackerShips());
                write.removeAll(report.getDefenderShips());
                report.getDrop().clear();
                report.getAttackerShips().clear();
                report.getDefenderShips().clear();
                write.remove(report);
            }
        });
    }
    
    
    
    public int getBattleReportCount() {
        final Number n = this.persistence.atomic().findSingle(
            Number.class, BattleReport.REPORT_COUNT);
        return n.intValue();
    }
    
    
    
    public List<BattleReport> battleReportRange(int first, int max) {
        return this.persistence.atomic().findList(BattleReport.class, 
            BattleReport.ALL_REPORTS, first, max, new Param(0));
    }



    public List<FleetScan> getScansWithClan(String clanName) {
        return this.persistence.atomic().findList(FleetScan.class, 
            FleetScan.SCANS_BY_CLAN, new Param(clanName));
    }



    public List<FleetScanShip> getShipsByClan(String clanName) {
        return this.persistence.atomic().findList(FleetScanShip.class, 
            FleetScanShip.SHIPS_BY_CLAN, new Param(clanName));
    }



    public List<FleetScan> getScansWithLocation(String quadrant) {
        return this.persistence.atomic().findList(FleetScan.class, 
            FleetScan.SCANS_BY_LOCATION, new Param(quadrant));
    }



    public List<FleetScanShip> getShipsWithLocation(String quadrant) {
        return this.persistence.atomic().findList(FleetScanShip.class, 
            FleetScanShip.SHIPS_BY_LOCATION, new Param(quadrant, "%" + quadrant + "%"));
    }



    public List<BattleReport> getReportByUserId(int id) {
        return this.persistence.atomic().findList(
            BattleReport.class, BattleReport.BY_USER_ID, new Param(id));
    }
}