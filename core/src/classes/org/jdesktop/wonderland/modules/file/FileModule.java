/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2008, Sun Microsystems, Inc., All Rights Reserved
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
 * $State$
 */

package org.jdesktop.wonderland.modules.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModuleResource;

/**
 * The FileModule class extends the Module abstract base class and represents
 * all modules that exist as a collection of directories and files on disk
 * representing the structure of the module.
 * <p>
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class FileModule extends Module {
    /* The root of the module directory structure on disk */
    private File root = null;

    
    /** Default constructor, takes a reference to the module directory root */
    public FileModule(File root) {
        super();
        this.root = root;
    }
    
    /**
     * Returns an input stream for the given resource, null upon error.
     * <p>
     * @param resource A resource contained within the archive
     * @return An input stream to the resource
     */
    public InputStream getInputStream(ModuleResource resource) {
        try {
            String resourcePath = "art/" + resource.getPathName();
            File entry = new File(this.root, resourcePath);
            return new FileInputStream(entry);
        } catch (java.io.IOException excp) {
            // print stack trace
            return null;
        }
    }
}
