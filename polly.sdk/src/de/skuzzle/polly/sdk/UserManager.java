package de.skuzzle.polly.sdk;

import java.util.List;
import java.util.Set;

import de.skuzzle.polly.sdk.eventlistener.IrcUser;
import de.skuzzle.polly.sdk.eventlistener.UserListener;
import de.skuzzle.polly.sdk.exceptions.AlreadySignedOnException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;
import de.skuzzle.polly.sdk.exceptions.UserExistsException;
import de.skuzzle.polly.sdk.model.User;

/**
 * <p>The UserManager manages several user related tasks such as adding and removing 
 * users. Additionally you can add String-attributes to each user to store user-specific 
 * plugin settings.</p>
 * 
 * <p>Also this class provides simple access to the polly declarations feature. Each user
 * can declare own Expression using an user-unique identifier (this is a pure syntax
 * feature so there is no API for declaring identifiers). You can retrieve all 
 * declared identifiers of delete the for a specific user.</p>
 * 
 * <p>Polly has a buildin auto logoff function for users: if a users quits or leaves the
 * last channel he shared with polly, he will automatically be logged off.</p>
 * 
 * <p>All user retrieval functions work case insensitive in regard to the users name
 * or nickname, whatever the functions need.</p>
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface UserManager {
    
    /**
     * Classifies the userlevel 'unknown'
     */
    public final static int UNKNOWN = 0;
    
    /**
     * Classifies the userlevel 'registered'
     */
    public final static int REGISTERED = 10;
    
    /**
     * Classifies the userlevel 'member'
     */
    public final static int MEMBER = 100;
    
    /**
     * Classifies the userlevel 'admin'
     */
    public final static int ADMIN = 1000;
    
    
    
    /**
     * Factory method for creating users. The only thing it does is to create a new
     * user instance with the given parameters. It will not get persisted. You may
     * use this for updating users via {@link #updateUser(User, User)}.
     * 
     * @param name The user name.
     * @param password The users password as plaintext.
     * @param userLevel The users level.
     * @return The created user instance.
     */
    public User createUser(String name, String password, int userLevel);
    
    
    
    /**
     * Gets a signed on user instance. Use this method to retrieve the database user 
     * instance of a logged in user.
     * 
     * @param user The irc user instance of the user to retrieve.
     * @return The User instance if the given irc user is logged in or <code>null</code>
     *      otherwise.
     */
    public abstract User getUser(IrcUser user);
    
    
    
    /**
     * Retrieves a user with the given registered name from the database.
     * @param registeredName The name of the user to retrieve.
     * @return A database managed user instance if one was found or <code>null</code>
     *      otherwise.
     */
    public abstract User getUser(String registeredName);
    
    
    
    /**
     * Deletes a declared identifier from the users namespace. This method will have
     * no effect of predefined functions or constants.
     * 
     * @param user The user for whom the declaration shall be removed.
     * @param id The identifier of the declaration.
     */
    public abstract void deleteDeclaration(User user, String id);
    
    
    
    /**
     * Gets a set of all declared identifiers by the given user.
     * @param user The User whose declarations shall be retrieved.
     * @return A set of strings containing the identifiers. This may be empty but not
     *      <code>null</code>.
     */
    public abstract Set<String> getDeclaredIdentifiers(User user);
	
    
    
    /**
     * Returns a String representation including type and parameters for a declaration.
     * @param user The User whose declarations shall be retrieved.
     * @param declaration The name of the declaration to inspect.
     * @return A String representation of the declaration. <code>null</code> if the
     *          declaration does not exist.
     */
    public abstract String inspect(User user, String declaration);
    
    
    
    /**
     * Updates an existing user. All attributes except the name of the existing user 
     * will be overridden with the values from the parameter <code>updated</code>. 
     * 
     * @param old The existing user.
     * @param updated The values to update the existing user with.
     * @return The updated, database managed user.
     */
    public abstract User updateUser(User old, User updated);
    
    
    
    /**
     * Adds a new User.
     * 
     * @param name The users name.
     * @param password The users password in plaintext. This will then be stored as a
     *      hash value.
     * @param level A number describing this users level.
     * @throws UserExistsException If a user with the same name already exists.
     * @throws DatabaseException If storing the new user to the database fails for any
     *      reason.
     */
	public abstract void addUser(String name, String password, int level) 
	        throws UserExistsException, DatabaseException;
	
	
	
	/**
	 * Deletes a user from the database.
	 * 
	 * @param user The user to delete.
	 * @throws UnknownUserException If the user you are trying to delete does not exist.
	 * @throws DatabaseException If deleting the user from the database fails for any
     *      reason.
	 */
	public abstract void deleteUser(User user) 
	        throws UnknownUserException, DatabaseException;

	
	
	/**
	 * <p>Logs on a user. For a logged on user, the method {@link #isSignedOn(User)} will
	 * always return <code>true</code>. He will be logged on until method 
	 * {@link #logoff(User)} has been called.</p>
	 * 
	 * <p>A logged in user may execute commands which are set to be executable only for
	 * registered users.</p>
	 * 
	 * @param from The nickname of the irc user who tries to login.
	 * @param registeredName The name of the user to log on.
	 * @param password The users password as plaintext.
	 * @return The database managed user instance if the login was successful, 
	 *     <code>null</code> otherwise.
	 * @throws UnknownUserException If the user you are trying to logon does not exist.
	 * @throws AlreadySignedOnException If the user is already signed on.
	 */
	public abstract User logon(String from, String registeredName, String password) 
	        throws UnknownUserException, AlreadySignedOnException;
	
	
	
	/**
	 * Marks the given user as logged off.
	 * {@link #isSignedOn(User)} will now return <code>false</code> for this user.
	 * 
	 * @param user The user to logoff.
	 * @throws UnknownUserException
	 */
	public abstract void logoff(User user) throws UnknownUserException;
	
	
	/**
	 * Logs off a user by its irc name.
	 * @param user The {@link IrcUser} object of the user to logoff.
	 * @see #logoff(User)
	 */
	public abstract void logoff(IrcUser user);
	
	
	
	/**
	 * Determines whether the given user is currently signed on.
	 * @param user The {@link IrcUser} object of the user to check.
	 * @return <code>true</code> if a user with the IrcUsers nickname is currently
	 *     logged in.
	 */
	public abstract boolean isSignedOn(IrcUser user);
	
	
	
	/**
	 * Determines whether the given user is currently signed on.
	 * @param user The user to check.
	 * @return <code>true</code> if the given user is currently logged in.
	 */
	public abstract boolean isSignedOn(User user);
	
	
	/**
	 * Gets a list of all registered users.
	 * 
	 * @return The registered users.
	 * @since 0.6.1
	 */
	public abstract List<User> getRegisteredUsers();
	
	
	
	/**
	 * <p>Adds an attribute to all users. You can add String attributes to all users to
	 * store user-specific information for your plugin. Having an {@link User} instance,
	 * you can retrieve the users value for an attribute via 
	 * {@link User#getAttribute(String)}.</p>
	 * 
	 * <p>Mind that attribute names must be unique. If you try to add an attribute which 
	 * already exists, this method does nothing. This might lead to interference with
	 * other plugins!</p>
	 * 
	 * @param name the new attributes name.
	 * @param defaultValue The default value for the new attribute. This value will be 
	 *     assigned to each user.
	 * @throws DatabaseException If storing the new attribute fails for any reason.
	 */
	public abstract void addAttribute(String name, String defaultValue) 
	        throws DatabaseException;
	
	
	
	/**
	 * Removes an attribute for all users.
	 * 
	 * @param name The attribute name to remove.
	 * @throws DatabaseException If removing the attribute fails for any reason.
	 */
	public abstract void removeAttribute(String name) throws DatabaseException;
	
	
	/**
	 * Adds a listener to receive logon/logoff events.
	 * 
	 * @param listener The listener to add.
	 * @since 0.6
	 */
	public void addUserListener(UserListener listener);
	
	
	
    /**
     * Adds a listener to receive logon/logoff events.
     * 
     * @param listener The listener to add.
     * @since 0.6
     */
	public void removeUserListener(UserListener listener);
}
