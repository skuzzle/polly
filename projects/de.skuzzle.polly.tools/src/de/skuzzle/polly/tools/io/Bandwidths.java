package de.skuzzle.polly.tools.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;


public class Bandwidths {
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // create big test file
        final long bigTestFileSize = megaBytesToBytes(1000);
        final byte[] buffer = new byte[80000];
        final int bytesPerSecond = 524288;
        Arrays.fill(buffer, (byte) 65);
        
        final AllocationStrategy as = new AllocationStrategy() {
            @Override
            public void close() throws IOException {
            }
            @Override
            public void registerConsumer(Object obj) {
            }
            @Override
            public double getSpeed() {
                return 0;
            }
            @Override
            public void consumerFinished(Object obj) {
            }
            @Override
            public int allocate(Object source, int bytes) {
                return bytes;
            }
        };
        
        try (OutputStream out = new FileOutputStream(new File("C:\\Users\\Simon\\Desktop\\temp.txt"))) {
            final BandwidthOutputStream bandOut = new BandwidthOutputStream(out, as);
            
            int written = 0;
            while (written < bigTestFileSize) {
                bandOut.write(buffer, 0, buffer.length);
                written += buffer.length;
                double kbs = bandOut.getSpeed();
                System.out.println("Written: " + written + " bytes, Speed: " + kbs + " kb/s");
            }
            bandOut.close();
        }
    }

    private final static int KILO_BYTE = 1 << 10;
    
    private final static int MEGA_BYTE = 1 << 20;
    
    
    
    public static AllocationStrategy bytesPerSecond(int bytes) {
        return new IntervalAllocationStrategy(bytes, 1000);
    }
    
    
    
    public static AllocationStrategy kBytesPerSecond(int kBytes) {
        return new IntervalAllocationStrategy(kBytes * 1024, 1000);
    }
    
    
    
    public static long kiloBytesToBytes(int kb) {
        return kb * KILO_BYTE;
    }
    
    
    
    public static long kiloBitsToBytes(int kb) {
        return (kb >> 3) * KILO_BYTE;
    }
    
    
    
    public static long megaBytesToBytes(int mb) {
        return mb * MEGA_BYTE;
    }
    
    
    
    public static long megaBitsToBytes(int mb) {
        return (mb >> 3) * MEGA_BYTE;
    }
}
