package polly.configuration;


public interface Reconfigurable {

    public abstract void reconfigure(PollyConfiguration cfg);
}