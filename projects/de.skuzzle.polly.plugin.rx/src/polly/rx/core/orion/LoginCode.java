package polly.rx.core.orion;

import java.util.Date;

public class LoginCode {

    private final Date date;
    private final String code;
    private final String imgHash;



    public LoginCode(Date date, String code, String imgHash) {
        this.date = date;
        this.code = code;
        this.imgHash = imgHash;
    }



    public String getImgHash() {
        return this.imgHash;
    }



    public String getCode() {
        return this.code;
    }



    public Date getDate() {
        return this.date;
    }
}
