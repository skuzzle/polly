package de.skuzzle.polly.http.api;

/**
 * <p>Controllers are used in conjunction with 
 * {@link HttpServletServer HttpServletServers}. They provided annotated handler methods 
 * for various {@link HttpEvent HttpEvents}. This is a quick example for a Controller 
 * which may handle a user database:</p>
 * 
 * <pre>
 * public class UserController extends Controller {
 *     private Persistence p; // Some abstract persistence manager for this sample
 *     
 *     public UserController(Persistence p) {
 *         this.p = p;
 *     }
 *     
 *     &#64;Override
 *     protected Controller createInstance() {
 *         return new UserController(p);
 *     }
 *     
 *     
 *     &#64;Post("/addUser")
 *     public HttpAnswer addUser(&#64;Param("name) String name) {
 *         User user = new User(name);
 *         this.p.persist(user);
 *         HttpAnswers.createStringAnswer("User added");
 *     }
 *     
 *     &#64;Post("/deleteUser")
 *     public void deleteUser(&#64;Param("name") String name) {
 *         User user = this.p.find(name);
 *         this.p.delete(user);
 *         HttpAnswers.createStringAnswer("User deleted");
 *     }
 * }
 * </pre>
 * 
 * When registering this controller with an instance of {@link HttpServletServer} it will
 * automatically call the annotated methods if a <tt>POST</tt> request to their URL
 * occurs.
 * 
 * @author Simon Taddiken
 */
public abstract class Controller {

    private HttpEvent event;

    
    /**
     * Gets the currently handled HttpEvent.
     * 
     * @return The HttpEvent.
     */
    protected final HttpEvent getEvent() {
        return this.event;
    }
    
    
    
    /**
     * Gets the HttpServer from which the current handled event originates. This is just
     * a call wrapper for <code>getEvent().getSource()</code>.
     * 
     * @return The HttpServer instance.
     */
    protected final HttpServer getServer() {
        return this.getEvent().getSource();
    }
    
    
    
    protected HttpSession getSession() {
        return this.getEvent().getSession();
    }
    
    
    
    /**
     * This method is intended to create an exact copy of this controller. It will be
     * used by {@link #bind(HttpEvent)}.
     * 
     * @return A new instance of this controller.
     */
    protected abstract Controller createInstance();
    
    
    
    /**
     * Creates a copy of this controller and binds it to the provided {@link HttpEvent}.
     * 
     * @param e The event.
     * @return A new instance of this controller.
     */
    public final Controller bind(HttpEvent e) {
        Controller result = this.createInstance();
        result.event = e;
        return result;
    }
}
