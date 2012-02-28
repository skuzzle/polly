package polly.util;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;


public class PollyClassLoader extends ClassLoader {

    private List<PluginClassLoader> children;
    
    
    
    public PollyClassLoader() {
        this(ClassLoader.getSystemClassLoader());
    }



    public PollyClassLoader(ClassLoader parent) {
        super(parent);
        this.children = new LinkedList<PluginClassLoader>();
    }
    
    
    
    public void addPlugin(PluginClassLoader child) {
        synchronized (this.children) {
            this.children.add(child);
        }
    }
    
    
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        synchronized (this.children) {
            for (PluginClassLoader cl : this.children) {
                try {
                    return cl.findClass(name);
                } catch (ClassNotFoundException ignore){};
            }
        }
        throw new ClassNotFoundException(name);
    }
    
    
    
    @Override
    protected URL findResource(String name) {
        synchronized(this.children) {
            for (PluginClassLoader cl : this.children) {
                URL result = cl.findResource(name);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
    
    
    
    @Override
    public String toString() {
        return this.getClass().getName();
    }
}