package polly.rx.entities;


public enum RxRessource {
    CR, NRG, REK, ERZ, ORG, SYNT, FE, LM, SM, EM, RAD, ES, EG, ISO;
    
    
    public static RxRessource parseRessource(String s) {
        final RxRessource ress = RxRessource.valueOf(s.toUpperCase());
        return ress;
    }
}