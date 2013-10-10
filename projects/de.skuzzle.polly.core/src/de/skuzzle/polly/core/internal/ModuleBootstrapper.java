package de.skuzzle.polly.core.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;





public class ModuleBootstrapper {

    private final static Logger logger = Logger.getLogger(
            ModuleBootstrapper.class.getName());
    

    
    public static void prepareModuleLoader(ModuleLoader loader, 
            File modulesCfg) throws IOException, SetupException {
        
        parseConfig(modulesCfg, loader);
    }
    
    
    
    private static void parseConfig(File modules, ModuleLoader loader) 
                throws IOException, SetupException {
        if (!modules.exists()) {
            throw new FileNotFoundException(
                    MSG.bind(MSG.moduleDefinitionNotFound, modules));
        }
        
        
        BufferedReader input = null;
        try {
            logger.info("Reading modules cfg from " + modules); //$NON-NLS-1$
            input = new BufferedReader(
                new InputStreamReader(new FileInputStream(modules)));
            
            String module = null;
            
            while ((module = input.readLine()) != null) {
                module = module.trim();
                try {
                    if (module.startsWith("include ")) { //$NON-NLS-1$
                        String[] parts = module.split(" ", 2); //$NON-NLS-1$
                        File parent = modules.getParentFile();
                        parseConfig(new File(parent, parts[1]), loader);
                        continue;
                    } 
                    if (module.startsWith("#") || module.startsWith("//") || module.equals("")) {  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                        // skip comments
                        continue;
                    }
                    logger.debug("Searching class for module '" + module + "'"); //$NON-NLS-1$ //$NON-NLS-2$
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
