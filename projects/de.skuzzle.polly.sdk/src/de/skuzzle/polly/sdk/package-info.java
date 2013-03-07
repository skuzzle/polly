/**
 * This is the sdk Package for polly, allowing you to write your own polly plugins.
 * 
 * Writing simple own plugins is ridiculous easy if you follow these steps:
 * <ul>
 * <li>Create a new java project and add the sdk package to the buildpath</li>
 * <li>Create a class which will be your entrypoint 
 * <li>Make your class extend the class {@link de.skuzzle.polly.sdk.PollyPlugin}</li>
 * <li>Implement a constructor <code>YourClass(MyPolly myPolly)</code> and place
 * 		all your initialization code in it</li>
 * <li>Optional: Register entities for persistence service</li>
 * <li>Optional: Register commands</li>
 * <li>Compile your plugin to a jar file and place it in pollys plugin folder</li>
 * </ul>
 * 
 * The next step is to create a .properties file which contains bootstrapping infos
 * for your plugin. It must contain the following keys:
 * <pre>
 * jarfile
 * name
 * description
 * version
 * developer
 * entrypoint
 * </pre>
 * 
 * <p>The keys <code>jarfile, name</code> and <code>entrypoint</code> must be defined, the
 * other are optional.</p>
 * 
 * <p><code>jarfile</code> is the name of your plugins compiled jar file.
 * <code>entrypoint</code> is the full qualified name of your class which extends
 * {@linkplain de.skuzzle.polly.sdk.PollyPlugin} and <code>name</code> is your 
 * plugins name.</p>
 * 
 * <p>If you followed these steps correctly and your plugins name is not in the plugin-
 * exclusion list of polly, it will be loaded automatically if polly starts.</p> 
 * 
 * <p>Additionally, your plugin can use the Apache log4j logging package to log info and 
 * error messages. You can access the polly root logger for plugins via</p>
 * 
 * <pre>
 * private Logger logger = Logger.getLogger(myPolly.getLoggerName(this.getClass()));
 * </pre>
 * 
 * or define an own log file for your plugin.
 * 
 * This is a simple plugin implementation:
 * <pre>
 * import de.skuzzle.polly.sdk.EntryPoint;
 * import de.skuzzle.polly.sdk.IncompatiblePluginException;
 * 
 * public class PollyPlugin extends PollyPlugin {
 * 	   private Command testcmd;
 * 
 *     public PollyPlugin(MyPolly myPolly) throws IncompatiblePluginException {
 *         super(myPolly);
 *         
 *         // place all your plugin initializations here. for example check the version:
 *         this.requireSpecificPollyVersion("0.x;1.x;2.0");
 *         
 *         // or register your commands:
 *         this.addCommand(new TestCommand(myPolly));
 *         
 *         // additionally, this is the only place to register your entities:
 *         myPolly.persistence().registerEntity(Employee.class);
 *         myPolly.persistence().registerEntity(OtherEntity.class);
 *     }
 * }
 * </pre>
 * 
 * <p>The class {@link de.skuzzle.polly.sdk.MyPolly} will grant you access to many polly 
 * features that you can use in your plugin to provide a wide range of new features.</p>
 * 
 * <p>Please note that you may not access persistence or irc features while initializing 
 * the plugin. These features are available for use by the time the 
 * {@link de.skuzzle.polly.sdk.PollyPlugin#onLoad()} was called.</p>
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
package de.skuzzle.polly.sdk;