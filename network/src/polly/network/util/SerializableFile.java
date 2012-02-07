package polly.network.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class SerializableFile implements Serializable {
    
    public static void main(String[] args) throws FileNotFoundException, IOException, 
            ClassNotFoundException {
        
        SerializableFile test = new SerializableFile(new File("./logs/polly.log"));
        ObjectOutputStream out = new ObjectOutputStream(
            new FileOutputStream(new File("./logs/polly_copy.log")));
        
        out.writeObject(test);
        out.flush();
        out.close();
        
        ObjectInputStream in = new ObjectInputStream(
            new FileInputStream(new File("./logs/polly_copy.log")));
        
        test = (SerializableFile) in.readObject();
        test.store(new File("./logs/polly_copy2.log"));
    }
    
    private static final long serialVersionUID = 1L;
    private static final int BUFFER_SIZE = 65536;
    
    private transient File localFile;
    private transient byte[] received;
    
    private String name;
    
    
    
    public SerializableFile(File localFile) {
        this.localFile = localFile;
        this.name = localFile.getName();
    }
    
    
    public void store(File destination) throws IOException {
        if (this.received == null) {
            throw new IOException("nothing received");
        } else if (destination.isDirectory()) {
            destination = new File(destination, this.name);
        }
        
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(destination);
            out.write(this.received);
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        if (this.localFile.length() > Integer.MAX_VALUE) {
            throw new IOException("unsupported file size");
        }
        // first, write file length
        out.writeInt((int) this.localFile.length());
        
        FileInputStream in = null;
        try {
            in = new FileInputStream(this.localFile);
            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                out.flush();
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
    
    
    
    private void readObject(ObjectInputStream in) throws IOException {
        int length = in.readInt();
        
        this.received = new byte[length];
        int lenrecvd = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        
        while (lenrecvd < length) {
            int len = in.read(buffer);
            System.arraycopy(buffer, 0, this.received, lenrecvd, len);
            lenrecvd += len;
        }
    }
}
