package polly.rx.captcha;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import polly.rx.captcha.ImgUtil.BoundingBox;


public class ManualClassifier {

    
    public static void main(String[] args) throws IOException {
        System.out.println(
                "Usage: <classifyFolder> <target char folder> <target library file>"); //$NON-NLS-1$
        System.out.println(Arrays.toString(args));
        if (args.length != 3) {
            System.out.println("Illegal arguments"); //$NON-NLS-1$
            return;
        }
        final File classifyFolder = new File(args[0]);
        final File charFolder = new File(args[1]);
        final File dbFile = new File(args[2]);
        
        final ManualClassifier mc = new ManualClassifier(classifyFolder, charFolder, dbFile);
        mc.startClassify();
    }
    
    
    
    private final File classifyFolder;
    private final ImageDatabase db;
    
    public ManualClassifier(File classifyFolder, File charFolder, File dbFile) {
        this.classifyFolder = classifyFolder;
        this.db = new ImageDatabase(charFolder.getPath(), dbFile.getPath());
    }
    
    
    
    public void startClassify() throws IOException {
        final BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        for (final File file : this.classifyFolder.listFiles(ImageDatabase.PNG_FILES)) {
            final Mat img = Highgui.imread(file.getPath(), 
                    Highgui.CV_LOAD_IMAGE_GRAYSCALE);
            
            if (img == null) {
                continue;
            }
            
            final JFrame fullFrame = ImgUtil.showImage(img, "Full"); //$NON-NLS-1$
            fullFrame.setLocation(0, 0);
            
            final List<BoundingBox> boxes = new ArrayList<>();
            ImgUtil.extractFeatures(img, boxes);
            
            for (final BoundingBox box : boxes) {
                final Mat extracted = ImgUtil.imageFromBoundingBox(box);
                final JFrame extrFrame = ImgUtil.showImage(extracted, "Extracted"); //$NON-NLS-1$
                System.out.println("Classify as:"); //$NON-NLS-1$
                final String c = r.readLine();
                if (!c.equals("skip")) { //$NON-NLS-1$
                    this.db.learnSingle(extracted, box, c);
                }
                
                extrFrame.dispose();
            }
            
            fullFrame.dispose();
            file.delete();
        }
    }
}
