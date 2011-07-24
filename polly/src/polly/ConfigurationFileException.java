package polly;

/**
 * Adapted from bibi
 * 
 * @author F.Nolte
 *
 */
public class ConfigurationFileException extends Exception {

  
  private static final long serialVersionUID = -1232701338280347182L;


  private Exception triggerException;

  
  public ConfigurationFileException(Exception triggerException, String message) {
    super(message);
    this.triggerException = triggerException;
  }

    
  public ConfigurationFileException(String message) {
    this((Exception) null, message);
  }

  
  public Exception getTriggerException() {
    return triggerException;
  }

}
