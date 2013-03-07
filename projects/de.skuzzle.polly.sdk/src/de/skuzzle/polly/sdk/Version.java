package de.skuzzle.polly.sdk;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * <p>This class represents a three-part version number. The parts are called
 * VERSION, REVISION and BUILD and are integer fields. A Version has a natural order,
 * which is given by the values of its parts in the order from VERSION to BUILD.</p>  
 * 
 * <p>A version number can optionally have a descriptive name.</p>
 * 
 * @author Simon
 * @version 28.07.2011
 * @since Beta 0.9
 * @see #compareTo(Version)
 */
public class Version implements Comparable<Version> {

    /**
     * The highest field part.
     */
    public final static int VERSION = 0;
    
    /**
     * The middle field part.
     */
    public final static int REVISION = 1;
    
    /**
     * The lowest field part.
     */
    public final static int BUILD = 2;
    
    private final static Pattern PATTERN = Pattern.compile(
        "(\\d+).(\\d+).(\\d+)( - (.+))?");
    
    private int[] fields;
    private String versionString;
    private String name;
    
    
    
    /**
     * <p>Creates a new version from a String. The String must follow exactly this 
     * formatting scheme: It must have three integer parts separated by a '.' (dot). Those
     * three parts can optionally be followed by a dash surrounded by spaces following a
     * description name of this version.</p>
     * 
     * <p>For example {@code 1.3.4} as well as {@code 0.2.1} and {@code 0.0.0} are valid
     * version strings as well as {@code 1.2.3 - beta}</p>
     * 
     * @param version The version string to parse.
     * @throws IllegalArgumentException If {@code version} does not follow the formatting
     *      rules.
     */
    public Version(String version) {
        Matcher m = PATTERN.matcher(version);
        if (!m.matches()) {
            throw new IllegalArgumentException("misformatted version string: " + version);
        }
        String v1 = version.substring(m.start(1), m.end(1));
        String v2 = version.substring(m.start(2), m.end(2));
        String v3 = version.substring(m.start(3), m.end(3));
        
        try {
            this.fields = new int[] {Integer.parseInt(v1), 
                                     Integer.parseInt(v2), 
                                     Integer.parseInt(v3)};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("misformatted version string: " + version);
        }
        if (m.groupCount() >= 5) {
            this.name = version.substring(m.start(5), m.end(5));
        } else {
            name = "";
        }

        this.versionString = version;
    }
    
    
    
    /**
     * Creates a new version from its three integer parts with no version name.
     * 
     * @param version The VERSION part.
     * @param rev The REVISION part.
     * @param build The BUILD part.
     */
    public Version(int version, int rev, int build) {
        this(version, rev, build, "");
    }
    
    
    
    /**
     * Creates a new version from its three integer parts and the given version name.
     * 
     * @param version The VERSION part.
     * @param rev The REVISION part.
     * @param build The BUILD part.
     * @param name The name of this version.
     * @since 0.9.1
     */
    public Version(int version, int rev, int build, String name) {
        this.fields = new int[] {version, rev, build};
        String tmp = name == null ? "" : name;
        tmp = tmp.equals("") ? "" : " - " + tmp;
        this.versionString = version + "." + rev + "." + build + tmp;
        this.name = name;
    }
    
    
    
    /**
     * Returns the value of any version field. Valid field values are: {@link #VERSION},
     * {@link #REVISION} and {@link #BUILD}.
     * @param field The version field.
     * @return The version value for that field.
     * @throws IndexOutOfBoundsException If {@code field} parameter is invalid.
     */
    public int get(int field) {
        if (field < VERSION || field > BUILD) {
            throw new IndexOutOfBoundsException("" + field);
        }
        return this.fields[field];
    }
    
    
    
    /**
     * Returns the version name of this version. If it has no version this is the empty
     * String.
     * 
     * @return The name of this version.
     * @since 0.9.1
     */
    public String getName() {
        return this.name;
    }
    
    
    
    /**
     * Return this version as a String.
     * @return A String representation for this Version.
     */
    @Override
    public String toString() {
        return this.versionString;
    }
    
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.fields);
        return result;
    }

    

    /**
     * Considers {@code obj} equals to {@code this} iff all three version parts
     * equals.
     * @return <code>true</code> if {@code obj} is equal to {@code this}.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Version)) {
            return false;
        }
        Version other = (Version) obj;
        if (!Arrays.equals(this.fields, other.fields)) {
            return false;
        }
        return true;
    }
    
    
    
    /**
     * Compares {@code this} to another Version. The ordering of Versions is defined as
     * follows: The version with the higher value in a higher field is considered greater
     * than the other. They are considered equal if all three version parts matches. The
     * highest field is {@link #VERSION}, the lowest field is {@link #BUILD}.
     * 
     * @param o The version to compare this version with.
     * @return An integer representing the order of these Versions in the means of the 
     *      Comparable contract.
     */
    @Override
    public int compareTo(Version o) {
        for (int i = 0; i < this.fields.length; ++i) {
            int diff = this.get(i) - o.get(i);
            if (diff != 0) {
                return diff;
            }
        }
        return 0;
    }
}
