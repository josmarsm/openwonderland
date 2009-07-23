/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath"
 * exception as provided by Sun in the License file that accompanied
 * this code.
 */
package org.jdesktop.wonderland.client.jme.artimport;

import java.net.URL;

/**
 * Loaders can subclass this (and also provide ui) to allow loader options to be set.
 * There is no api yet for the modules to provide the extended ui, its TODO
 *
 * @author paulby
 */
public class ImportSettings {
    private final URL modelURL;

    public ImportSettings(URL modelURL) {
        this.modelURL = modelURL;
    }

    public URL getModelURL() {
        return modelURL;
    }
}