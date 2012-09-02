package polly.core.http;


public class TrafficCounter {

    private int upload;
    private int download;
    
    
    
    public int getUpload() {
        return this.upload;
    }
    
    
    
    public int getDownload() {
        return this.download;
    }
    
    
    
    public synchronized void updateUpload(int len) {
        this.upload += len;
    }
    
    
    
    public synchronized void updateDownload(int len) {
        this.download += len;
    }
}