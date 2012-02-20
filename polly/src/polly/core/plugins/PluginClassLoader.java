package polly.core.plugins;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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
    private ZipEntry[] dependencies = new ZipEntry[0];
    private Map<String, byte[]> cache;


    public static PluginClassLoader getInstance(File file) {
        return getInstance(file, ClassLoader.getSystemClassLoader());
    }



    public static PluginClassLoader getInstance(File file, ClassLoader parent) {
        PluginClassLoader loader = new PluginClassLoader(file, parent);
        try {
            loader.readManifestClasspath();
            return loader;
        } catch (IOException e) {
            return null;
        }
    }



    private PluginClassLoader(File file) {
        this(file, ClassLoader.getSystemClassLoader());
    }



    private PluginClassLoader(File file, ClassLoader parent) {
        super(parent);
        this.file = file;
        this.cache = new HashMap<String, byte[]>();
    }



    private void openJar() throws IOException {
        if (this.jar == null) {
            this.jar = new JarFile(this.file);
            this.jarLastModified = this.file.lastModified();
        }
    }

    

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', '/').concat(".class");

        byte[] data = this.getFile(path);
        if (data == null)
            throw new ClassNotFoundException();

        return this.defineClass(name, data, 0, data.length);
    }


    
    @Override
    protected URL findResource(String name) {
        byte[] data = this.getFile(name);

        if (data == null)
            return null;
        try {
            return this.getDataURL(name, data);
        } catch (MalformedURLException e) {
            return null;
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



    private void initializeClasspath(String[] items) throws IOException {
        openJar();
        List<ZipEntry> deps = new ArrayList<ZipEntry>();
        for (int i = 0; i < items.length; i++) {
            String item = items[i].trim();
            ZipEntry entry = this.jar.getEntry(item);
            if (entry == null) {
                logger.error("Class-Path entry " + item + " in jar " + file
                    + " does not exist");
            } else {
                deps.add(entry);
            }
        }
        this.dependencies = new ZipEntry[deps.size()];
        deps.toArray(this.dependencies);
    }



    private void readManifestClasspath() throws IOException {
        this.openJar();
        Manifest mf = this.jar.getManifest();
        if (mf == null) {
            return;
        }
        Attributes attribs = mf.getMainAttributes();
        String classpath = attribs.getValue(Attributes.Name.CLASS_PATH);
        if (classpath == null) {
            return;
        }
        String[] items = classpath.split(" ");
        this.initializeClasspath(items);
    }



    protected byte[] getFile(String path) {
        try {
            this.openJar();
            ZipEntry entry = this.jar.getEntry(path);
            
            if (entry == null) {
                byte[] cached = this.cache.get(path);
                if (cached == null) {
                    for (int i = 0; i < this.dependencies.length; ++i) {
                        ZipEntry dep = this.dependencies[i];
                        JarInputStream in = new JarInputStream(
                                    this.jar.getInputStream(dep));
                        
                        ZipEntry check = in.getNextEntry();
                        while (check != null) {
                            String name = check.getName();
                            if (!name.endsWith("/")) {
                                byte[] data = this.readStream(in);
                                this.cache.put(name, data);
                                if (name.equals(path)) {
                                    cached = data;
                                }
                            }
                            check = in.getNextEntry();
                        }
                    }
                }
                return cached;
            } else {
                InputStream in = this.jar.getInputStream(entry);
                int size = (int) entry.getSize();
                return this.readStream(in, size);
            }
        } catch (IOException e) {
            return null;
        }
    }



    protected byte[] readStream(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];

        int bytesRead = 0;
        while (true) {
            int byteReadThisTurn = in.read(buffer, bytesRead, buffer.length
                - bytesRead);
            if (byteReadThisTurn < 0)
                break;

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



    protected byte[] readStream(InputStream in, int size) throws IOException {
        if (in == null)
            return null;
        if (size == 0)
            return new byte[0];
        int currentTotal = 0;
        int bytesRead;
        byte[] data = new byte[size];
        while (currentTotal < data.length
            && (bytesRead = in.read(data, currentTotal, data.length
                - currentTotal)) >= 0)
            currentTotal += bytesRead;

        in.close();
        return data;
    }
}