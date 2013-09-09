package de.skuzzle.polly.core.internal.users;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import de.skuzzle.polly.core.internal.persistence.PersistenceManagerImpl;
import de.skuzzle.polly.core.parser.InputParser;
import de.skuzzle.polly.core.parser.InputScanner;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.declarations.DeclarationReader;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.ParentSetter;
import de.skuzzle.polly.core.parser.ast.visitor.resolving.TypeResolver;
import de.skuzzle.polly.core.parser.problems.ProblemReporter;
import de.skuzzle.polly.core.parser.problems.SimpleProblemReporter;
import de.skuzzle.polly.core.util.CaseInsensitiveStringKeyMap;
import de.skuzzle.polly.core.util.TypeMapper;
import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.Attribute;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.constraints.AttributeConstraint;
import de.skuzzle.polly.sdk.eventlistener.IrcUser;
import de.skuzzle.polly.sdk.eventlistener.UserEvent;
import de.skuzzle.polly.sdk.eventlistener.UserListener;
import de.skuzzle.polly.sdk.exceptions.AlreadySignedOnException;
import de.skuzzle.polly.sdk.exceptions.ConstraintException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.InvalidUserNameException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.exceptions.UnknownAttributeException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;
import de.skuzzle.polly.sdk.exceptions.UserExistsException;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.events.Dispatchable;
import de.skuzzle.polly.tools.events.EventProvider;
import de.skuzzle.polly.tools.events.Listeners;



/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 */
public class UserManagerImpl extends AbstractDisposable implements UserManager {

    private static Logger logger = Logger.getLogger(UserManagerImpl.class.getName());
    
    private final static AttributeConstraint NO_CONSTRAINT = new AttributeConstraint() {
        @Override
        public boolean accept(Types value) {
            return true;
        }
    };
    
    
    private final static FileFilter DECLARATION_FILTER = new FileFilter() {
        
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(".decl");
        }
    };
    
    
    private PersistenceManagerImpl persistence;

    /**
     * Stores the currently signed on users. Key: the nickname in lower case.
     */
    private Map<String, User> onlineCache;
    private File declarationCachePath;
    private Map<String, AttributeConstraint> constraints;
    private EventProvider eventProvider;
    private User admin;
    private boolean registeredStale;
    private List<User> registeredUsers;
    private boolean attributesStale;
    private List<AttributeImpl> allAttributes;
    private RoleManager roleManager;
    private final FormatManager formatter;
    
    
    
    public UserManagerImpl(PersistenceManagerImpl persistence, 
            String declarationCachePath, int tempVarLifeTime,
            boolean ignoreUnknownIdentifiers, EventProvider eventProvider, 
            RoleManager roleManager,
            FormatManager formatter) {
        this.formatter = formatter;
        this.eventProvider = eventProvider;
        this.persistence = persistence;
        this.roleManager = roleManager;
        this.onlineCache = Collections.synchronizedMap(
                new CaseInsensitiveStringKeyMap<User>());
        this.declarationCachePath = new File(declarationCachePath);
        this.constraints = new HashMap<String, AttributeConstraint>();
        try {
            logger.info("Reading declarations...");
            if (!this.declarationCachePath.exists()) {
                logger.warn("Declaration-cache directory does not exist. " +
                		"Trying to create folder structure");
                this.declarationCachePath.mkdirs();
            }
            Namespace.setDeclarationFolder(this.declarationCachePath);
			readDeclarations(this.declarationCachePath);
			logger.trace("done");
		} catch (IOException e) {
			logger.warn("No declarations restored", e);
		}
    }
    
    
    
    private static void readDeclarations(File folder) throws IOException {
        for (final File file : folder.listFiles(DECLARATION_FILTER)) {
            DeclarationReader dr = null;
            try {
                final String nsName = file.getName().substring(
                        0, file.getName().length() - 5);
                final Namespace ns = Namespace.forName(nsName);
                dr = new DeclarationReader(file, "ISO-8859-1", ns);
                dr.readAll();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (dr != null) {
                    dr.close();
                }
            }
        }
    }
    
    

    @Override
    public synchronized Set<String> getDeclaredIdentifiers(String namespace) {
        final Namespace ns = Namespace.forName(namespace);

        final Set<String> result = new HashSet<String>();
        for (final List<Declaration> decls : ns.getDeclarations().values()) {
            for (final Declaration decl : decls  ) {
                result.add(decl.getName().getId());
            }
        }
        return result;
    }
    
    
    
    public void setAdmin(User admin) {
        this.admin = admin;
    }
    
    
    
    public User getAdmin() {
        return this.admin;
    }
    
    
    
    @Override
    public User getUser(IrcUser user) {
        return this.onlineCache.get(user.getNickName());
    }
    
    
    
    @Override
    public User getUser(int id) {
        de.skuzzle.polly.core.internal.users.UserImpl result = 
            (de.skuzzle.polly.core.internal.users.UserImpl)  this.persistence.atomicRetrieveSingle(
            de.skuzzle.polly.core.internal.users.UserImpl.class, id);
        
        if (result != null) {
            result.setUserManager(this);
        }
        return result;
    }

    
    
    @Override
    public User getUser(String registeredName) {
        if (registeredName == null) {
            return null;
        }
        try {
            this.persistence.readLock();
            final de.skuzzle.polly.core.internal.users.UserImpl result = 
                this.persistence.findSingle(
                    de.skuzzle.polly.core.internal.users.UserImpl.class,
                    "USER_BY_NAME", registeredName);
            
                if (result != null) {
                    result.setUserManager(this);
                }
                return result;
        } finally {
            this.persistence.readUnlock();
        }
    }
    
    

    @Override
    public User updateUser(User old, User updated) {
        try {
            this.persistence.writeLock();
            this.persistence.startTransaction();
            old.setHashedPassword(updated.getHashedPassword());
            this.persistence.commitTransaction();
            logger.info("User '" + old + "' updated to '" + updated + "'");
        } catch (DatabaseException e) {
            e.printStackTrace();
        } finally {
            this.persistence.writeUnlock();
        }
        return old;
    }
    
    
    
    public void addUser(User user) 
                throws UserExistsException, DatabaseException, InvalidUserNameException {
        
        Matcher m = USER_NAME_PATTERN.matcher(user.getName());
        if (!m.matches()) {
            throw new InvalidUserNameException(user.getName());
        }
        
        
        try {
            this.persistence.writeLock();
            User check = this.persistence.findSingle(User.class, "USER_BY_NAME", 
                    user.getName());
            
            if (check != null) {
                logger.trace("User already exists.");
                throw new UserExistsException(check);
            }
            this.persistence.startTransaction();
            this.persistence.persist(user);
            this.persistence.commitTransaction();
            this.registeredStale = true;
            
            // Assign registered role to new user.
            try {
                this.roleManager.assignRole(user, RoleManager.DEFAULT_ROLE);
            } catch (RoleException ignore) {
                logger.warn("Ignoring RoleException", ignore);
            }
            
            logger.info("Added user " + user);
        } finally {
            this.persistence.writeUnlock();
        }
    }
    
    

    @Override
    public User addUser(String name, String password) 
            throws UserExistsException, DatabaseException, InvalidUserNameException {
        User newUser = this.createUser(name, password);
        this.addUser(newUser);
        return newUser;
    }

    
    
    @Override
    public void deleteUser(User user) throws DatabaseException {
        try {
            this.persistence.writeLock();
            this.logoff(user);
            this.persistence.startTransaction();
            this.persistence.remove(user);
            this.persistence.commitTransaction();
            this.registeredStale = true;
            logger.info("Deleted user " + user);
        } finally {
            this.persistence.writeUnlock();
        }
    }

    
    
    @Override
    public synchronized User logon(String from, String registeredName, String password) 
            throws UnknownUserException, AlreadySignedOnException {
        logger.info("Trying to log on user '" + registeredName + "'.");
        User user = this.getUser(registeredName);
        if (user == null) {
            throw new UnknownUserException(registeredName);
        }
        
        this.checkAlreadySignedOn(user);
        
        if (user.checkPassword(password)) {
            user.setCurrentNickName(from);
            this.onlineCache.put(user.getCurrentNickName(), user);
            logger.info("Irc User " + from + " successfully logged in as " + 
                    registeredName);
            
            ((de.skuzzle.polly.core.internal.users.UserImpl) user).setLoginTime(Time.currentTimeMillis());
            UserEvent e = new UserEvent(this, user);
            this.fireUserSignedOn(e);
            return user;
        }
        
        logger.warn("Login from '" + from + "' with user name '" + registeredName + 
                "' failed: Invalid password.");
        return null;
    }
    
    
    
    public synchronized User logonWithoutPassword(String from) throws 
                AlreadySignedOnException, UnknownUserException {
        logger.info("Trying to autologon user '" + from + "'.");
        User user = this.getUser(from);
        if (user == null) {
            throw new UnknownUserException(from);
        }
        
        this.checkAlreadySignedOn(user);
        
        user.setCurrentNickName(from);
        this.onlineCache.put(user.getCurrentNickName(), user);
        logger.info("Irc User " + from + " successfully logged in as " + 
                from);
        
        ((de.skuzzle.polly.core.internal.users.UserImpl) user).setLoginTime(Time.currentTimeMillis());
        UserEvent e = new UserEvent(this, user);
        this.fireUserSignedOn(e);
        return user;
    }
    
    
    
    private void checkAlreadySignedOn(User user) throws AlreadySignedOnException {        
        if (this.onlineCache.containsKey(user.getCurrentNickName())) {
            throw new AlreadySignedOnException(user.getName());
        }
    }

    
    
    @Override
    public void logoff(User user) {
        logger.info("User " + user + " logged off.");
        UserEvent e = new UserEvent(this, user);
        this.onlineCache.remove(user.getCurrentNickName());
        
        this.fireUserSignedOff(e);
    }
    
    
    
    @Override
    public void logoff(IrcUser user) {
        this.logoff(user, false);
    }
    
    
    
    public synchronized void logoff(IrcUser user, boolean auto) {
        logger.info("User " + user + " logged off.");
        UserEvent e = new UserEvent(this, this.getUser(user), auto);
        this.onlineCache.remove(user.getNickName());

        this.fireUserSignedOff(e);
    }
    
    
    
    public synchronized void logoffAll() {
        //  HACK: copy users to not get a concurrent modification exception
        Collection<User> online = new ArrayList<User>(this.onlineCache.values());
        
        for (User user : online) {
            IrcUser tmp = new IrcUser(user.getCurrentNickName(), "", "");
            this.logoff(tmp, true);
        }
    }
    


    @Override
    public boolean isSignedOn(IrcUser user) {
        return this.onlineCache.containsKey(user.getNickName());
    }
    
    
    
    @Override
    public boolean isSignedOn(User user) {
        return this.onlineCache.containsKey(user.getCurrentNickName());
    }
    
    
    
    @Override
    public synchronized List<User> getRegisteredUsers() {
        if (this.registeredStale || this.registeredUsers == null) {
            final List<de.skuzzle.polly.core.internal.users.UserImpl> all = 
                this.persistence.atomicRetrieveList(
                    de.skuzzle.polly.core.internal.users.UserImpl.class, 
                    de.skuzzle.polly.core.internal.users.UserImpl.ALL_USERS);
            
            for (final de.skuzzle.polly.core.internal.users.UserImpl u : all) {
                u.setUserManager(this);
            }
            this.registeredUsers = new ArrayList<User>(all);
            this.registeredStale = false;
        }
        return this.registeredUsers;
    }
    
    
    
    @Override
    public Collection<User> getOnlineUsers() {
        return Collections.unmodifiableCollection(this.onlineCache.values());
    }
    
    
    
    public synchronized void traceNickChange(IrcUser oldUser, IrcUser newUser) {
        logger.debug("Tracing nickchange from '" + oldUser + "' to '" + newUser + "'");
        User tmp = this.onlineCache.get(oldUser.getNickName());
        tmp.setCurrentNickName(newUser.getNickName());
        this.onlineCache.remove(oldUser.getNickName());
        this.onlineCache.put(newUser.getNickName(), tmp);
    }
    
    
    
    @Override
    public synchronized Map<String, List<Attribute>> getAllAttributes() {
        if (this.attributesStale || this.allAttributes == null) {
            this.allAttributes = this.persistence.atomicRetrieveList(AttributeImpl.class, 
                AttributeImpl.ALL_ATTRIBUTES);
        }
        final Map<String, List<Attribute>> result = 
            new HashMap<>(this.allAttributes.size());
        for (final Attribute attr : this.allAttributes) {
            List<Attribute> lst = result.get(attr.getCategory());
            if (lst == null) {
                lst = new ArrayList<>();
                result.put(attr.getCategory(), lst);
            }
            lst.add(attr);
        }
        
        return result;
    }
    
    
    
    public void resetAllAttributes() {
        try {
            this.persistence.writeLock();
            final List<de.skuzzle.polly.core.internal.users.Attribute> attributes = 
                this.persistence.atomicRetrieveList(
                    de.skuzzle.polly.core.internal.users.Attribute.class, 
                    de.skuzzle.polly.core.internal.users.Attribute.ALL_ATTRIBUTES);
            
            for (final de.skuzzle.polly.core.internal.users.Attribute attr : attributes) {
                this.removeAttribute(attr.getName());
            }
        } catch (DatabaseException e) {
            // TODO: todo
            e.printStackTrace();
        } finally {
            this.persistence.writeUnlock();
        }
    }
    
    
    
    @Override
    public void addAttribute(final String name, final Types defaultValue, 
            final String description, String category, AttributeConstraint constraint) 
                throws DatabaseException {
        
        try {
            this.persistence.writeLock();
            this.constraints.put(name.toLowerCase(), constraint);

            final AttributeImpl check = this.persistence.findSingle(AttributeImpl.class, 
                    AttributeImpl.ATTRIBUTE_BY_NAME, name);
            
            if (check != null) {
                logger.trace("Tried to add an attribute that already existed: " + name + 
                        ". Existing attribute: " + check);
                return;
            }
            
            final String sDefaultValue = defaultValue.valueString(
                this.getPersistenceFormatter());
            
            final AttributeImpl att = new AttributeImpl(name, sDefaultValue, 
                description, category);
            
            final List<User> all = this.getRegisteredUsers();
            this.persistence.startTransaction();
            this.persistence.persist(att);
            
            logger.trace("Adding new attribute to each user.");
            for (User user : all) {
                final UserImpl u = (UserImpl) user;
                u.getAttributes().put(name, sDefaultValue);
            }
            this.persistence.commitTransaction();
            this.attributesStale = true;
            logger.info("Attribute " + att + " added.");
        } finally {
            this.persistence.writeUnlock();
        }
    }
    
    
    
    @Override
    public String setAttributeFor(User executor, final User user, final String attribute, 
            String value) throws DatabaseException, ConstraintException {
        logger.trace("Trying to set attribute '" + attribute + "' to value '" + 
            value + "'");
        
        // check if attribute exists:
        user.getAttribute(attribute);
        
        if (value.equalsIgnoreCase("%default%")) {
            Attribute attr = this.persistence.findSingle(Attribute.class, 
                AttributeImpl.ATTRIBUTE_BY_NAME, attribute);
            
            value = attr.getDefaultValue();
        }
        
        final Types valueCopy = this.parseValue(executor, value);
        final AttributeConstraint constraint = this.constraints.get(
            attribute.toLowerCase());
        
        if (!constraint.accept(valueCopy)) {
            throw new ConstraintException("'" + value + 
                "' ist kein gültiger Wert für das Attribut '" + attribute + "'");
        }
        
        this.persistence.atomicWriteOperation(new WriteAction() {
            
            @Override
            public void performUpdate(PersistenceManager persistence) {
                ((UserImpl) user).setAttribute(attribute, valueCopy);
            }
        });
        return valueCopy.valueString(this.getPersistenceFormatter());
    }

    
    
    public FormatManager getPersistenceFormatter() {
        return this.formatter;
    }
    
    
    
    Types parseValue(User executor, String value) {
        if (executor == null) {
            // use admin as namespace if no executor is specified.
            executor = this.admin;
        }
        
        final ProblemReporter reporter = new SimpleProblemReporter();
        final InputScanner is = new InputScanner(value);
        final InputParser ip = new InputParser(is, reporter);
        is.setSkipWhiteSpaces(true);
        
        try {
            final Expression exp = ip.parseSingleExpression();
            exp.visit(new ParentSetter());
            
            final String nsName = executor.getCurrentNickName() == null 
                ? executor.getName() 
                : executor.getCurrentNickName(); 
            final Namespace ns = Namespace.forName(nsName);
            final ExecutionVisitor exec = new ExecutionVisitor(ns, ns, reporter);
            // resolve types
            TypeResolver.resolveAST(exp, ns, reporter);
            
            exp.visit(exec);
            final Literal result = exec.getSingleResult();
            return TypeMapper.literalToTypes(result);
        } catch (ASTTraversalException e) {
            // ignore the exception, just use plain value which was submitted
            return new Types.StringType(value);
        }
    }


    
    @Override
    public void addAttribute(String name, Types defaultValue, String description, 
            String category) throws DatabaseException {
        this.addAttribute(name, defaultValue, description, category, NO_CONSTRAINT);
    }



    @Override
    public void removeAttribute(String name) throws DatabaseException {
        try {
            this.persistence.writeLock();
            List<User> all = this.persistence.findList(User.class, "ALL_USERS");
            Attribute att = this.persistence.findSingle(
                    Attribute.class, AttributeImpl.ATTRIBUTE_BY_NAME, name);
            
            if (att == null) {
                throw new UnknownAttributeException(name);
            }
            
            this.persistence.startTransaction();
            logger.trace("Removing attribute from all users.");
            for (User user : all) {
                final UserImpl u = (UserImpl) user;
                u.getAttributes().remove(name);
            }
            this.persistence.remove(att);
            this.persistence.commitTransaction();
            this.constraints.remove(name);
            logger.info("Attribute " + att + " removed.");
        } finally {
            this.persistence.writeUnlock();
        }
    }



    @Override
    protected void actualDispose() throws DisposingException {
        this.persistence = null;
        this.onlineCache.clear();
        this.onlineCache = null;
    }



    @Override
    public User createUser(String name, String password) {
        final UserImpl result = new UserImpl(name, password);
        result.setUserManager(this);
        final List<AttributeImpl> all = this.persistence.atomicRetrieveList(
            AttributeImpl.class, AttributeImpl.ALL_ATTRIBUTES);
        
        for (Attribute att : all) {
            result.getAttributes().put(att.getName(), att.getDefaultValue());
        }
        return result;
    }
    
    
    
    @Override
    public void addUserListener(UserListener listener) {
        this.eventProvider.addListener(UserListener.class, listener);
    }
    
    
    
    @Override
    public void removeUserListener(UserListener listener) {
        this.eventProvider.removeListener(UserListener.class, listener);
    }
    
    
    
    protected void fireUserSignedOn(final UserEvent e) {
        final Listeners<UserListener> listeners = 
            this.eventProvider.getListeners(UserListener.class);
        
        Dispatchable<UserListener, UserEvent> d = 
            new Dispatchable<UserListener, UserEvent>(listeners, e) {
                @Override
                public void dispatch(UserListener listener, UserEvent event) {
                    listener.userSignedOn(event);
                }
        };
        this.eventProvider.dispatchEvent(d);
    }
    
    
    
    protected void fireUserSignedOff(final UserEvent e) {
        final Listeners<UserListener> listeners = 
            this.eventProvider.getListeners(UserListener.class);
        
        Dispatchable<UserListener, UserEvent> d = 
            new Dispatchable<UserListener, UserEvent>(listeners, e) {
                @Override
                public void dispatch(UserListener listener, UserEvent event) {
                    listener.userSignedOff(event);
                }
        };
        this.eventProvider.dispatchEvent(d);
    }
}