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
    public static String indexRestart;
    public static String indexShutdown;

    // RoleController
    public static String roleAdminCategory;
    public static String roleManagerPage;
    public static String roleManagerDesc;

    // SessionController
    public static String sessionAdminCategory;
    public static String sessionManagerPage;
    public static String sessionManagerDesc;
    public static String sessionRequiredPermission;
    
    // UserController
    public static String userAdminCategory;
    public static String userManagerPage;
    public static String userManagerDesc;
    public static String userUnknownId;
    public static String userDeleteSuccess;
    public static String userDatebaseFail;
    public static String userPasswordMismatch;
    public static String userPasswordChanged;
    public static String userInvalidFormat;
    public static String userAlreadyExists;
    public static String userAdded;
    public static String userAttributeConstraintFail;
    public static String userRoleAdded;
    public static String userCantRemoveAdminRole;
    public static String userRoleRemoved;
    
    // UserTableModel
    public static String userTableColId;
    public static String userTableColName;
    public static String userTableColNick;
    public static String userTableColIdle;
    public static String userTableColLastAction;
    public static String userTableColLogin;
    public static String userTableColAction;
    public static String userTableEditTitle;
    public static String userTableDeleteTitle;
    
    // NewsManage
    public static String newsUnknownId;
    
    
    static {
        Resources.init(FAMILY, MSG.class);
    }
}
