package de.skuzzle.polly.core.internal.persistence;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import de.skuzzle.polly.core.internal.plugins.PluginManagerImpl;


public class XmlCreator {
    
    private final static String TEMPLATE_NAME = "persistence.xml.tmpl"; //$NON-NLS-1$
    private final static String TEMPLATE_PATH = 
            XmlCreator.class.getPackage().getName().replace(".", "/") + "/" + TEMPLATE_NAME; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private EntityList entities;
    private DatabaseProperties properties;
    private String persistenceUnit;
    private PluginManagerImpl pluginManager;
    private String pluginFolder;
    
    
    public XmlCreator(EntityList entities, DatabaseProperties properties,
            String persistenceUnit, PluginManagerImpl pluginManager, 
            String pluginFolder) {
        this.entities = entities;
        this.properties = properties;
        this.persistenceUnit = persistenceUnit;
        this.pluginManager = pluginManager;
        this.pluginFolder = pluginFolder;
    }
    
    
    
    public void writePersistenceXml(String path) throws IOException {
        final File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        final File file = new File(folder, "persistence.xml"); //$NON-NLS-1$
        if (file.exists()) {
            file.delete();
        }
        
        final VelocityEngine velo = new VelocityEngine();
        velo.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); //$NON-NLS-1$
        velo.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName()); //$NON-NLS-1$
        velo.init();
        final Template tmpl = velo.getTemplate(TEMPLATE_PATH);
        final VelocityContext c = new VelocityContext();
        c.put("unitName", this.persistenceUnit); //$NON-NLS-1$
        c.put("pluginFolder", this.pluginFolder); //$NON-NLS-1$
        c.put("plugins", this.pluginManager.loadedPlugins()); //$NON-NLS-1$
        c.put("entities", this.entities); //$NON-NLS-1$
        c.put("unitPassword", this.properties.getPassword()); //$NON-NLS-1$
        c.put("unitUser", this.properties.getUser()); //$NON-NLS-1$
        c.put("unitDriver", this.properties.getDriver()); //$NON-NLS-1$
        c.put("unitUrl", this.properties.getUrl()); //$NON-NLS-1$
        
        try (PrintWriter pw = new PrintWriter(file)) {
            tmpl.merge(c, pw);
        }
    }
}