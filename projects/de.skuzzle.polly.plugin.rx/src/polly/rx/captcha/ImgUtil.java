package polly.rx.captcha;

import static com.googlecode.javacv.cpp.opencv_core.cvLine;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_core.cvSet;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_OTSU;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opencv.core.Mat;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;


public final class ImgUtil {
    
    public static class BoundingBox implements Comparable<BoundingBox> {
        private int x;
        private int y;
        private int maxX;
        private int maxY;
        private int width;
        private int height;
        
        private double integral;
        private double integralX;
        private double integralY;
        private double centerX;
        private double centerY;
        
        final Set<Interval> intervals;
        
        public BoundingBox(int x, int y) {
            super();
            this.x = x;
            this.y = y;
            this.intervals = new HashSet<>();
        }
        
        public void update(Interval iv) {
            this.x = Math.min(iv.xStart, this.x);
            this.y = Math.min(iv.y, this.y);
            this.maxX = Math.max(iv.xEnd, this.maxX);
            this.maxY = Math.max(iv.y, this.maxY);
            
            this.width = this.maxX - this.x;
            this.height = this.maxY - this.y;
            
            // update moments
            final double xHi = iv.xEnd;
            final double xLo = iv.xStart;
            final double y = iv.y;
            
            this.integral += xHi - xLo + 1.0;
            this.integralX += (xHi * (xHi + 1.0) - xLo * (xLo - 1.0)) / 2.0;
            this.integralY += (xHi - xLo + 1.0) * y;
            this.centerX = this.integralX / this.integral;
            this.centerY = this.integralY / this.integral;
            
            this.intervals.add(iv);
        }
        
        
        public int getWidth() {
            return this.width;
        }
        
        
        public int getHeight() {
            return this.height;
        }
        
        public double getCenterX() {
            return this.centerX - this.x;
        }
        
        
        public double getCenterY() {
            return this.centerY - this.y;
        }
        
        
        public double getIntegral() {
            return this.integral;
        }

        @Override
        public int compareTo(BoundingBox o) {
            final int c = Integer.compare(this.x, o.x);
            if (c == 0) {
                return Integer.compare(this.y, o.y);
            }
            return c;
        }
    }
    
    
    
    public static class Interval {
        int xStart;
        int xEnd; // excl;
        int y;
        int count;
        
        Interval parent;
        
        public Interval(int y, int xStart, int xEnd) {
            this.xStart = xStart;
            this.xEnd = xEnd;
            this.y = y;
            this.parent = this;
            this.count = 1;
        }
        
        
        
        public Interval translate(BoundingBox box) {
            final Interval r = new Interval(this.y - box.y, this.xStart - box.x, 
                    this.xEnd - box.x);
            return r;
        }
        
        
        
        public boolean isRoot() {
            return this == this.parent;
        }
        
        
        
        public Interval root() {
            Interval root = this;
            while (root.parent != root) root = root.parent;
            
            Interval current = this;
            while (current != root) {
                final Interval temp = current.parent;
                current.parent = root;
                current = temp;
            }
            assert this.parent == root;
            return this.parent;
        }
        
        public void unite(Interval other) {
            final Interval myRoot = this.root();
            final Interval otherRoot = other.root();
            
            final Interval newRoot;
            final Interval child;
            if (myRoot.y < otherRoot.y || myRoot.y == otherRoot.y && myRoot.xStart < otherRoot.xStart) {
                // this is the first
                newRoot = myRoot.parent;
                child = otherRoot.parent;
            } else {
                newRoot= otherRoot.parent;
                child = myRoot.parent;
            }
            
            newRoot.count = child.parent.count + newRoot.count;
            child.parent = newRoot;
        }
    }

    
    
    private ImgUtil() {}
    
    
    public static IplImage part(IplImage whole, BoundingBox part) {
        final IplImage newImage = IplImage.create(part.width, part.height, whole.depth(), 
                whole.nChannels());
        final int wholeWs = whole.widthStep();
        final int newWs = newImage.widthStep();
        final int c = whole.nChannels();
        
        final ByteBuffer wholeImg = whole.getByteBuffer();
        final ByteBuffer newImg = newImage.getByteBuffer();
        
        for (int y = 0; y < part.height; ++ y) {
            for (int x = 0; x < part.width; ++x) {
                final int srcVal = wholeImg.get(y * wholeWs + x * c) & 0xFF;
                newImg.put(y * newWs + x * c, (byte) srcVal);
            }
        }
        
        return newImage;
    }
    
    
    
    public static void rle(IplImage image, int minLength, 
            Collection<Interval> intervals) {
        
        intervals.clear();
        int threshold = 1;
        final ByteBuffer img = image.getByteBuffer();
        final int ws = image.widthStep();
        final int c = image.nChannels();
        
        for (int y = 0; y < image.height(); ++y) {
            final int lineStart = y * ws;
            int xStart = 0;
            int xEnd = 0;
            while (xEnd != image.width()) {
                
                while (xStart < image.width()) {
                    final int b = img.get(lineStart + xStart * c) & 0xFF;
                    if (b < threshold) { 
                        ++xStart;
                    } else { 
                        break;
                    }
                }
                xEnd = xStart;
                while (xEnd < image.width()) {
                    ++xEnd;
                    final int b = img.get(lineStart + xEnd * c) & 0xFF;
                    if (b < threshold)
                        break;
                }
                
                if (xEnd - xStart >= minLength) {
                    intervals.add(new Interval(y, xStart, xEnd - 1)); // make xEnd inclusive
                }
                xStart = xEnd;
            }
        }
    }
    
    
    
    public static void regionize(Collection<Interval> rle) {
        final Iterator<Interval> runIt = rle.iterator();
        final Iterator<Interval> followIt = rle.iterator();
        
        Interval run = runIt.next();
        Interval follow = followIt.next();
        boolean abort = false;
        while (!abort) {
            if (run.y == follow.y + 1 && follow.xStart <= run.xEnd && 
                    run.xStart <= follow.xEnd) { // use <= if xEnd is inclusive
                run.unite(follow);
            }
            
            if (follow.y + 1 < run.y || run.y == follow.y + 1 && 
                    follow.xEnd < run.xEnd) {
                follow = followIt.next();
            } else if (runIt.hasNext()) {
                run = runIt.next();
            } else {
                abort = true;
            }
        }
    }
    
    
    
    public static void boundingBoxes(Collection<Interval> regions, int minSize,
            Collection<BoundingBox> boxes) {
        boxes.clear();
        final Map<Interval, BoundingBox> bboxes = new HashMap<>();
        for (final Interval iv : regions) {
            if (iv.root().count < minSize) {
                continue;
            }
            BoundingBox bbox = null;
            if (iv.isRoot()) {
                bbox = new BoundingBox(iv.xStart, iv.y);
                bboxes.put(iv, bbox);
            } else {
                bbox = bboxes.get(iv.root());
            }
            bbox.update(iv);
        }
        boxes.addAll(bboxes.values());
    }
    
    
    
    public static IplImage imageFromRegions(IplImage src, Collection<Interval> rle) {
        final IplImage img = IplImage.create(src.width(), src.height(), src.depth(), 
                src.nChannels());
        
        cvSet(img, cvScalar(255, 255, 255, 255));
        for (final Interval iv : rle) {
            final CvPoint start = cvPoint(iv.xStart, iv.y);
            final CvPoint end = cvPoint(iv.xEnd - 1, iv.y);
            cvLine(img, start, end, cvScalar(0, 0, 0, 0), 1, 8, 0);
        }
        return img;
    }
    
    
    
    public static void drawPlus(IplImage img, int x, int y) {
        final int LENGTH = 3;
        final CvScalar c = cvScalar(50, 50, 50, 50);
        
        cvLine(img, cvPoint(x - LENGTH / 2, y), cvPoint(x + LENGTH / 2, y), c, 1, 8, 0);
        cvLine(img, cvPoint(x, y - LENGTH / 2), cvPoint(x, y + LENGTH / 2), c, 1, 8, 0);
    }
    
    
    public static void drawBox(IplImage img, BoundingBox box) {
        final CvScalar c = cvScalar(50, 50, 50, 50);
        cvLine(img, cvPoint(box.x, box.y), cvPoint(box.x, box.y + box.height), c, 1, 8, 0); // left line
        cvLine(img, cvPoint(box.x, box.y), cvPoint(box.x + box.width, box.y), c, 1, 8, 0); // top line
        cvLine(img, cvPoint(box.x + box.width, box.y), cvPoint(box.x + box.width, box.y + box.height), c, 1, 8, 0); // right line
        cvLine(img, cvPoint(box.x + box.width, box.y + box.height), cvPoint(box.x + box.width, box.y + box.height), c, 1, 8, 0); // bottom line
    }
    
    
    
    public static void eraseNeighbors(IplImage src, int threshold) {
        int FILTER_SIZE = 3;
        assert FILTER_SIZE % 2 != 0 : "Filter size must be odd"; //$NON-NLS-1$
        
        final IplImage cpy = src.clone();
        
        final ByteBuffer cpyImg = cpy.getByteBuffer();
        final ByteBuffer srcImg = src.getByteBuffer();
        
        final int ws = src.widthStep();
        final int c = src.nChannels();
        final int r = FILTER_SIZE / 2; // border of the filter
        final int lower = -r;
        final int upper = r + 1;
        
        for (int y = r; y < src.height() - r; ++ y) {
            for (int x = r; x < src.width() - r; ++x) {
                
                // count black pixels in filter neighborhood
                int sum = 0;
                for (int i = lower; i < upper; ++i) {
                    for (int j = lower; j < upper; ++j) {
                        final int ty = (y + i) * ws;
                        final int tx = (x + j) * c;
                        final int val = cpyImg.get(ty + tx) & 0xFF;
                        if (val == 0) {
                            ++sum;
                        }
                    }
                }
                
                // wipe out current pixel if too many black pixels around
                if (sum > threshold) {
                    srcImg.put(y * ws + x * c, (byte) 0);
                }
            }
        }
    }
    
    
    
    public static void extractFeatures(IplImage image, List<BoundingBox> boxes) {
        boxes.clear();
        
        cvThreshold(image, image, 0, 255, CV_THRESH_BINARY | CV_THRESH_OTSU);
        ImgUtil.eraseNeighbors(image, 5);
        final List<Interval> rle = new ArrayList<>();
        
        ImgUtil.rle(image, 1, rle);
        ImgUtil.regionize(rle);
        ImgUtil.boundingBoxes(rle, 10, boxes);
        Collections.sort(boxes);
    }
    
    
    
    public static IplImage imageFromBoundingBox(BoundingBox box) {
        final IplImage img = IplImage.create(box.width, box.height, 
                8, 1);
        
        cvSet(img, CvScalar.BLACK);
        for (final Interval iv : box.intervals) {
            final Interval ivT = iv.translate(box);
            final CvPoint start = cvPoint(ivT.xStart, ivT.y);
            final CvPoint end = cvPoint(ivT.xEnd, ivT.y);
            cvLine(img, start, end, CvScalar.WHITE, 1, 8, 0);
        }
        return img;
    }
    
    
    
    public static CanvasFrame showImage(IplImage image, String caption) {
        final CanvasFrame frame = new CanvasFrame(caption);
        final int width = image.width();
        final int height = image.height();
        frame.setCanvasSize(image.width(), image.height());
        frame.getCanvas().setSize(width, height);
        frame.pack();
        frame.showImage(image);
        return frame;
    }
    
    
    
    static public BufferedImage Mat2BufferedImage(Mat m) {
        // source:
        // http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
        // Fastest code
        // The output can be assigned either to a BufferedImage or to an Image
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster()
                .getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }
    
}
