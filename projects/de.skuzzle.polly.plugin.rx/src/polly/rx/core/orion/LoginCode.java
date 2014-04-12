package polly.rx.core.orion;

import java.util.Date;

public class LoginCode {

    private final Date date;
    private final String code;



    public LoginCode(Date date, String code) {
        this.date = date;
        this.code = code;
    }


    
    public String getCode() {
        return this.code;
    }



    public Date getDate() {
        return this.date;
    }
}
