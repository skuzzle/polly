package de.skuzzle.polly.sdk.resources;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import sun.reflect.Reflection;

/**
 * Manages internationalization of polly and its plugins. This class is based on Java's
 * {@link ResourceBundle ResourceBundles}. It is capable of reading resource property 
 * files for the {@link Locale} which is currently configured for polly. The 
 * <tt>Locale</tt> polly uses can be configured in <tt>polly.cfg</tt>.
 * 
 * <p>The {@link #get(String)} accesses the resource bundle for your plugin. Its 
 * recommended for your plugins to adapt to this structure when using 
 * internationalization:</p>
 * <ul>
 * <li>Create a <tt>Messages_&lt;locale&gt;.properties</tt> in the package of your 
 * {@link de.skuzzle.polly.sdk.PollyPlugin PollyPlugin} subclass for each locale you 
 * want to support</li>
 *  <li>Create a static constant in your PollyPlugin subclass referring to the resource 
 *  bundle: <tt>public final static String FAMILY = "my.plugin.Messages";</tt> where 
 *  <tt>my.plugin</tt> is the name of the package in which your properties file is 
 *  located</li>
 *  <li>Fill the properties file with key-value pairs of translated strings, for 
 *  example: <tt>my.command.description = Description text with placeholder: %s</tt></li>
 *  <li>In client code which is to be translated, use the following pattern to access 
 *  Strings from the resource bundle:
 *  <pre>
 *  private final static String DESC = "my.command.description";
 *  private final static PollyBundle MSG = Resources.get(MyPlugin.FAMILY);
 *  //...
 *  final String description = MSG.get(DESC, "test"); // "test" will be inserted for '%s' placeholder
 *  </pre>
 *  </li>
 * </ul>
 * 
 * Note that unlike Java's default behavior, polly supports reading those property files
 * in UTF-8 encoding.
 * 
 * @author Simon Taddiken
 * @since 1.1
 */
public class Resources {

    /** This field will be set by polly during initialization */
    static Locale pollyLocale;
    
    /** Reads property files in UTF-8 */
    private final static Control UTF8 = new UTF8Control();
    
    
    
    /**
     * Gets the {@link PollyBundle} for the provided resource bundle family in the 
     * current {@link #getLocale() polly locale}.
     * 
     * @param family Name of the resource bundle family to load.
     * @return The PollyBundle to access translated Strings.
     */
    public static PollyBundle get(String family) {
        final Class<?> caller = Reflection.getCallerClass();
        final ClassLoader cl = caller.getClassLoader();
        final ResourceBundle r = ResourceBundle.getBundle(
                family, pollyLocale, cl, UTF8);
        return new PollyBundle(r);
    }
    
    
    
    /**
     * Gets the locale for which polly has been configured in <tt>polly.cfg</tt>
     * @return The polly locale.
     */
    public static Locale getLocale() {
        return pollyLocale;
    }
}
