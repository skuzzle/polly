package polly.rx.core.orion.model;

import java.awt.image.BufferedImage;
import java.util.Random;

import polly.rx.MSG;
import polly.rx.httpv2.view.orion.Images;


public enum SectorType {
    NONE("", 0, 1), //$NON-NLS-1$
    UNKNOWN("", 300, 301), //$NON-NLS-1$
    HIGHLIGHT_SECTOR(MSG.secTypeHLRouteSector, 200),
    HIGHLIGHT_START(MSG.secTypeHLRouteStart, 201), 
    HIGHLIGHT_TARGET(MSG.secTypeHLRouteTarget, 202),
    HIGHLIGHT_WH_DROP(MSG.secTypeHLWHDrop, 203),
    HIGHLIGHT_WH_START(MSG.secTypeHLWHStart, 204),
    HIGHLIGHT_SAFE_SPOT(MSG.secTypeHLSafeSpot, 205),
    HIGHLIGHT_SAFE_SPOT_WL(MSG.secTypeHLSafeSpotWH, 206),
    EMPTY(MSG.secTypeEmpty, 1, 9),
    BLAUER_STERN(MSG.secTypeBlauerStern, 11),
    BLAUER_RIESE(MSG.secTypeBlauerRiese, 12),
    ROTER_RIESE(MSG.secTypeRoterRiese, 13),
    WEISSER_ZWERG(MSG.secTypeWeisserZwerg, 14),
    GELBER_ZWERG(MSG.secTypeGelberZwerg, 15),
    ROTER_ZWERG(MSG.secTypeRoterZwerg, 16),
    BRAUNER_ZWERG(MSG.secTypeBraunerZwerg, 17),
    SCHWARZER_ZWERG(MSG.secTypeSchwarzerZwerg, 18),
    PLANET_TYP_A(MSG.secTypePlanetTypA, 30),
    PLANET_TYP_B(MSG.secTypePlanetTypB, 31),
    GRUMORIAN(MSG.secTypeGrumorian, 31),
    PLANET_TYP_C(MSG.secTypePlanetTypC, 32),
    PLANET_TYP_D(MSG.secTypePlanetTypD, 33),
    PLANET_TYP_E(MSG.secTypePlanetTypE, 34),
    PLANET_TYP_F(MSG.secTypePlanetTypF, 35),
    PLANET_TYP_G(MSG.secTypePlanetTypG, 36),
    PLANET_TYP_H(MSG.secTypePlanetTypH, 37),
    PLANET_TYP_I(MSG.secTypePlanetTypI, 38),
    SCHWARZES_LOCH(MSG.secTypeSchwarzesLoch, 50),
    SUPER_NOVA_TYP_1(MSG.secTypeSuperNovaTyp1, 51),
    SUPER_NOVA_TYP_2(MSG.secTypeSuperNovaTyp2, 52),
    NOVA(MSG.secTypeNova, 53),
    NEUTRONENGESTERN(MSG.secTypeNeutronengestern, 54),
    NEUTRONENSTERN(MSG.secTypeNeutronenstern, 54),
    PULSAR(MSG.secTypePulsar, 55),
    KOMET(MSG.secTypeKomet, 60),
    METEORID(MSG.secTypeMeteorid, 61),
    ASTEROID(MSG.secTypeAsteroid, 62),
    ASTEROIDENFELD(MSG.secTypeAsteroidenFeld, 63),
    STERNEN_NEBEL(MSG.secTypeSternenNebel, 70),
    PLANETARISCHER_NEBEL(MSG.secTypePlanetarischerNebel, 73),
    HERBIG_HARO_OBJECT(MSG.secTypeHerbigHaroObjekt, 77),
    METEORIT(MSG.secTypeMeteorit, 61),
    SCHWARZER_STERN(MSG.secTypeSchwarzerStern, 80),
    DOPPELGESTIRN(MSG.secTypeDoppelgestirn, 81),
    DIMENSIONSFALTE(MSG.secTypeDimensionsFalte, 83),
    TRUEMMERFELD(MSG.secTypeTruemmerfeld, 84),
    MINENKOLONIE(MSG.secTypeMinenkolonie, 85),
    FABRIKANLAGE(MSG.secTypeFabrikanlage, 86),
    ALIENKOLONIE(MSG.secTypeAlienKolonie, 87),
    KOLONIE(MSG.secTypeKolonie, 88),
    ANDROIDENKOLONIE(MSG.secTypeAndroidenKolonie, 89),
    HANDELSZENTRUM(MSG.secTypeHandelszentrum, 90),
    EINTRITTS_PORTAL(MSG.secTypePortal, 100),
    ;

    
    
    public static SectorType byName(String name) {
        for (final SectorType type : SectorType.values()) {
            if (type.toString().equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException(name);
    }
    
    
    
    public static SectorType[] HIGHLIGHTS = {
        HIGHLIGHT_START,
        HIGHLIGHT_TARGET,
        HIGHLIGHT_SECTOR,
        HIGHLIGHT_WH_START,
        HIGHLIGHT_WH_DROP,
        HIGHLIGHT_SAFE_SPOT,
        HIGHLIGHT_SAFE_SPOT_WL
    };
    
    
    private final static Random RANDOM = new Random();
    
    private final String name;
    private final int minId;
    private final int maxId;
    private BufferedImage image;
    
    
    private SectorType(String name, int id) {
        this(name, id, id + 1);
    }
    
    
    
    private SectorType(String name, int minId, int maxId) {
        assert minId >= 0 && minId < maxId;
        this.name = name;
        this.minId = minId;
        this.maxId = maxId;
    }
    
    
    
    public boolean isHighlight() {
        switch (this) {
        case HIGHLIGHT_SECTOR:
        case HIGHLIGHT_START:
        case HIGHLIGHT_WH_DROP:
        case HIGHLIGHT_WH_START:
        case HIGHLIGHT_SAFE_SPOT:
        case HIGHLIGHT_SAFE_SPOT_WL:
            return true;
        default: return false;
        }
    }
    
    
    
    public int getId() {
        return this.minId;
    }
    
    
    
    public int getMaxId() {
        return this.maxId;
    }
    
    
    
    public String getImgName() {
        switch (this) {
        case NONE:    return "n.gif"; //$NON-NLS-1$
        case UNKNOWN: return "u.gif"; //$NON-NLS-1$
        default: 
            return "" + (this.minId + RANDOM.nextInt(this.maxId - this.minId)) + ".gif";  //$NON-NLS-1$//$NON-NLS-2$
        } 
    }
    
    
    
    public synchronized BufferedImage getImage() {
        if (this == EMPTY) {
            final int r = RANDOM.nextInt(Images.EMPTY_ROOM.length);
            return Images.EMPTY_ROOM[r];
        } else if (this.image == null) {
           this.image = Images.getImage(this.getImgName()); 
        }
        return this.image;
    }
    
    
    
    @Override
    public String toString() {
        return this.name;
    }
}
