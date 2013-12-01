package de.skuzzle.polly.core.internal.mypolly;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;

public class MSG extends Constants {

    public final static String FAMILY = "de.skuzzle.polly.core.internal.mypolly.Translation"; //$NON-NLS-1$

    public static String statusVersion;
    public static String statusRuntime;
    public static String statusUptime;

    static {
        Resources.init(FAMILY, MSG.class);
    }
}
