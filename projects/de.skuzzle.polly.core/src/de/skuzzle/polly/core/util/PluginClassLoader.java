package de.skuzzle.polly.core.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.SecureClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.apache.log4j.Logger;



/**
 * This classloader implementation is adapted from the IRC Bot implementation JBot
 * by Hani Suleiman (hani@formicary.net).
 * 
 * Original sources can be found at 
 * <a href="http://java.net/projects/jbot/>JBot</a>
 * 
 * @author Simon
 * @version 20.02.2012
 */
public class PluginClassLoader extends SecureClassLoader implements Cloneable {

    
    private class BytesURLStreamHandler extends URLStreamHandler {

        byte[] content;

        public BytesURLStreamHandler(byte[] content) {
            this.content = content;
        }



        public URLConnection openConnection(URL url) {
            return new BytesURLConnection(url, content);
        }
    }

    
    
    private class BytesURLConnection extends URLConnection {

        protected byte[] content;


        public BytesURLConnection(URL url, byte[] content) {
            super(url);
            this.content = content;
        }



        public void connect() {}



        public InputStream getInputStream() {
            return new ByteArrayInputStream(this.content);
        }
    }
    
    
    

    private final static Logger logger = Logger
        .getLogger(PluginClassLoader.class.getName());

    private JarFile jar;
    private File file;
    private long jarLastModified;
    private Map<String, byte[]> dependencyCache;
    private Map<String, Class<?>> classCache;


    public PluginClassLoader(File file) throws IOException {
        this(file, ClassLoader.getSystemClassLoader());
    }



    public PluginClassLoader(File file, ClassLoader parent) throws IOException {
        super(parent);
        this.file = file;
        this.dependencyCache = new HashMap<String, byte[]>();
        this.classCache = new HashMap<String, Class<?>>();
        
        this.jar = new JarFile(this.file);
        this.jarLastModified = file.lastModified();
    }



    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (this.jar == null) {
            throw new ClassNotFoundException(name);
        }
        
        String path = name.replace('.', '/').concat(".class");

        synchronized (this) {
            Class<?> cached = this.classCache.get(path);
            if (cached != null) {
                return cached;
            }
            
            byte[] data = this.getFile(this.jar, path);
            if (data == null)
                throw new ClassNotFoundException();
    
            cached = this.defineClass(name, data, 0, data.length);
            this.classCache.put(path, cached);
            return cached;
        }
    }
    

    
    @Override
    protected URL findResource(String name) {
        if (this.jar == null) {
            return null;
        }
        
        synchronized (this) {
            byte[] data = this.getFile(this.jar, name);
            
            if (data == null) {
                return null;
            }
            try {
                return this.getDataURL(name, data);
            } catch (MalformedURLException e) {
                return null;
            }
        }
    }



    protected URL getDataURL(String name, byte[] data)
        throws MalformedURLException {
        return new URL(null, this.file.toURI().toURL().toExternalForm() + '!'
            + name, new BytesURLStreamHandler(data));
    }

    

    @Override
    protected Enumeration<URL> findResources(String name) {
        URL url = this.findResource(name);

        if (url == null)
            return null;

        return Collections.enumeration(Collections.singleton(url));
    }



    public boolean isStale() {
        return new File(this.jar.getName()).lastModified() > this.jarLastModified;
    }



    private String[] readManifestClasspath(JarFile jar) throws IOException {
        return getClasspath(jar.getManifest());
    }
    
    
    
    /*private String[] readMainfestClasspath(String jarName, JarInputStream in) {
        String[] cp = this.cpCache.get(jarName);
        if (cp != null) {
            return cp;
        }
        
        cp = getClasspath(in.getManifest());
        this.cpCache.put(jarName, cp);
        return cp;
    }*/
    
    
    
    private String[] getClasspath(Manifest mf) {
        if (mf == null) {
            return null;
        }
        Attributes attribs = mf.getMainAttributes();
        String classpath = attribs.getValue(Attributes.Name.CLASS_PATH);
        if (classpath == null) {
            return null;
        }
        
        return classpath.split(" ");
    }
    
    
    
    private byte[] readSimpleEntry(JarFile jar, ZipEntry entry) throws IOException {
        InputStream in = jar.getInputStream(entry);
        int size = (int) entry.getSize();
        return readStream(in, size);
    }
    
    
    
    private byte[] getFile(JarFile jar, String className) {
        byte[] file = null;
        
        try {
            // try finding class in current jar
            ZipEntry entry = jar.getEntry(className);
            if (entry != null) {
                file = this.readSimpleEntry(jar, entry);
                return file;
            }
            
            // try finding class in dependency cache
            file = this.dependencyCache.get(className);
            if (file != null) {
                return file;
            }
            
            String[] classpath = this.readManifestClasspath(jar);
            
            // now, try to find the path in our dependencies
            for (int i = 0; classpath != null && i < classpath.length && file == null; ++i) {
                String cpEntry = classpath[i];
                
                entry = jar.getEntry(cpEntry);
                if (entry != null) {
                    // dependency found in our jar
                    if (cpEntry.endsWith(".jar")) {
                        // dependencies in contained jars are not resolved recursively!
                        // now try to find requested class in the dependency.
                        // We read the whole referenced dependency into our cache so 
                        // there is no need to open it again
                        JarInputStream in = new JarInputStream(jar.getInputStream(entry));
                        
                        ZipEntry check = in.getNextEntry();
                        while (check != null) {
                            if (!check.isDirectory()) {
                                // cache the file so we do not need to read this jar
                                // file again
                                byte[] tmp = readStream(in);
                                this.dependencyCache.put(check.getName(), tmp);
                                if (check.getName().equals(className)) {
                                    file = tmp;
                                }
                            }
                            check = in.getNextEntry();
                        }
                    } else if (cpEntry.endsWith(".class")) {
                        file = this.readSimpleEntry(jar, entry);
                    }
                } else {
                    // try to find dependeny in directory
                    File cpEntryFile = new File(cpEntry);
                    if (!cpEntryFile.exists()) {
                        continue;
                    } else if (cpEntry.endsWith(".class")) {
                        // classpath references a classfile, so we can read it directly
                        file = readStream(new FileInputStream(cpEntryFile));
                        continue;
                    } else if (!cpEntry.endsWith("jar")) {
                        // we cannot process dependencies that are no jar files
                        continue;
                    }
                    
                    // file exists, so try finding the requested class in the referenced
                    // jar file recursively
                    file = this.getFile(new JarFile(cpEntryFile), className);
                }
            }
        } catch (IOException ignore) {
            logger.error("", ignore);
            /* returning null */ 
        }
        
        return file;
    }
    
    
    
    private static byte[] readStream(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];

        int bytesRead = 0;
        while (true) {
            int byteReadThisTurn = in.read(buffer, bytesRead, 
                buffer.length - bytesRead);
            
            if (byteReadThisTurn < 0) {
                break;
            }

            bytesRead += byteReadThisTurn;

            if (bytesRead >= buffer.length - 256) {
                byte[] newBuffer = new byte[buffer.length * 2];
                System.arraycopy(buffer, 0, newBuffer, 0, bytesRead);
                buffer = newBuffer;
            }
        }

        if (buffer.length == bytesRead) {
            return buffer;
        } else {
            byte[] response = new byte[bytesRead];
            System.arraycopy(buffer, 0, response, 0, bytesRead);

            return response;
        }
    }



    private static byte[] readStream(InputStream in, int size) throws IOException {
        if (in == null) {
            return null;
        } else if (size == 0) {
            return new byte[0];
        }
        int currentTotal = 0;
        int bytesRead = 0;
        byte[] data = new byte[size];
        while (currentTotal < data.length
            && (bytesRead = in.read(data, currentTotal, data.length
                - currentTotal)) >= 0)
            currentTotal += bytesRead;

        in.close();
        return data;
    }
    
    
    
    public void dispose() {
        this.classCache.clear();
        this.dependencyCache.clear();
        this.classCache = null;
        this.dependencyCache = null;
    }
    
    
    
    @Override
    public String toString() {
        return "PluginClassLoader:" + this.file;
    }
}