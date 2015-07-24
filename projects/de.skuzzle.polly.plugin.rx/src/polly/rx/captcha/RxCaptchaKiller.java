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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import polly.rx.captcha.ImgUtil.BoundingBox;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class RxCaptchaKiller {

    private final static String fileExtension = ".png"; //$NON-NLS-1$
    private final static String CAPTCHA_URL = "http://www.revorix.info/gfx/code/code.png"; //$NON-NLS-1$
    private final ImageDatabase db;
    private final File captchaHistoryDir;


    public final static class CaptchaResult {
        final File tempFile;
        final IplImage capthaImg;
        String captcha;

        CaptchaResult(File tempFile, IplImage capthaImg) {
            this.tempFile = tempFile;
            this.capthaImg = capthaImg;
        }
    }



    public RxCaptchaKiller(ImageDatabase db, File captchaHistory) {
        this.db = db;
        this.captchaHistoryDir = captchaHistory;
    }



    public String decodeCurrentCaptcha() {
        final CaptchaResult captcha = readCaptcha();
        final List<BoundingBox> boxes = new ArrayList<>();
        ImgUtil.extractFeatures(captcha.capthaImg, boxes);

        final StringBuilder b = new StringBuilder();
        boolean needClassification = false;
        for (final BoundingBox bb : boxes) {
            final IplImage extracted = ImgUtil.imageFromBoundingBox(bb);

            final String c = this.db.tryClassify(extracted, bb);
            needClassification |= c.equals("?"); //$NON-NLS-1$
            b.append(c);
        }
        if (needClassification) {
            captcha.captcha = b.toString();
            this.db.needsClassification(captcha);
        } else {
            final String fileName = b.toString()  + fileExtension;
            final Path source = captcha.tempFile.toPath();
            final Path target = this.captchaHistoryDir.toPath().resolve(fileName);
            if (!Files.exists(target)) {
                tryMove(source, target);
            }
        }
        return b.toString();
    }

    private void tryMove(Path source, Path target) {
        try {
            Files.move(source, target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private CaptchaResult readCaptcha() {
        try {
            // HACK: download captcha to file, because conversion to IplImage does
            // not work properly
            final URL url = new URL(CAPTCHA_URL);
            final byte[] buffer = new byte[1024];
            final File tempFile = File.createTempFile("captcha_",  //$NON-NLS-1$
                    "" + System.nanoTime() + fileExtension);  //$NON-NLS-1$
            tempFile.deleteOnExit();

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
            return new CaptchaResult(tempFile, serverImg);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
