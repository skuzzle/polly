package de.skuzzle.polly.core.internal.httpv2;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.JarResourceLoader;

import de.skuzzle.polly.core.internal.plugins.Plugin;
import de.skuzzle.polly.http.api.handler.TemplateAnswerHandler;


public class PollyTemplateAnswerHandler extends TemplateAnswerHandler {

    private final static Logger logger = Logger
        .getLogger(PollyTemplateAnswerHandler.class.getName());
    
    private final String jarResourceLoaderPath;
    
    
    
    public PollyTemplateAnswerHandler(String pluginFolder, 
            Collection<Plugin> pluginjarFiles) {
        final StringBuilder b = new StringBuilder();
        final Iterator<Plugin> it = pluginjarFiles.iterator();
        while (it.hasNext()) {
            final Plugin plugin = it.next();
            final String path = new File(pluginFolder, 
                    plugin.readString(Plugin.JAR_FILE)).toString().replace("\\", "/");
            b.append("jar:file:" + path);
            if (it.hasNext()) {
                b.append(", ");
            }
        }
        this.jarResourceLoaderPath = b.toString();
        logger.info("Preparing PollyTemplateAnswerHandler. Resource path: " + 
            this.jarResourceLoaderPath);
    }
    
    
    
    @Override
    protected void prepare(org.apache.velocity.app.VelocityEngine ve, 
            String templatePath) {
        //super.prepare(ve, templatePath);
        ve.setProperty("resource.loader", "class, jar");
        ve.setProperty("class.resource.loader.class", 
            ClasspathResourceLoader.class.getName());
        
        ve.setProperty("jar.resource.loader.class", 
            JarResourceLoader.class.getName());
        ve.setProperty("jar.resource.loader.path", this.jarResourceLoaderPath);
        
    };
}
