package polly.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;


public class ConfigurationProviderImpl implements ConfigurationProvider {

    private File configDir;
    private File pluginConfigDir;
    
    private Map<File, Configuration> cfgCache;
    private Configuration rootCfg;
    
    
    
    public ConfigurationProviderImpl(File configDir) {
        this.configDir = configDir;
        this.pluginConfigDir = new File(configDir, "pluginconfigs");
        this.cfgCache = new WeakHashMap<File, Configuration>();
    }
    
    
    
    public Configuration open(String cfgName, boolean isRoot)
            throws FileNotFoundException, IOException {
        File cfgFile = this.searchFor(cfgName);
        Configuration cached = this.cfgCache.get(cfgFile.getCanonicalFile());
        if (cached != null) {
            return cached;
        } else {
            cached = new ConfigurationImpl(cfgFile);
            this.cfgCache.put(cfgFile.getCanonicalFile(), cached);
        }
        
        if (isRoot) {
            this.rootCfg = cached;
        }
        return cached;
    }
    
    
    
    @Override
    public Configuration open(String cfgName) throws FileNotFoundException, IOException {
        return this.open(cfgName, false);
    }
    
    
    
    @Override
    public Configuration emptyConfiguration() {
        return new ConfigurationImpl();
    }
    
    
    
    @Override
    public Configuration getRootConfiguration() {
        return this.rootCfg;
    }

    
    
    private File searchFor(String cfgName) throws FileNotFoundException {
        File cfgFile = new File(this.configDir, cfgName);
        if (!cfgFile.exists()) {
            cfgFile = new File(this.pluginConfigDir, cfgName);
            if (!cfgFile.exists()) {
                throw new FileNotFoundException(cfgName);
            }
        }
        return cfgFile;
    }
}
