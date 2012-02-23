package polly.porat.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


import polly.network.protocol.LogItem;


public class LogFileParser {

    private final static Pattern PATTERN = Pattern.compile("\\s+");
    private final static int DATE = 0;
    private final static int TIME = 1;
    private final static int THREAD = 2;
    private final static int LEVEL = 3;
    private final static int SOURCE = 4;
    private final static int MESSAGE = 5;
    
    
    
    public static List<LogItem> parseLogFile(File file) 
                throws IOException {
        BufferedReader r = null;
        List<LogItem> result = new ArrayList<LogItem>();
        
        try {
            r = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            
            String line = null;
            while ((line = r.readLine()) != null) {
                try {
                    result.add(parseLine(line));
                } catch (ParseException e) {
                    continue;
                }
            }
            
            return result;
        } finally {
            if (r != null) {
                r.close();
            }
        }
    }
    
    
    
    private static LogItem parseLine(String line) throws ParseException {
        /*
         * Static pattern for logger lines:
         * <date> <time> [<thread>] <level> <message>
         */
        String[] parts = PATTERN.split(line, 6);
        try {
            return new LogItem(
                parseDate(parts[DATE], parts[TIME]), 
                parts[LEVEL], 
                parts[THREAD], 
                parts[SOURCE], 
                parts[MESSAGE]);
        } catch (Exception e) {
            throw new ParseException("", 0);
        }
    }
    
    
    
    private static long parseDate(String date, String time) throws ParseException {
        String compund = date + " " + time;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.parse(compund).getTime();
    }
}