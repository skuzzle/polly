package polly.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.CommandManager;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownCommandException;
import de.skuzzle.polly.sdk.exceptions.UnknownSignatureException;
import de.skuzzle.polly.sdk.model.User;



public class CommandManagerImpl implements CommandManager {
	
	private static Logger logger = Logger.getLogger(CommandManagerImpl.class.getName());
	private Map<String, Command> commands;
	private Set<String> ignoredCommands;
	
	
	
	public CommandManagerImpl(String[] ignoredCommands) {
		this.commands = new HashMap<String, Command>();
		this.ignoredCommands = new HashSet<String>(
		        Arrays.asList(ignoredCommands));
	}
	
	
	
	@Override
	public synchronized void registerCommand(Command cmd) 
			throws DuplicatedSignatureException {
		
		if (cmd.getCommandName().length() < 3) {
			throw new IllegalArgumentException(
					"Too short commandname: " + cmd.getCommandName());
		}
		if (this.ignoredCommands.contains(cmd.getCommandName())) {
		    logger.warn("Ignoring command '" + cmd.getCommandName() + "'.");
		    return;
		}
		if (this.isRegistered(cmd)) {
			throw new DuplicatedSignatureException(cmd.getCommandName());
		}
		this.commands.put(cmd.getCommandName(), cmd);
		logger.debug("Command '" + cmd.getCommandName() + "' with " + 
				cmd.getSignatures().size() + " signatures successfuly registered");
	}
	

	
	@Override
	public synchronized void unregisterCommand(Command command) {
		Command cmd = this.getCommand(command.getCommandName());
		this.commands.remove(cmd.getCommandName());
		logger.debug("Unregistered command: " + command.getCommandName());
	}
	
	
	
	@Override
	public Collection<Command> getRegisteredCommands() {
		return Collections.unmodifiableCollection(this.commands.values());
	}
	
	
	
	@Override
	public boolean isRegistered(Command cmd) {
		return this.isRegistered(cmd.getCommandName());
	}
	
	
	
	@Override
	public boolean isRegistered(String name) {
		return this.commands.containsKey(name);
	}



	@Override
	public synchronized Command getCommand(Signature signature) 
	        throws UnknownSignatureException {
		logger.debug("Looking for '" + signature.toString() + "'.");
		
		Command cmd = this.getCommand(signature.getName());
		boolean found = false;
		for (Signature formal : cmd.getSignatures()) {
			if (formal.equals(signature)) {
				found = true;
				signature.setId(formal.getId());
				logger.debug("Signature found. Formal id is " + signature.getId());
			}
		}
		if (!found) {
			throw new UnknownSignatureException(signature);
		}
		
		assert cmd != null;
		return cmd;
	}



	@Override
	public Command getCommand(String name) {
		Command cmd = this.commands.get(name);
		if (cmd == null)	 {
			throw new UnknownCommandException(name);
		}
		return cmd;
	}
	
	
	
	public void executeString(String commandString) {
	    // TODO ISSUE 0000040
	}
	
	
	public void executeString(String commandString, User asUser) {
	    // TODO ISSUE 0000040
	}
}
