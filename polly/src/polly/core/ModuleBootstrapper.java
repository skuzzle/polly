package polly.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;


import polly.configuration.PollyConfiguration;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;


public class ModuleBootstrapper {

    private final static Logger logger = Logger.getLogger(
            ModuleBootstrapper.class.getName());
    

    
    public static void prepareModuleLoader(ModuleLoader loader, 
            PollyConfiguration config) throws IOException, SetupException {
        File modules = new File(config.getModulesCfg());
        
        parseConfig(modules, loader);
    }
    
    
    
    private static void parseConfig(File modules, ModuleLoader loader) 
                throws IOException, SetupException {
        if (!modules.exists()) {
            throw new FileNotFoundException("Module definition not found: " + modules);
        }
        
        
        BufferedReader input = null;
        try {
            logger.info("Reading modules cfg from " + modules);
            input = new BufferedReader(
                new InputStreamReader(new FileInputStream(modules)));
            
            String module = null;
            
            while ((module = input.readLine()) != null) {
                module = module.trim();
                try {
                    if (module.startsWith("include ")) {
                        String[] parts = module.split(" ", 2);
                        File parent = modules.getParentFile();
                        parseConfig(new File(parent, parts[1]), loader);
                        continue;
                    } 
                    if (module.startsWith("#") || module.startsWith("//") || module.equals("")) {
                        // skip comments
                        continue;
                    }
                    logger.debug("Searching class for module '" + module + "'");
                    Class<?> cls = Class.forName(module);
                    Constructor<?> ctor = cls.getConstructor(ModuleLoader.class);
                    ctor.newInstance(loader);
                } catch (Exception e) {
                    throw new SetupException(e);
                }
            }
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

}
