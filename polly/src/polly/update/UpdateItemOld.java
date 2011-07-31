package polly.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import polly.update.DownloadManager.DownloadCallback;
import polly.update.DownloadManager.DownloadObject;


public class UpdateItemOld implements DownloadCallback {
    
    public static File createTempDirectory() throws IOException {
        final File temp = File.createTempFile("temp", 
            Long.toString(System.nanoTime()));

        if(!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp);
        }

        if(!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp);
        }

        return (temp);
    }
    
    
    private static Logger logger = Logger.getLogger(UpdateItemOld.class.getName());
    
    private Properties props;
    private File destination; 
    private boolean complete;
    private boolean failed;
    private Boolean verified;
    
    
    
    public UpdateItemOld(Properties props, File destination) {
        this.props = props;
        this.destination = destination;
    }
    
    
    
    public boolean isDone() {
        return this.complete || this.failed;
    }
    
    
    
    public boolean isReady() {
        return this.complete && !this.failed;
    }
    
    
    
    public void delete() {
        this.destination.delete();
    }
    
    
    
    /*public void install(PrintStream p, File pollyDir) throws ZipException, IOException {
        if (!this.isReady() || !this.verify()) {
            return;
        }
        
        File tmp = this.unzip(createTempDirectory());
        p.println("xcopy \"" + tmp.getAbsolutePath() + "\" \"" + 
            pollyDir.getAbsolutePath() + "\" /s /e");
        p.println("deltree \"" + tmp.getAbsolutePath() + "\"");
        p.println();
        
        this.verified = null;
        this.delete();
    }*/
    
    
    
    
    /*public boolean verify() {
        if (this.verified != null) {
            return this.verified;
        }
        
        CheckedInputStream cis = null;
        try {
            cis = new CheckedInputStream(
                new FileInputStream(this.destination), new CRC32());
            
            byte[] buffer = new byte[2048];
            while (cis.read(buffer) != -1);
            long checksum = cis.getChecksum().getValue();
            
            //String tmp = this.props.getProperty(UpdateManager.UPDATE_CRC, "0");
            //long expected = Long.parseLong(tmp, 16);
            //return this.verified = (checksum == expected);
            
        } catch (Exception e) {
            return this.verified = false;
        } finally {
            if (cis != null) {
                try {
                    cis.close();
                } catch (IOException e) {
                }
            }
        }
    }*/

    
    
    public File unzip(File to) throws ZipException, IOException {
        ZipFile zipFile = new ZipFile(this.destination);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            BufferedInputStream in =  new BufferedInputStream(
                zipFile.getInputStream(entry));
            
            File next = new File(to, entry.getName());
            if (!next.isDirectory()) {
                next.getParentFile().mkdirs();
            }
            
            logger.trace("Unzipping " + next);
            if (entry.isDirectory()) {
                next.mkdirs();
                continue;
            }
            BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(next));

            byte[] buffer = new byte[2048];
            int bytes = in.read(buffer);
            while (bytes != -1) {
                out.write(buffer, 0, bytes);
                bytes = in.read(buffer);
            }
            out.flush();
            out.close();
            in.close();
        }
        zipFile.close();
        System.out.println("done");
        return to;
    }
    
    
    
    @Override
    public void downloadFinished(DownloadObject o) {
        logger.info("Download complete: " + o);
        this.complete = true;
        try {
            this.unzip(createTempDirectory());
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
    
    @Override
    public void downloadFailed(DownloadObject o, Exception e) {
        logger.error("Download failed: " + o.toString(), e);
        System.out.println("fail");
        e.printStackTrace();
        this.failed = true;
    }
    
    
    
    @Override
    public String toString() {
        return "";
    }
}