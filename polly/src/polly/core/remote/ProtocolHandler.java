package polly.core.remote;


import polly.core.remote.AdministrationManager.LoginResult;
import polly.network.events.ConnectionListener;
import polly.network.events.NetworkEvent;
import polly.network.events.ObjectReceivedEvent;
import polly.network.events.ObjectReceivedListener;
import polly.network.protocol.Constants;
import polly.network.protocol.Constants.ErrorType;
import polly.network.protocol.Constants.RequestType;
import polly.network.protocol.Constants.ResponseType;
import polly.network.protocol.ErrorResponse;
import polly.network.protocol.Request;
import polly.network.protocol.Response;



public class ProtocolHandler implements ObjectReceivedListener, ConnectionListener {

    private AdministrationManager adminManager;
    
    
    public ProtocolHandler(AdministrationManager adminManager) {
        this.adminManager = adminManager;
    }
    
    
    
    @Override
    public void objectReceived(ObjectReceivedEvent e) {
        if (e.isRequest()) {
            this.handleRequest(e);
        } else {
            e.getSource().send(new Response(ResponseType.IGNORED));
        }
    }
    
    
    
    private void handleRequest(ObjectReceivedEvent e) {
        Request request = (Request) e.getObject();
        
        if (request.is(RequestType.LOGIN)) {
            this.handleLogin(e);
            return;
        }
        
        if (!e.getSource().isAuthenticated()) {
            e.getSource().send(new ErrorResponse(request, ErrorType.INSUFFICIENT_RIGHTS));
            return;
        }
        
        
        switch (request.getType()) {
        case LIVE_LOG_ON:
            this.adminManager.enableLiveLog(e.getSource());
            e.getSource().send(new Response(ResponseType.LIVE_LOG_ON));
            break;
        case LIVE_LOG_OFF:
            this.adminManager.disableLiveLog(e.getSource());
            e.getSource().send(new Response(ResponseType.LIVE_LOG_OFF));
            break;
        case LOGOUT:
            this.adminManager.logout(e.getSource());
            e.getSource().send(new Response(ResponseType.LOGOUT));
        }
    }
    
    
    
    private void handleLogin(ObjectReceivedEvent e) {
        Request request = (Request) e.getObject();
        String userName = (String) request.getPayload().get(Constants.USER_NAME);
        String password = (String) request.getPayload().get(Constants.PASSWORD);
        Response answer = null;
        
        LoginResult login = this.adminManager.login(e.getSource(), userName, password);
        switch (login) {
        case INSUFICCIENT_RIGHTS:
            answer = new ErrorResponse(ErrorType.INSUFFICIENT_RIGHTS); break;
        case INVALID_PASSWORD:
            answer = new ErrorResponse(ErrorType.INVALID_PASSWORD); break;
        case UNKNOWN_USER:
            answer = new ErrorResponse(ErrorType.UNKNOWN_USER); break;
        case SUCCESS:
            answer = new Response(ResponseType.LOGGED_IN); break;
        }
        
        e.getSource().send(answer);
    }
    
    
    
    @Override
    public void connectionAccepted(NetworkEvent e) {}



    @Override
    public void connectionClosed(NetworkEvent e) {
        this.adminManager.disableLiveLog(e.getSource());
        this.adminManager.disableIrcForward(e.getSource());
    }

}
