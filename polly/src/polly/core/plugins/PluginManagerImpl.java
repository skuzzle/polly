package polly.core.plugins;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import polly.util.PluginClassLoader;
import polly.util.ProxyClassLoader;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PluginManager;
import de.skuzzle.polly.sdk.PluginState;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.PluginException;


/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 */
public class PluginManagerImpl extends AbstractDisposable implements PluginManager {

    private static Logger logger = Logger.getLogger(PluginManagerImpl.class.getName());
    
    
    
    /** 
     * Stores all loaded plugins. Key: plugin name
     */
    private Map<String, Plugin> pluginCache;
    private ProxyClassLoader pollyCl;
    
    
    
    public PluginManagerImpl(ProxyClassLoader pollyCl) {
        this.pluginCache = new HashMap<String, Plugin>();
        this.pollyCl = pollyCl;
    }
    
    
    
    public synchronized void load(File propertyFile, MyPolly myPolly) 
            throws PluginException {
        
        Plugin pluginCfg = null;
        try {
            pluginCfg = new Plugin(propertyFile.getAbsolutePath());
            logger.debug("Loading Plugin:\n" + pluginCfg.toString());
        } catch (Exception e) {
            throw new PluginException("Error reading plugin property file.", e);
        }
        
        String mainClass = pluginCfg.getProperty(Plugin.ENTRY_POINT);
        String fileName = pluginCfg.getProperty(Plugin.JAR_FILE);
        File jarFile = new File(propertyFile.getParent(), fileName);

        PollyPlugin pluginInstance = null;
        try {
            Class<?> clazz = this.loadClass(mainClass, jarFile);
            Constructor<?> cons = clazz.getConstructor(MyPolly.class); 
            pluginInstance = (PollyPlugin) cons.newInstance(myPolly);
            pluginCfg.setPluginInstance(pluginInstance);
            pluginCfg.setLoader((PluginClassLoader) clazz.getClassLoader());
            pluginInstance.setPluginState(PluginState.LOADED);
            this.addPlugin(pluginCfg);
            logger.info("Plugin from " + propertyFile.getName() + "' loaded.");
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }
    


    @Override
    public synchronized void unload(String pluginName) throws PluginException {

    }
    
    
    
    private void unload(String pluginName, Iterator<Plugin> it) throws PluginException {
        Plugin pluginCfg = this.pluginCache.get(pluginName);
        if (pluginCfg == null) {
            throw new PluginException("Plugin not loaded");
        }
        
        try {
            this.pollyCl.removeLoader(pluginCfg.getLoader());
            pluginCfg.getPluginInstance().setPluginState(PluginState.NOT_LOADED);
            pluginCfg.dispose();
            logger.info("Plugin '" + pluginName + "' successfully unloaded.");
        } catch (Exception e) {
            logger.error("Error while unloading plugin: '" + pluginName + "'", e);
        } finally {
            it.remove();
        }
    }
    
    

    @Override
    public boolean isLoaded(String pluginName) {
        return this.pluginCache.containsKey(pluginName);
    }

    
    
    @Override
    protected void actualDispose() throws DisposingException {
        logger.info("Unloading all plugins.");
        Iterator<Plugin> it = this.pluginCache.values().iterator();
        while (it.hasNext()) {
            Plugin pluginCfg = it.next();
            
            String pluginName = pluginCfg.getProperty(Plugin.PLUGIN_NAME);
            try {
                this.unload(pluginName, it);
            } catch (Exception e) {
                logger.error("Error while unloading plugin '" + pluginName + "'", e);
            }
        }
    }
    
    
    
    public void uninstall(String pluginName) throws PluginException {
        Plugin pluginCfg = this.pluginCache.get(pluginName);
        if (pluginCfg != null) {
            logger.info("Uninstalling plugin '" + pluginName + "'.");
            try {
                pluginCfg.getPluginInstance().uninstall();
            } catch (Exception e) {
                logger.error("Error while uninstalling plugin.", e);
                throw new PluginException(e);
            }
            logger.info("Plugin successfully uninstalled.");
        }
    }
    
    
    
    private void addPlugin(Plugin pluginCfg) {
        this.pluginCache.put(pluginCfg.getProperty(Plugin.PLUGIN_NAME), 
                pluginCfg);
    }
    
    
    
    private Class<?> loadClass(String clazz, File jarFile) throws ClassNotFoundException {
        ClassLoader contextCl = Thread.currentThread().getContextClassLoader();
        PluginClassLoader cl;
        try {
            cl = new PluginClassLoader(jarFile, contextCl);
        } catch (IOException e) {
            throw new ClassNotFoundException(clazz, e);
        }
        Class<?> result = cl.loadClass(clazz);

        this.pollyCl.addLoader(cl);
        return result;
    }
    
    
    
    public void notifyPlugins() {
        for (Map.Entry<String, Plugin> entry : this.pluginCache.entrySet()) {
            try {
                entry.getValue().getPluginInstance().onLoad();
            } catch (Exception e) {
                entry.getValue().getPluginInstance().setPluginState(PluginState.ERROR);
                logger.error("Error while notifying plugin '" + 
                        entry.getKey() + "'.", e);
            }
        }
    }
    
    
    
    public Collection<Plugin> loadedPlugins() {
        return Collections.unmodifiableCollection(this.pluginCache.values());
    }
    
    
    
    public List<Plugin> enumerate(String folder, final String...excludes) {
        List<Plugin> result = new LinkedList<Plugin>();
        
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                for (String exclude : excludes) {
                    if (pathname.getName().equals(exclude)) {
                        return false;
                    }
                }
                return pathname.getName().toLowerCase().endsWith(".properties");
            }
        };
        
        for (File file : (new File(folder).listFiles(filter))) {
            try {
                result.add(new Plugin(file.getAbsolutePath()));
            } catch (Exception e) {
                logger.error("Error reading plugin property file.", e);
            }
        }
        return result;
    }
    
    
    
    public void loadFolder(String folder, MyPolly myPolly, final String...excludes) 
            throws PluginException {
        
        File dir = new File(folder);
        if (!dir.isDirectory()) {
            throw new PluginException(folder + " is not a directory.");
        }
        
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                for (String exclude : excludes) {
                    if (pathname.getName().equals(exclude)) {
                        return false;
                    }
                }
                return pathname.getName().toLowerCase().endsWith(".properties");
            }
        };
        
        int success = 0;
        int fails = 0;
        File[] files = dir.listFiles(filter);
        
        for (File file : files) {
            try {
                this.load(file, myPolly);
                ++success;
            } catch (Exception e) {
                logger.error("Error while loading plugin", e);
                ++fails;
            }
        }
        logger.info(success + " of " + (fails + success) + 
                " plugins successfully loaded.");
        if (fails > 0) {
            throw new PluginException(fails + " plugins failed to load.");
        }
    }
}
