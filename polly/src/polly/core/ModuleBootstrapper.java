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
                try {
                    logger.debug("Searching class for module '" + module + "'");
                    Class<?> cls = Class.forName(module);
                    
                    logger.trace("Getting constructor.");
                    Constructor<?> ctor = cls.getConstructor(ModuleLoader.class);
                    
                    logger.trace("Invoking constructor.");
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
