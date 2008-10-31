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

package org.jdesktop.wonderland.client.modules;

import java.util.logging.Logger;
import org.jdesktop.wonderland.common.modules.ModuleArtList;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.common.modules.ModuleWFSList;

/**
 * The CachedModule class stores information about a single module, such as
 * its basic information (ModuleInfo), its repository information, the list
 * of checksums for its assets, a list of assets, and list of WFSs.
 * <p>
 * All CacheModule objects must be created with a ModuleInfo that identifies
 * the module uniquely.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CachedModule {

    private ModuleInfo       moduleInfo       = null; /* Basic module info   */
    private ModuleArtList    moduleArt        = null; /* List of module art  */
    private ModuleWFSList    moduleWFS        = null; /* List of module WFSs */
//    private ModuleRequires   moduleRequires   = null; /* Module dependencies */
//    private ModuleRepository moduleRepository = null; /* Module repository   */
    
    /* The error logger */
    private static Logger logger = Logger.getLogger(CachedModule.class.getName());
    
    /** Constructor, takes the essential module information */
    public CachedModule(ModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
    }
    
    /**
     * Returns the basic module information.
     * 
     * @return The ModuleInfo object for the module
     */
    public ModuleInfo getInfo() {
        return this.moduleInfo;
    }
    
    /**
     * Returns the list of module art, loading it from the server if necessary
     * 
     * @return A list of module art
     */
    public synchronized ModuleArtList getArt() {
        if (this.moduleArt == null) {
            this.moduleArt = ModuleUtils.fetchModuleArtList(moduleInfo.getName());
        }
        return this.moduleArt;
    }
    
    /**
     * Returns the list of module wfs, loading it from the server if necessary
     * 
     * @return A list of module wfs
     */
    public synchronized ModuleWFSList getWFS() {
        if (this.moduleWFS == null) {
            this.moduleWFS = ModuleUtils.fetchModuleWFSList(moduleInfo.getName());
        }
        return this.moduleWFS;
    }
}
