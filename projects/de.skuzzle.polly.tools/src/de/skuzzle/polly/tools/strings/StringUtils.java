package de.skuzzle.polly.tools.strings;

public final class StringUtils {

    private final static String DOTS = " [...]";
    
    public static String shorten(String s, int maxLength) {
        if (s == null) {
            return "";
        }
        //maxLength = Math.min(maxLength, s.length() + DOTS.length());
        if (s.length() < maxLength) {
            return s;
        }
        final int endIdx = Math.max(1, maxLength - DOTS.length());
        return s.substring(0, endIdx) + DOTS;
    }
    
    
    
    public String firstParagraph(String s) {
        if (s == null) {
            return "";
        }
        return firstParagraph(s, s.length());
    }
    
    
    
    public static String firstParagraph(String s, int maxLength) {
        if (s == null) {
            return "";
        }
        final int i = s.indexOf("\n");
        final String firstParagraph = i == -1 
                ? s 
                : s.substring(0, i) + DOTS;
        return shorten(firstParagraph, maxLength);
    }
    
    
    
    // Algorithm from: http://mrfoo.de/archiv/1176-Levenshtein-Distance-in-Java.html
    public static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        int n = s.length(); // length of s
        int m = t.length(); // length of t

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        int p[] = new int[n + 1]; // 'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; // placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left
                // and up +cost
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return p[n];
    }
    
    
    
    public static void padSpaces(int desiredLength, int currentLength, StringBuilder b) {
        int spaces = desiredLength - currentLength;
        if (spaces <= 0) {
            return;
        }
        for (int i = 0; i < spaces; ++i) {
            b.append(" ");
        }
    }
    
    
    
    public static String replace(String original, char[] characters, 
            String[] replacement) {
        if (characters.length != replacement.length) {
            throw new IllegalArgumentException("different array size");
        }
        final StringBuilder b = new StringBuilder(original.length() + 16);
        for (int i = 0; i < original.length(); ++i) {
            final char c = original.charAt(i);
            boolean mustAppend = true;
            
            for (int j = 0; j < characters.length; ++j) {
                final char c2 = characters[j];
                if (c == c2) {
                    b.append(replacement[j]);
                    mustAppend = false;
                    break;
                }
            }
            
            if (mustAppend) {
                b.append(c);
            }
        }
        return b.toString();
    }
}
