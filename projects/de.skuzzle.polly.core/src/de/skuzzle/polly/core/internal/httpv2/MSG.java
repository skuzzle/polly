package de.skuzzle.polly.core.internal.httpv2;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;

public class MSG extends Constants {

    public final static String FAMILY = "de.skuzzle.polly.core.internal.httpv2.Translation"; //$NON-NLS-1$

    // IndexController
    public static String indexAdminCategory;
    public static String indexGeneralCategory;
    public static String indexStatusPage;
    public static String indexStatusDesc;
    public static String indexHomePage;
    public static String indexHomeDesc;

    // RoleController
    public static String roleAdminCategory;
    public static String roleManagerPage;
    public static String roleManagerDesc;

    // UserController
    public static String userAdminCategory;
    public static String userManagerPage;
    public static String userManagerDesc;

    static {
        Resources.init(FAMILY, MSG.class);
    }
}
