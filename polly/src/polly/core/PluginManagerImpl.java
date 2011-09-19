package polly.core;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PluginManager;
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
    private Map<String, PluginConfiguration> pluginCache;
    
    
    
    public PluginManagerImpl() {
        this.pluginCache = new HashMap<String, PluginConfiguration>();
    }
    
    
    
    public synchronized void load(File propertyFile, MyPolly myPolly) 
            throws PluginException {
        
        PluginConfiguration pluginCfg = null;
        try {
            pluginCfg = new PluginConfiguration(propertyFile.getAbsolutePath());
            logger.debug("Loading Plugin:\n" + pluginCfg.toString());
        } catch (Exception e) {
            throw new PluginException("Error reading plugin property file.", e);
        }
        
        String mainClass = pluginCfg.getProperty(PluginConfiguration.ENTRY_POINT);
        String fileName = pluginCfg.getProperty(PluginConfiguration.JAR_FILE);
        File jarFile = new File(propertyFile.getParent(), fileName);

        PollyPlugin pluginInstance = null;
        try {
            Class<?> clazz = this.loadClass(mainClass, jarFile);
            Constructor<?> cons = clazz.getConstructor(MyPolly.class); 
            pluginInstance = (PollyPlugin) cons.newInstance(myPolly);
            pluginCfg.setPluginInstance(pluginInstance);
            this.addPlugin(pluginCfg);
            logger.info("Plugin from " + propertyFile.getName() + "' loaded.");
        } catch (Exception e) {
            throw new PluginException(e);
        }
        
    }
    


    @Override
    public synchronized void unload(String pluginName) throws PluginException {
        PluginConfiguration pluginCfg = this.pluginCache.get(pluginName);
        if (pluginCfg == null) {
            throw new PluginException("Plugin not loaded");
        }
        
        try {
            pluginCfg.getPluginInstance().dispose();
            logger.info("Plugin '" + pluginName + "' successfully unloaded.");
        } catch (Exception e) {
            
        } finally {
            this.pluginCache.put(pluginName, null);
        }
    }
    
    

    @Override
    public boolean isLoaded(String pluginName) {
        return this.pluginCache.containsKey(pluginName);
    }

    
    
    @Override
    protected void actualDispose() throws DisposingException {
        logger.info("Unloading all plugins.");
        for (PluginConfiguration pluginCfg : this.pluginCache.values()) {
            String pluginName = pluginCfg.getProperty(PluginConfiguration.PLUGIN_NAME);
            try {
                this.unload(pluginName);
            } catch (Exception e) {
                logger.error("Error while unloading plugin '" + pluginName + "'", e);
            }
        }
    }
    
    
    
    public void uninstall(String pluginName) throws PluginException {
        PluginConfiguration pluginCfg = this.pluginCache.get(pluginName);
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
    
    
    
    private void addPlugin(PluginConfiguration pluginCfg) {
        this.pluginCache.put(pluginCfg.getProperty(PluginConfiguration.PLUGIN_NAME), 
                pluginCfg);
    }
    
    
    
    private Class<?> loadClass(String clazz, File jarFile) throws ClassNotFoundException {
        ClassLoader contextCl = Thread.currentThread().getContextClassLoader();
        try {
            URL url = jarFile.toURI().toURL();
            URLClassLoader urlCl = new URLClassLoader(new URL[]{url}, contextCl);
            Class<?> result = urlCl.loadClass(clazz);

            Thread.currentThread().setContextClassLoader(urlCl);
            return result;
        } catch (MalformedURLException e) {
            throw new ClassNotFoundException(clazz, e);
        }
    }
    
    
    
    public void notifyPlugins() {
        for (Map.Entry<String, PluginConfiguration> entry : this.pluginCache.entrySet()) {
            try {
                entry.getValue().getPluginInstance().onLoad();
            } catch (Exception e) {
                logger.error("Error while notifying plugin '" + 
                        entry.getKey() + "'.", e);
            }
        }
    }
    
    
    
    public Collection<PluginConfiguration> loadedPlugins() {
        return Collections.unmodifiableCollection(this.pluginCache.values());
    }
    
    
    
    public List<PluginConfiguration> enumerate(String folder, final String...excludes) {
        List<PluginConfiguration> result = new LinkedList<PluginConfiguration>();
        
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
                result.add(new PluginConfiguration(file.getAbsolutePath()));
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
