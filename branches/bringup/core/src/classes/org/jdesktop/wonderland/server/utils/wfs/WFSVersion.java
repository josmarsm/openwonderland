/**
 * Project Looking Glass
 *
 * $RCSfile: WFSVersion.java,v $
 *
 * Copyright (c) 2004-2007, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision$
 * $Date$
 * $State: Exp $
 */

package org.jdesktop.wonderland.server.utils.wfs;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * The WFSVersion class represents a version of the Wonderland File System. Note
 * that this version reflect the WFS architecture specification version defining
 * the structure of the file system, etc. Each individual cell has a separate
 * version number too. The WFS version consists of a 'major' and 'minor' version
 * number, both integers.
 * <p>
 * This class follows the Java Bean pattern (default constructor, setter/getter
 * methods) so that it may be serialised to/from disk. The static decode() and
 * encode methods take an instance of a WFSVersion class and perform the loading
 * and saving from/to disk.
 *
 * @author jslott
 */
public class WFSVersion {
    /* The major and minor version numbers */
    private int major;
    private int minor;
    
    /** Default constructor */
    public WFSVersion() {}
    
    /** Constructor which takes major/minor version number */
    public WFSVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }
    
    /* Java Bean Setter/Getter methods */
    public int getMajor() { return this.major; }
    public void setMajor(int major) { this.major = major; }
    public int getMinor() { return this.minor; }
    public void setMinor(int minor) { this.minor = minor; }
    
    /**
     * Returns the version as a string: <major>.<minor>
     */
    public String toString() {
        return Integer.toString(this.getMajor()) + "." + Integer.toString(this.getMinor());
    }
    
    /**
     * Takes the input stream of the XML file on disk (or elsewhere) and
     * instantiates an instance of the WFSVersion class
     * <p>
     * @param is The input stream of the version XML file
     * @throw FileNotFoundException If the input file does not exist
     * @throw ClassCastException If the input file does not map to WFSAlises
     * @throw ArrayIndexOutOfBoundsException If the file contains no objects
     */
    public static WFSVersion decode(InputStream is) {
        XMLDecoder d       = new XMLDecoder(new BufferedInputStream(is));
        WFSVersion version = (WFSVersion)d.readObject();
        d.close();
        
        return version;
    }
    
    /**
     * Takes the File of the XML file on disk (or elsewhere) and writes the
     * XMLVersion class to that file.
     * <p>
     * @param file The output XML file to encode
     * @throw FileNotFoundException If the file cannot be created
     * @throw SecurityException If the file is not permitted to be created
     *
     */
    public void encode(File file) throws FileNotFoundException {
        FileOutputStream fos     = new FileOutputStream(file);
        XMLEncoder       e       = new XMLEncoder(new BufferedOutputStream(fos));
        e.writeObject(this);
        e.close();
    }
    
    /**
     * Main method which writes a sample WFSVersion class to disk
     */
    public static void main(String args[]) {
        try {
            WFSVersion version = new WFSVersion();
            version.setMajor(5);
            version.setMinor(3);
            version.encode(new File("version.xml"));
        } catch (java.lang.Exception excp) {
            System.out.println(excp.toString());
        }
    }
}