package polly.rx.core.orion;

import java.util.regex.Pattern;

import polly.rx.captcha.RxCaptchaKiller;
import de.skuzzle.polly.sdk.time.Time;


public class LoginCodeManager {
    
    private final Pattern CODE_PATTREN = Pattern.compile("[a-z0-9]",  //$NON-NLS-1$
            Pattern.CASE_INSENSITIVE);
    
    private final RxCaptchaKiller captchaKiller;
    
    
    
    public LoginCodeManager(RxCaptchaKiller captchaKiller) {
        this.captchaKiller = captchaKiller;
    }
    
    
    public boolean testCodeValid(String code) {
        return code.length() == 4 && CODE_PATTREN.matcher(code).matches();
    }
    
    
    
    public synchronized LoginCode getCurrentCode() {
        final String code = this.captchaKiller.decodeCurrentCaptcha();
        return new LoginCode(Time.currentTime(), code);
    }
}
