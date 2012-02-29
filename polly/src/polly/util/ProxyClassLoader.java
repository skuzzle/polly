package polly.util;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;


public class ProxyClassLoader extends ClassLoader {

    private List<PluginClassLoader> children;
    
    
    
    public ProxyClassLoader() {
        this(ClassLoader.getSystemClassLoader());
    }



    public ProxyClassLoader(ClassLoader parent) {
        super(parent);
        this.children = new LinkedList<PluginClassLoader>();
    }
    
    
    
    public void addLoader(PluginClassLoader child) {
        synchronized (this.children) {
            this.children.add(child);
        }
    }
    
    
    
    public void removeLoader(PluginClassLoader child) {
        synchronized (this.children) {
            this.children.remove(child);
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