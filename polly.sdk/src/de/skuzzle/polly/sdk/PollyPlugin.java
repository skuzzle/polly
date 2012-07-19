package de.skuzzle.polly.sdk;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import de.skuzzle.polly.sdk.exceptions.PluginException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.roles.SecurityContainer;



/**
 * <p>This is the entry point class for all polly plugins. In order to provide your own
 * plugin functionality, you need to implement a class extending
 * this class.</p>
 * 
 * <p>Its important to implement a constructor with only one parameter of type 
 * {@link MyPolly}.</p>
 * 
 * <p>It will get passed a {@link MyPolly} instance, which provides access to all 
 * important polly features which you may use in your plugin.</p>
 * 
 * @author Simon
 * @since zero day
 */
public abstract class PollyPlugin extends AbstractDisposable 
        implements SecurityContainer {
	
	private MyPolly myPolly;
	private List<Command> commands;
	private CompositeDisposable disposables;
	private PluginState state = PluginState.NOT_LOADED;
	
	

	/**
	 * This is the main entry point procedure of your plugin. When extending this class
	 * for your own plugin, you need to provide a constructor with only a MyPolly 
	 * parameter which calls this super constructor.
	 * 
     * @param myPolly The MyPolly instance which provides access to all important polly
     *      features.
	 * @throws IncompatiblePluginException Your plugin may throw this exception if 
	 * 		it is not compatible with the current polly version. You can retrieve the
	 * 		current version via {@link MyPolly#getPollyVersion()} and check it with 
	 * 		{@link #requireSpecificPollyVersion(String)}
	 */
	public PollyPlugin(MyPolly myPolly) throws IncompatiblePluginException {
	    this.commands = new LinkedList<Command>();
	    this.disposables = new CompositeDisposable();
		this.myPolly = myPolly;
	}
	
	
	
	/**
     * <p>This method is called by polly upon shutdown and on manual unload of your 
     * plugin. Your plugin has to take care of all commands it registered and must 
     * unregister them all upon calling this method.
     * Furthermore, you can release all resources that will no longer be needed.</p>
     * 
     * <p>The default implementation unregisters all commands that have been added using
     * {@link #addCommand(Command)}. Additionally, all Disposable objects registered
     * with {@link #addDisposable(Disposable)} are disposed.
     * If you override it, ensure to call super.unload() in order to properly unload your
     * plugin.</p>
     * 
     * <p>One use case to override {@linkplain #dispose()} is to unregister all listeners 
     * you assigned to polly.</p>
     */
	@Override
	protected void actualDispose() throws DisposingException {
        for (Command command : this.commands) {
            try {
                this.myPolly.commands().unregisterCommand(command);
            } catch (Exception ignore) {};
        }
        
        this.disposables.dispose();
	}
	
	
	
	/**
	 * <p>This method returns all permissions that any command of this class needs in order
	 * to be executed.</p>
	 * 
	 * <p>You may override it in order to report further permissions to polly but must 
	 * remember to always call the super method if you do so.</p>
	 * 
	 * @return A set of all permissions required by the commands of this plugin.
	 */
	@Override
	public Set<String> getContainedPermissions() {
	    Set<String> result = new HashSet<String>();
	    for (Command command : this.commands) {
	        result.addAll(command.getContainedPermissions());
	    }
	    return result;
	}
	
	
	
	/**
	 * Using this method you can assign your commands permissions to the polly default
	 * roles, or you may create your own role and assign the permissions to it.
	 * 
	 * @param roleManager Pollys role manager.
	 */
	public void assignPermissions(RoleManager roleManager) 
	        throws RoleException, DatabaseException {}
	
	
	
	/**
	 * This method is called when all plugins have been loaded. From this point on, you
	 * may use all {@link MyPolly} features.
	 * 
	 * @throws PluginException If this plugin fails to initialize.
	 */
	public void onLoad() throws PluginException {}
	
	
	
	/**
	 * <p>This method is called when your plugin has been updated and before it has been
	 * loaded. Please note that not all polly features are available at this point. So 
	 * you may get an exception when invoking a function from {@link MyPolly}.</p>
	 * 
	 * <p>You may use it to delete unused files or alter table columns.</p>
	 * @throws PluginException When setup fails for any reason.
	 * @since 0.6.1
	 */
	public void onUpdate(MyPolly myPolly) throws PluginException {}
	
	
	
	/**
	 * This method is called after you plugin has been installed. Please note that not 
	 * all polly features are available at this point. So you may get an exception when
	 * invoking a function from {@link MyPolly}.	 * 
	 * 
	 * @param myPolly The {@link MyPolly} instance.
     * @throws PluginException When setup fails for any reason.
	 */
	public void onInstall(MyPolly myPolly) throws PluginException {}
	
	
	
	/**
	 * Adds a command to a local command list and tries to register it with polly. If you 
	 * use this method to add your commands, the default implementation of 
	 * {@link #dispose()} ensures that they are properly unloaded upon shutdown of your
	 * plugin.
	 * 
	 * @param command The command to add.
	 * @throws DuplicatedSignatureException If the command you want to add already 
	 *     exists.
	 */
	public void addCommand(Command command) 
	            throws DuplicatedSignatureException {
	    this.myPolly.commands().registerCommand(command);
	    this.commands.add(command);
	}
	
	
	
	/**
	 * Adds a disposable object to the local disposable list. These objects will be
	 * disposed on unloading ({@link #dispose()}) this command.
	 * 
	 * @param d The disposable object to add.
	 */
	public void addDisposable(Disposable d) {
	    this.disposables.add(d);
	}
	
	
	
	/**
	 * This method is used to uninstall your plugin. You may remove all local content 
	 * your plugin stored during time. This is the only place where you can call
	 * {@link PersistenceManager#dropTable(String)} to remove the database tables you
	 * installed.
	 * 
	 * @throws Exception Propagate any exception that occurs during uninstall process.
	 */
	public void uninstall() throws Exception {}
	
	
	
	/**
	 * <p>You can call this method if your plugin requires a very specific version of 
	 * polly to work with.</p>
	 * 
	 * <p>You may specify wildcard characters to check for a range of versions. For 
	 * example <code>2.x</code> will match every version that starts with a "2" followed
	 * by a dot and one other character.
	 * Further, you can check for multiple versions by separating them with ";" for 
	 * example <code>1.x;2.0x</code>
	 * If you need more complex version check for your plugin you may validate the 
	 * String returned by {@link MyPolly#getPollyVersion()} yourself.</p>
	 * 
	 * <p>This version check method ignores the buildnumber and therefore compares only
	 * the first part of the version String. Polly version Strings always have the 
	 * following format:</p>
	 * <pre>
	 * [version-name] <version-number>--#<build-number>
	 * </pre>
	 * <p>Where the <code>version-name</code> part can be left out, the other parts are 
	 * required. This method compares your input with the complete part before 
	 * <code>--#build<build-number></code>.
	 * 
	 * @param required The required polly version.
	 * @throws IncompatiblePluginException If the current version is not compatible with
	 * 		your plugin.
	 */
	public void requireSpecificPollyVersion(String required) 
			throws IncompatiblePluginException {
		String versions[] = required.split(";");
		// separate version from buildnumber 
		String[] parts = this.myPolly.getPollyVersion().split("--build#");
		if (parts.length != 2) {
			throw new IncompatiblePluginException("Invalid version number format");
		}
		String current = parts[0];
		
		for (String version : versions) {
			version = version.replace(".", "\\Q.\\E");
			version = version.replaceAll("x", ".?");
			if (!current.matches(version)) {
				throw new IncompatiblePluginException(
						"This plugin is not compatible with polly Version '" + current + 
						"'. Required polly Version is '" + required + "'");
			}
		}
	}
	
	
	
	/**
	 * Sets the state of this plugin.
	 * 
	 * @param state The plugin state.
	 * @since 0.6.1
	 */
	public void setPluginState(PluginState state) {
	    this.state = state;
	}
	
	

	/**
	 * Gets the current plugin state.
	 * 
	 * @return The plugin state.
	 * @since 0.6.1
	 */
    public PluginState getState() {
        return this.state;
    }
	
	
	
	/**
	 * @return The MyPolly instance.
	 */
	public final MyPolly getMyPolly() {
		return this.myPolly;
	}
	
	
	
	/**
	 * Returns the SDK version that your plugin uses.
	 * @return The plugin version as defined in the sdk's MANIFEST file.
	 */
	public final String getSdkVersion() {
	    return PollyPlugin.class.getPackage().getImplementationVersion();
	}
}
