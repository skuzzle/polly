package de.skuzzle.polly.sdk;

import java.util.Collection;

import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownCommandException;
import de.skuzzle.polly.sdk.exceptions.UnknownSignatureException;


/**
 * This class manages all command related tasks.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface CommandManager {
	
	/**
	 * Registers a new command and all of its signatures.
	 * 
	 * @param cmd The command to add.
	 * @throws DuplicatedSignatureException If the given command is already registered.
	 * @throws IllegalArgumentException If the commands name is shorter than 3 characters.
	 */
	public abstract void registerCommand(Command cmd) throws DuplicatedSignatureException;
	
	
	
	/**
	 * Unregisters a command and all of its signatures.
	 * 
	 * @param command The command which shall be removed.
	 * @throws UnknownCommandException If the command you are trying to unregister
	 * 		does not exist.
	 */
	public abstract void unregisterCommand(Command command) 
			throws UnknownCommandException;
	
	
	
	/**
	 * Returns a readonly {@link Collection} of all currently registered commands.
	 * @return A set of commands.
	 */
	public abstract Collection<Command> getRegisteredCommands();
	
	
	
	/**
	 * Determines whether the given command is currently registered.
	 * @param cmd The command to check.
	 * @return <code>true</code> if the command is registered, <code>false</code> 
	 * 		otherwise.
	 */
	public abstract boolean isRegistered(Command cmd);
	
	
	
	/**
	 * Determines whether a command with given name is currently registered.
	 * @param name The command name to check.
	 * @return <code>true</code> if the command is registered, <code>false</code> 
	 * 		otherwise.
	 */
	public abstract boolean isRegistered(String name);
	
	
	
	/**
	 * Retrieves a command with the given signature. If a command with the given 
	 * signature exists, the passed signatures id is set to the found signatures
	 * id.
	 * 
	 * @param signature The signature of the command to retrieve.
	 * @return The found command.
	 * @throws UnknownSignatureException If no command with the given signature exists.
	 * @throws UnknownCommandException If no command with the signatures name exists.
	 */
	public abstract Command getCommand(Signature signature) 
			throws UnknownSignatureException;
	
	
	
	/**
	 * <p>Retrieves a command with given name, disregarding any signatures.</p>
	 * 
	 * <p>Attention: Executing the resulting command with any signature may result in 
	 * random errors, as it does not check whether the signature is compatible to this 
	 * command. This method therefore should only be used to retrieve infos about
	 * the command with given name but not for executing it!</p>
	 * 
	 * @param name The name of the command to retrieve.
	 * @return The command with the given name.
	 * @throws UnknownCommandException If no command with the name exists.
	 */
	public abstract Command getCommand(String name);
}