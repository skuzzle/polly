package polly.porat.events;

import polly.network.protocol.Request;


public abstract class FilteringProtocolListener implements ProtocolListener {

    private Request request;

    
    public FilteringProtocolListener(Request request) {
        if (request == null) {
            throw new NullPointerException("request");
        }
        this.request = request;
    }
    
    
    
    public void handleResponseReceived(ProtocolEvent e) {}
    
    public void handleErrorReceived(ProtocolEvent e) {}
    
    
    @Override
    public final void responseReceived(ProtocolEvent e) {
        if (e.getResponse().getResponseFor() == this.request.getId()) {
            this.handleResponseReceived(e);
        }
    }

    @Override
    public final void errorReceived(ProtocolEvent e) {
        if (e.getResponse().getResponseFor() == this.request.getId()) {
            this.handleErrorReceived(e);
        }
    }




}
