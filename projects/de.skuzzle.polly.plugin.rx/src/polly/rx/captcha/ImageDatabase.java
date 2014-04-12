package polly.rx.captcha;

import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polly.rx.captcha.ImgUtil.BoundingBox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_highgui;


public class ImageDatabase {
    
    public static class DatabaseItem {
        public int centerX;
        public int centerY;
        public int integral;
        public int width;
        public int height;
        public String imgName;
        public String c;
        public transient WeakReference<IplImage> image;
        
        public DatabaseItem(int centerX, int centerY, int integral, int width,
                int height, String imgName, String c) {
            super();
            this.centerX = centerX;
            this.centerY = centerY;
            this.integral = integral;
            this.width = width;
            this.height = height;
            this.imgName = imgName;
            this.c = c;
        }
    }
    
    
    private final static FileFilter PNG_FILES = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.toString().toLowerCase().endsWith(".png"); //$NON-NLS-1$
        }
    };
    private final static int MAX_INTEGRAL_DIFF = 10; // pixels
    private final static double MATCH_THRESHOLD = .9; // percent
    
    
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, DatabaseItem> database = new HashMap<>();
    private final String imgDir;
    private final String databaseFile;
    
    
    
    public ImageDatabase(String imgDir, String databaseFile) {
        this.imgDir = imgDir;
        this.databaseFile = databaseFile;

        this.restoreDatabase();
    }
    
    
    
    public void needsClassification(IplImage image) {
        final File classifyFolder = new File(this.imgDir, "needsClassification"); //$NON-NLS-1$
        if (!classifyFolder.exists()) {
            classifyFolder.mkdirs();
        }
        final File newFile = this.findFileName(classifyFolder, "classify_"); //$NON-NLS-1$
        opencv_highgui.cvSaveImage(newFile.getPath(), image);
    }
    
    
    private File findFileName(File folder, String prefix) {
        int i = 0;
        File newFile = new File(folder, prefix + i + ".png"); //$NON-NLS-1$
        while (newFile.exists()) {
            ++i;
            newFile = new File(folder, prefix + i + ".png"); //$NON-NLS-1$
        }
        return newFile;
    }
    
    
    public void learnFrom(String directory) {
        final File dir = new File(directory);
        if (!dir.isDirectory()) {
            return;
        }
        for (final File chr : new File(this.imgDir).listFiles()) {
            System.out.println("Deleting existing cached images"); //$NON-NLS-1$
            chr.delete();
        }
        
        System.out.println("Learning characters from " + directory); //$NON-NLS-1$
        this.database.clear();
        for (final File file : dir.listFiles(PNG_FILES)) {
            final int dotIdx = file.getName().lastIndexOf("."); //$NON-NLS-1$
            final String rawName = file.getName().substring(0, dotIdx);
            
            if (rawName.length() != 4) {
                System.out.println("Skipping invalid code: " + rawName); //$NON-NLS-1$
                continue;
            }
            
            System.out.println("Processing code: " + rawName); //$NON-NLS-1$
            final IplImage image = cvLoadImage(file.getPath(), CV_LOAD_IMAGE_GRAYSCALE);
            final List<BoundingBox> boxes = new ArrayList<>();
            ImgUtil.extractFeatures(image, boxes);
            
            if (boxes.size() != 4) {
                System.out.println("   Image contains " + boxes.size() + " regions, skipping"); //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }
            
            for (int i = 0; i < 4; ++i) {
                final BoundingBox box = boxes.get(i);
                
                final String character = "" + rawName.charAt(i); //$NON-NLS-1$
                final IplImage classifiedImg = ImgUtil.imageFromBoundingBox(box);
                
                final String tc = this.tryClassify(classifiedImg, box);
                if (tc.equals("?")) { //$NON-NLS-1$
                    System.out.println("    Processing new character: " + character); //$NON-NLS-1$
                } else {
                    System.out.println("    Character " + character + " already exists. Skipping"); //$NON-NLS-1$ //$NON-NLS-2$
                    continue;
                }
                
                final boolean isLower = character.equals(character.toLowerCase());
                final String postfix = isLower ? "_l" : "_u"; //$NON-NLS-1$ //$NON-NLS-2$
                
                final int centerX = (int) box.getCenterX();
                final int centerY = (int) box.getCenterY();
                final int integral = (int) box.getIntegral();
                final int width = box.getWidth();
                final int height = box.getHeight();
                final DatabaseItem dbi = new DatabaseItem(centerX, centerY, integral, 
                        width, height, character + postfix + ".png", character); //$NON-NLS-1$
                this.database.put(character, dbi);
                opencv_highgui.cvSaveImage(this.imgDir + "/" + dbi.imgName, classifiedImg); //$NON-NLS-1$
            }
        }
        this.storeDatabase();
    }
    
    
    
    public String tryClassify(IplImage character, BoundingBox box) {
        double integral = box.getIntegral();
        
        double bestMatch = 0;
        DatabaseItem bestMatchItem = null;
        for (final DatabaseItem dbi : this.database.values()) {
            if (Math.abs(integral - dbi.integral) < MAX_INTEGRAL_DIFF) {
                final IplImage dbImage = this.getCachedImage(dbi);
                
                final double match = this.matchImages(character, box, dbImage, dbi);
                if (match > bestMatch) {
                    bestMatch = match;
                    bestMatchItem = dbi;
                }
            }
        }
        
        System.out.println("Best match:" + bestMatch); //$NON-NLS-1$
        if (bestMatchItem != null && bestMatch >= MATCH_THRESHOLD) {
            return bestMatchItem.c;
        }
        return "?"; //$NON-NLS-1$
    }
    
    
    
    private IplImage getCachedImage(DatabaseItem dbi) {
        IplImage cached = dbi.image != null ? dbi.image.get() : null;
        if (dbi.image == null || cached == null) {
            final IplImage image = cvLoadImage(this.imgDir + "/" + dbi.imgName,  //$NON-NLS-1$
                    CV_LOAD_IMAGE_GRAYSCALE);
            dbi.image = new WeakReference<IplImage>(image);
            return image;
        }
        assert cached != null;
        return cached;
    }
    
    
    
    private double matchImages(IplImage toClassify, BoundingBox box, IplImage dbImage, 
            DatabaseItem dbi) {
        
        final int wsC = toClassify.widthStep();
        final int cC = toClassify.nChannels();
        final int wsDb = dbImage.widthStep();
        final int cDb = dbImage.nChannels();
        
        final int centerX = (int) box.getCenterX();
        final int centerY = (int) box.getCenterY();
        final ByteBuffer cBuffer = toClassify.getByteBuffer();
        final ByteBuffer dbBuffer = dbImage.getByteBuffer();
        
        double total = 0;
        double positive = 0;
        for (int y = 0; y < toClassify.height(); ++ y) {
            for (int x = 0; x < toClassify.width(); ++x) {
                // difference to center in classify img
                int dxC = x - centerX;
                int dyC = y - centerY;

                int xInDb = (int) (dbi.centerX) + dxC;
                int yInDb = (int) (dbi.centerY) + dyC;
                
                if (this.inBounds(dbImage, xInDb, yInDb)) {
                    int valC = cBuffer.get(y * wsC + x * cC);
                    int valDb = dbBuffer.get(yInDb * wsDb + xInDb * cDb);
                    if (valC == valDb) {
                        positive += 1.0;
                    }
                }
                
                total += 1.0;
            }
        }
        return positive / total;
    }
    
    
    
    private boolean inBounds(IplImage img, int x, int y) {
        return x >= 0 && x < img.width() && y >= 0 && y < img.height();
    }
    
    
    
    private void restoreDatabase() {
        final File dbFile = new File(this.databaseFile);
        if (!dbFile.exists()) {
            return;
        }
        
        try (final BufferedReader r = new BufferedReader(new FileReader(dbFile))) {
            
            final Type cType = new TypeToken<Collection<DatabaseItem>>() {}.getType();
            final Collection<DatabaseItem> db = this.gson.fromJson(r, cType);
            this.database.clear();
            for (final DatabaseItem dbi : db) {
                this.database.put(dbi.c, dbi);
            }
                    
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    
    private void storeDatabase() {
        final String s = this.gson.toJson(this.database.values());
        System.out.println("Storing database file to " + this.databaseFile); //$NON-NLS-1$
        try (BufferedWriter w = new BufferedWriter(new FileWriter(this.databaseFile))) {
            w.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
