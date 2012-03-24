package polly.network;


public class NetworkException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public NetworkException() {
        super();
    }

    public NetworkException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public NetworkException(String arg0) {
        super(arg0);
    }

    public NetworkException(Throwable arg0) {
        super(arg0);
    }
}
