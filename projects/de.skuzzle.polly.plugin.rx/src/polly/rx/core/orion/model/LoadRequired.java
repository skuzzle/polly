package polly.rx.core.orion.model;

import polly.rx.MSG;


public enum LoadRequired {
    NONE(MSG.loadNone), PARTIAL(MSG.loadPartial), FULL(MSG.loadFull);
    
    public static LoadRequired parse(String s) {
        if (s.equalsIgnoreCase("nein") || s.equalsIgnoreCase("keine")) { //$NON-NLS-1$ //$NON-NLS-2$
            return NONE;
        } else if (s.equalsIgnoreCase("ja")) { //$NON-NLS-1$
            return FULL;
        } else if (s.equalsIgnoreCase("partiell")) { //$NON-NLS-1$
            return PARTIAL;
        }
        throw new IllegalArgumentException(s);
    }
    
    private final String name;
    
    
    private LoadRequired(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
