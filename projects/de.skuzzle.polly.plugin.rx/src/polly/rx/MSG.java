package polly.rx;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;

public class MSG extends Constants {

    public final static String FAMILY = "polly.rx.Translation"; //$NON-NLS-1$

    // AddTrainCommand
    public static String addTrainHelp;
    public static String addTrainSig0Desc;
    public static String addTrainSig0User;
    public static String addTrainSig0Bill;
    public static String addTrainSig1Desc;
    public static String addTrainSig1User;
    public static String addTrainSig1Details;
    public static String addTrainSig2Desc;
    public static String addTrainSig2User;
    public static String addTrainSig2Bill;
    public static String addTrainSig2Weight;
    public static String addTrainSig3Desc;
    public static String addTrainSig3User;
    public static String addTrainSuccess;
    public static String addTrainFail;
    public static String addTrainRemind;

    // IGMCommand
    public static String igmHelp;
    public static String igmSig0Desc;
    public static String igmSig0ParamMsg;
    public static String igmViaIrcPostfix;

    // AnonymizationCommand
    public static String anonymizationHelp;
    public static String anonymizationSig0Desc;
    public static String anonymizationSig0Name;
    public static String anonymizationOn;
    public static String anonymizationOff;

    // CLoseTrainCommand
    public static String closeTrainHelp;
    public static String closeTrainSig0Desc;
    public static String closeTrainSig0User;
    public static String closeTrainSig1Desc;
    public static String closeTrainSig1Id;
    public static String closeTrainSuccessAll;
    public static String closeTrainSuccessSingle;

    // CrackerCommand
    public static String crackerHelp;
    public static String crackerSig0Desc;
    public static String crackerSig1Desc;
    public static String crackerSig1User;
    public static String crackerUnknownUser;
    public static String crackerSuccess;

    // DeliverTrainCommand
    public static String deliverHelp;
    public static String deliverSig0Desc;
    public static String deliverSig0User;
    public static String deliverSig1Desc;
    public static String deliverSig1User;
    public static String deliverSig1Receiver;

    // IPCommand
    public static String ipHelp;
    public static String ipSig0Desc;
    public static String ipSig0Venad;
    public static String ipInvalidAnswer;
    public static String ipNoIp;
    public static String ipResultWithClan;
    public static String ipResult;

    // MyTrainsCommand
    public static String myTrainsHelp;
    public static String myTrainsSig0Desc;
    public static String myTrainsSig0Trainer;
    public static String myTrainsSig1Desc;
    public static String myTrainsSig1Trainer;
    public static String myTrainsSig1Details;

    // MyVenadCommand
    public static String myVenadHelp;
    public static String myVenadSig0Desc;
    public static String myVenadSig0Name;
    public static String myVenadSuccess;

    // RankCommand
    public static String rankHelp;
    public static String rankSig0Desc;
    public static String rankSig0Name;
    public static String rankNoVenad;
    public static String rankSuccess;

    // RessCommand
    public static String ressHelp;
    public static String ressSigDesc;
    public static String ressSigExpression;

    // RouteCommand
    public static String routeHelp;
    public static String routeSig0Desc;
    public static String routeSig0Start;
    public static String routeSig0Ziel;
    public static String routeParseError;
    public static String routeInfo;
    public static String routeNoRouteFound;

    // VenadCommand
    public static String venadHelp;
    public static String venadSig0Desc;
    public static String venadSig0User;
    public static String venadUnknownUser;
    public static String venadSuccess;

    // AZEntryManager
    public static String azEntryCantDeleteOther;

    // FleetDbManager
    public static String fleetDbReportExists;
    public static String fleetDbDeletedScanWithShip;

    // ScoreboardManager
    public static String scoreboardAvgPoints;
    public static String scoreboardAvgRank;
    public static String scoreboardPoints;
    public static String scoreboardRank;
    public static String scoreboardDatePoints;
    public static String scoreboardDateRank;

    // TrainBillV2
    public static String billNoOpen;
    public static String billOpen;

    // TrainManagerV2
    public static String trainManagerInvalidTrainId;
    public static String trainManagerInvalidTrainerId;

    // BattleTactic
    public static String tacticNormal;
    public static String tacticRaubzug;
    public static String tacticMachtDemo;
    public static String tacticTPT;
    public static String tacticSchnitt;
    public static String tacticNahkampf;
    public static String tacticSystem;
    public static String tacticAusweich;
    public static String tacticTT;
    public static String tacticZange;
    public static String tacticAbgesichert;
    public static String tacticSondierung;
    public static String tacticFern;
    public static String tacticMultivektor;
    public static String tacticKonzentriert;
    public static String tacticVertreiben;
    public static String tacticKralle;
    public static String tacticSichel;
    public static String tacticHitAndRun;
    public static String tacticHinterhalt;
    public static String tacticSturm;
    public static String tacticDauerbeschuss;
    public static String tacticAlien;
    public static String tacticSekWache;

    // FleetScanShip
    public static String scanShipSpotFirstTime;
    public static String scanShipSpotted;
    public static String scanShipNameChanged;
    public static String scanShipOwnerChanged;
    public static String scanShipOwnerChangedIndicator;
    public static String scanShipOwnerChangedClan;

    // TrainEntity
    public static String trainEntityMisformatted;
    public static String trainEntityFormatWithFactor;
    public static String trainEntityFormatWithoutFactor;

    // TrainType
    public static String trainTypeIntelligence;
    public static String trainTypeBody;
    public static String trainTypeCommando;
    public static String trainTypeModule;
    public static String trainTypeCrew;
    public static String trainTypeTech;
    public static String trainTypePayment;
    public static String trainTypeExtendedIntelligence;
    public static String trainTypeExtendedBody;
    public static String trainTypeExtendedCommand;
    public static String trainTypeExtendedModule;
    public static String trainTypeExtendedCrew;
    public static String trainTypeExtendedTech;
    public static String trainTypeIntensiveIntelligence;
    public static String trainTypeIntensiveBody;
    public static String trainTypeIntensiveCommand;
    public static String trainTypeIntensiveModule;
    public static String trainTypeIntensiveCrew;
    public static String trainTypeIntensiveTech;

    // DailyGreeter
    public static String greeterGreetings;

    // BattleReportModel
    public static String reportModelColumns;
    public static String reportModelDetailTitle;
    public static String reportModelDeleteTitle;

    // TrainingTableModel
    public static String trainingModelColumns;

    // RXController
    public static String httpRxCategory;
    public static String httpFleetScanMngr;
    public static String httpFleetScanMngrDesc;
    public static String httpScannedShips;
    public static String httpScannedShipsDesc;
    public static String httpScoreboardMngr;
    public static String httpScoreboardMngrDesc;
    public static String httpReportsMngr;
    public static String httpReportsMngrDesc;
    public static String httpAzMngr;
    public static String httpAzMngrDesc;
    public static String httpAddAzIllegalFormat;
    public static String httpGmScripts;
    public static String httpGmScriptsDesc;
    public static String httpPostScanSuccess;
    public static String httpIllegalLogin;
    public static String httpNoPermission;
    public static String httpPostScoreboardSuccess;
    public static String httpPostReportSuccess;
    public static String httpAutoRemindCommand;
    public static String httpShipsBelow;
    public static String httpRepairWarning;

    // BattleReportShipModel
    public static String reportShipModelColumns;
    public static String reportShipModelUnknown;

    // FleetScanShipTableModel
    public static String scanShipModelColumns;

    // FleetScanTableModel
    public static String scanModelColumns;

    // ScoreboardDetailModel
    public static String scoreboardDetailModelColumns;

    // ScoreboardTableModel
    public static String scoreboardModelColumns;
    public static String scoreboardModelCompareTitle;
    public static String scoreboardModelDetailsTitle;

    // HTML
    public static String htmlDrop;
    public static String htmlAttackerFleet;
    public static String htmlDefenderFleet;
    public static String htmlFleetName;
    public static String htmlVenad;
    public static String htmlClan;
    public static String htmlKw;
    public static String htmlBonus;
    public static String htmlXPMod;
    public static String htmlShieldDmg;
    public static String htmlPzDmg;
    public static String htmlWend;
    public static String htmlCapiXp;
    public static String htmlCrewXp;
    public static String htmlStatisticCaption;
    public static String htmlStatisticHowTo;
    public static String htmlStatisticAdjust;
    public static String htmlStatisticReload;
    public static String htmlAllReportsCaption;
    public static String htmlPostReportCaption;
    public static String htmlPostReportSubmit;
    public static String htmlPostDeleteConfirm;
    public static String htmlReportStatsHeader;
    public static String htmlReportStatsArtifacts;
    public static String htmlReportStatsTotalDrop;
    public static String htmlReportStatsAvgDrop;
    public static String htmlReportStatsChance;
    public static String htmlReportStatsMaxDrop;
    public static String htmlReportStatsMinDrop;
    public static String htmlReportStatsDropNetto;
    public static String htmlReportStatsSum;
    public static String htmlReportStatsAvgKw;
    public static String htmlReportStatsCapiXp;
    public static String htmlReportStatsCrewXp;
    public static String htmlReportStatsPzDmg;
    public static String htmlReportStatsRepairTime;
    public static String htmlReportStatsRepairCosts;
    public static String htmlReportStatsNote;
    public static String htmlReportStatsNoteAlienAttacks;

    public static String htmlConfigAzCaption;
    public static String htmlConfiAzDesc;
    public static String htmlConfigAzDesc2;
    public static String htmlConfigAzAz;
    public static String htmlConfigAzJumpTime;
    public static String htmlConfigAzAction;
    public static String htmlConfigAzSubmit;
    public static String htmlConfigAzDelete;

    public static String htmlFleetScanDetailsCaption;
    public static String htmlFleetScanDate;
    public static String htmlFleetScanSens;
    public static String htmlFleetScanName;
    public static String htmlFleetScanOwner;
    public static String htmlFleetScanClan;
    public static String htmlFleetScanTag;
    public static String htmlFleetScanLocation;
    public static String htmlFleetScanMeta;
    public static String htmlFleetScanContainedShipsCaption;
    public static String htmlFleetScanOVerviewCaption;
    public static String htmlFleetScanOverviewInfo;
    public static String htmlFleetScanPostScanCaption;
    public static String htmlFleetScanPasteLabel;
    public static String htmlFleetScanQuadrantLabel;
    public static String htmlFleetScanXLabel;
    public static String htmlFleetScanYLabel;
    public static String htmlFleetScanSubmit;

    public static String htmlGMCaption;
    public static String htmlGMScript;
    public static String htmlGMDescription;
    public static String htmlGMScoreboardPasterName;
    public static String htmlGMScoreboardPasterDesc;
    public static String htmlGMReportPasterName;
    public static String htmlGMReportPasterDesc;
    public static String htmlGMFleetScansName;
    public static String htmlGMFleetScansDesc;
    public static String htmlGMOrionName;
    public static String htmlGMOrionDesc;
    public static String htmlGMOrionV2Name;
    public static String htmlGMOrionV2Desc;

    public static String htmlGraphSelectMonths;
    public static String htmlGraphLinkToImage;

    public static String htmlScanShipCaption;
    public static String htmlScanShipId;
    public static String htmlScanShipTL;
    public static String htmlScanShipOwner;
    public static String htmlScanShipClan;
    public static String htmlScanShipNameWhenFirstSpotted;
    public static String htmlScanShipHistoryCaption;
    public static String htmlScanShipDate;
    public static String htmlScanShipChanges;
    public static String htmlScanShipFleetsWithShip;
    public static String htmlScanShipAllCaption;
    public static String htmlScanShipAllInfo;
    public static String htmlScoreboardCmpCaption;
    public static String htmlScoreboardVenad;
    public static String htmlScoreboardDetailsFor;
    public static String htmlScoreboardDetailsEntriesCaption;
    public static String htmlScoreboardOverviewPostCaption;
    public static String htmlScoreboardOverviewPostSubmit;
    public static String htmlScoreboardOverviewCompareList;
    public static String htmlScoreboardOverviewRemoveFromCompare;
    public static String htmlScoreboardOverviewCompare;
    public static String htmlScoreboardOverviewCompareTitle;
    public static String htmlScoreboardOverviewEntries;

    public static String htmlTrainingCaption;
    public static String htmlOpenTrainings;
    public static String htmlClosedTrainings;

    // BattleReportParser
    public static String reportParserInvalid;
    public static String reportParserInvalidDate;
    public static String reportParserInvalidLocation;
    public static String reportParserInvalidHeader;
    public static String reportParserExpectedAttackerFleet;
    public static String reportParserExpectedDefenderFleet;
    public static String reportParserNoAttackerShips;
    public static String reportParserNoDefenderShips;

    // FleetScanParser
    public static String fleetScanParserInvalid;
    public static String fleetScanParserAlienScan;

    // QReportParser
    public static String qreportParserResourceExpected;
    public static String qreportParserAttackAtExpected;
    public static String qreportParserInvalidLocation;
    public static String qreportParserFailedToParseAttr;

    // Orion related
    public static String secTypeHLRouteSector;
    public static String secTypeHLRouteStart;
    public static String secTypeHLRouteTarget;
    public static String secTypeHLWHDrop;
    public static String secTypeHLWHStart;
    public static String secTypeHLSafeSpot;
    public static String secTypeHLSafeSpotWH;
    public static String secTypeEmpty;
    public static String secTypeBlauerStern;
    public static String secTypeBlauerRiese;
    public static String secTypeRoterRiese;
    public static String secTypeWeisserZwerg;
    public static String secTypeGelberZwerg;
    public static String secTypeBraunerZwerg;
    public static String secTypeSchwarzerZwerg;
    public static String secTypePlanetTypA;
    public static String secTypePlanetTypB;
    public static String secTypePlanetTypC;
    public static String secTypePlanetTypD;
    public static String secTypePlanetTypE;
    public static String secTypePlanetTypF;
    public static String secTypePlanetTypG;
    public static String secTypePlanetTypH;
    public static String secTypePlanetTypI;
    public static String secTypeSchwarzesLoch;
    public static String secTypeSuperNovaTyp1;
    public static String secTypeSuperNovaTyp2;
    public static String secTypeNova;
    public static String secTypeNeutronengestern;
    public static String secTypeKomet;
    public static String secTypeRoterZwerg;
    public static String secTypeAsteroid;
    public static String secTypeAsteroidenFeld;
    public static String secTypeSternenNebel;
    public static String secTypePlanetarischerNebel;
    public static String secTypeHerbigHaroObjekt;
    public static String secTypeMeteorit;
    public static String secTypeSchwarzerStern;
    public static String secTypeDoppelgestirn;
    public static String secTypeDimensionsFalte;
    public static String secTypeTruemmerfeld;
    public static String secTypeMinenkolonie;
    public static String secTypeFabrikanlage;
    public static String secTypeKolonie;
    public static String secTypeAndroidenKolonie;
    public static String secTypePortal;
    public static String secTypePulsar;
    public static String secTypeAlienKolonie;
    public static String secTypeMeteorid;
    public static String secTypeHandelszentrum;
    public static String secTypeGrumorian;
    public static String secTypeNeutronenstern;
    public static String secTypeDreifachgestirn;
    public static String secTypeHLAlienSpawn;
    public static String loadNone;
    public static String loadPartial;
    public static String loadFull;

    public static String htmlOrionName;
    public static String htmlOrionDesc;
    public static String htmlOrionAlienManagement;
    public static String htmlOrionAlienManagementDesc;
    public static String htmlAvailableQuads;
    public static String htmlOrionResources;
    public static String htmlOrionWormholes;
    public static String htmlOrionBoni;
    public static String htmlOrionSectorInfo;
    public static String htmlOrionAttackerBonus;
    public static String htmlOrionDefenderBonus;
    public static String htmlOrionGuardBonus;
    public static String htmlOrionWLName;
    public static String htmlOrionWLLoad;
    public static String htmlOrionWLUnload;
    public static String htmlOrionWLStart;
    public static String htmlOrionWLTarget;
    public static String htmlOrionToHere;
    public static String htmlOrionFromHere;
    public static String htmlOrionRouting;
    public static String htmlOrionRouteStart;
    public static String htmlOrionRouteTarget;
    public static String htmlOrionRouteAction;
    public static String htmlOrionRefreshRoute;
    public static String htmlOrionQuadJumps;
    public static String htmlOrionSectorJumps;
    public static String htmlOrionRouteInfo;
    public static String htmlOrionNoRouteFound;
    public static String htmlOrionSelectFleet;
    public static String htmlOrionTotalWaitingTime;
    public static String htmlOrionMaxWaitingTime;
    public static String htmlOrionWaitingTime;
    public static String htmlOrionOptional;
    public static String htmlOrionOrJumpTime;
    public static String htmlOrionNone;
    public static String htmlOrionCurrentJumpTime;
    public static String htmlOrionSafeSpots;
    public static String htmlOrionShareRoute;
    public static String htmlOrionCalcRoute;
    public static String htmlOrionRouteNr;
    public static String htmlOrionLegend;
    public static String htmlOrionPersonalPortals;
    public static String htmlOrionEntryPortals;
    public static String htmlOrionWormhole;
    public static String htmlOrionFrom;
    public static String htmlOrionTo;
    public static String htmlOrionUnload;
    public static String htmlOrionRemainingJumpTime;
    public static String htmlOrionExpertSetting;
    public static String htmlOrionBlockTail;
    public static String htmlOrionBlockEntryPortal;
    public static String htmlOrionPersonalPortal;
    public static String htmlOrionClanPortal;
    public static String htmlOrionHourlyProduction;
    public static String htmlOrionSpawnAt;
    public static String htmlOrionAggressiveAliens;
    public static String htmlOrionRouteTimeOut;
    public static String htmlOrionRenderDark;

    public static String htmlAlienRaces;
    public static String htmlAlienRaceColumns;
    public static String htmlAlienRaceRemove;
    public static String htmlAlienRaceAdd;
    public static String htmlAlienRaceName;
    public static String htmlAlienRaceType;
    public static String htmlAlienRaceAggressive;
    public static String htmlAlienSpawns;
    public static String htmlAlienSpawnColumns;
    public static String htmlAlienSpawnAdd;
    public static String htmlAlienSpawnSector;
    public static String htmlAlienSpawnSectorHint;
    public static String htmlAlienRace;

    public static String htmlSectorsColumns;
    public static String htmlSectorsTitle;
    public static String htmlSectorsDesc;

    public static String htmlPortalColumns;
    public static String htmlPortals;
    public static String htmlPortalsDesc;

    // MyPlugin
    public static String pluginRepairTimeWarning;
    public static String pluginVenadDesc;
    public static String pluginCrackerDesc;
    public static String pluginAutoRemindDesc;
    public static String pluginAutoRemindAzDesc;
    public static String pluginLowPzWarningDesc;
    public static String pluginPortalDesc;
    public static String pluginDockLvlDescription;



    static {
        Resources.init(FAMILY, MSG.class);
    }
}
