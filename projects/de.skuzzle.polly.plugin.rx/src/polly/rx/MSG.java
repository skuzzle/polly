package polly.rx;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;

public class MSG extends Constants {

    private final static String FAMILY = "polly.rx.Translation"; //$NON-NLS-1$

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

    // VenadCommand
    public static String venadHelp;
    public static String venadSig0Desc;
    public static String venadSig0User;
    public static String venadUnknownUser;
    public static String venadSuccess;

    static {
        Resources.init(FAMILY, MSG.class);
    }
}
