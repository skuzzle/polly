package polly.rx.captcha;

import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import polly.rx.captcha.ImgUtil.BoundingBox;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class RxCaptchaKiller {
    
    private final static String CAPTCHA_URL = "http://www.revorix.info/gfx/code/code.png"; //$NON-NLS-1$
    private final ImageDatabase db;
    
    
    public RxCaptchaKiller(ImageDatabase db) {
        this.db = db;
    }
    
    
    
    public String decodeCurrentCaptcha() {
        final IplImage captcha = this.readCaptcha();
        final List<BoundingBox> boxes = new ArrayList<>();
        ImgUtil.extractFeatures(captcha, boxes);
        
        final StringBuilder b = new StringBuilder();
        for (final BoundingBox bb : boxes) {
            final IplImage extracted = ImgUtil.imageFromBoundingBox(bb);

            final String c = this.db.tryClassify(extracted, bb);
            
            // return empty string if unknown char occurred.
            if (c.equals("?")) { //$NON-NLS-1$
                this.db.needsClassification(captcha);
                return ""; //$NON-NLS-1$
            }
            b.append(c);
        }
        return b.toString();
    }
    
    
    
    private IplImage readCaptcha() {
        try {
            // HACK: download captcha to file, because conversion to IplImage does
            // not work properly
            final URL url = new URL(CAPTCHA_URL);
            final byte[] buffer = new byte[1024];
            final File tempFile = File.createTempFile("captcha_",  //$NON-NLS-1$
                    "" + System.nanoTime());  //$NON-NLS-1$
            
            final HttpURLConnection c = Anonymizer.openConnection(url);
            try (final InputStream in = c.getInputStream(); 
                    OutputStream out = new FileOutputStream(tempFile)) {
                int length = 0;
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                out.flush();
            }
            
            final IplImage serverImg = cvLoadImage(tempFile.getPath(), 
                    CV_LOAD_IMAGE_GRAYSCALE);
            tempFile.deleteOnExit();
            tempFile.delete();
            return serverImg;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
