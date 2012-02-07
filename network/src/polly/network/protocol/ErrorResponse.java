package polly.network.protocol;


import polly.network.protocol.Constants.ErrorType;
import polly.network.protocol.Constants.ResponseType;


public class ErrorResponse extends Response {

    private static final long serialVersionUID = 1L;

    private ErrorType type;

    
    public ErrorResponse(Request request, ErrorType type) {
        super(request, ResponseType.ERROR);
        this.type = type;
    }
    
    
    
    public ErrorResponse(ErrorType type) {
        super(ResponseType.ERROR);
        this.type = type;
    }
    
    
    
    public ErrorType getErrorType() {
        return this.type;
    }
    
}
