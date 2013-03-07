package polly.rx.entities;


public enum RxRessource {
    CR, NRG, REK, ORE, ORG, SYNTH, FE, LM, SM, EM, RAD, ES, EG, ISO;
    
    public static RxRessource byOrdinal(int i) {
        for (RxRessource ress : RxRessource.values()) {
            if (ress.ordinal() == i) {
                return ress;
            }
        }
        
        throw new IllegalArgumentException("No such ordinal");
    }
}