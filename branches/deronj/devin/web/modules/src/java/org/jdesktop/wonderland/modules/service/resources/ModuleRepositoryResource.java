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

package org.jdesktop.wonderland.modules.service.resources;

import java.io.StringWriter;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.jdesktop.wonderland.modules.ModuleRepository;
import org.jdesktop.wonderland.modules.ModuleRepository.Repository;
import org.jdesktop.wonderland.modules.service.InstalledModule;
import org.jdesktop.wonderland.modules.service.ModuleManager;
import org.jdesktop.wonderland.modules.service.ModuleManager.State;

/**
 * The ModuleRepositoryResource class is a Jersey RESTful service that returns
 * the repository information about a module given its name encoding into a
 * request URI. The getModuleRepository() method handles the HTTP GET request.
 * <p>
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Path("/{modulename}/repository")
public class ModuleRepositoryResource {
    
    @Context
    private UriInfo context;
    
    /**
     * Returns the repository information about a module, given its module name
     * encoded into the URI. The format of the URI is:
     * <p>
     * /module/{modulename}/repository
     * <p>
     * where {modulename} is the name of the module. All spaces in the module
     * name must be encoded to %20. Returns BAD_REQUEST to the HTTP connection if
     * the module name is invalid or if there was an error encoding the module's
     * information.
     * 
     * @param moduleName The unique name of the module
     * @return An XML encoding of the module's basic information
     */
    @GET
    @ProduceMime("text/plain")
    public Response getModuleRepository(@PathParam("modulename") String moduleName) {
        /* Fetch thhe error logger for use in this method */
        Logger logger = ModuleManager.getLogger();
        
        /* Fetch the module from the module manager */
        ModuleManager mm = ModuleManager.getModuleManager();
        InstalledModule im = (InstalledModule)mm.getModule(moduleName, State.INSTALLED);
        if (im == null) {
            /* Log an error and return an error response */
            logger.warning("ModuleManager: unable to locate module " + moduleName);
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();
        }
        
        /*
         * If there are any entries with %WL_SERVER%, then replace with the
         * server path to the asset.
         */
        UriBuilder artBuilder = context.getBaseUriBuilder().path(moduleName).path("art");
        String hostname = artBuilder.build().toString();
        
        /* Fetch the module repository, return an error if it does not exist */
        ModuleRepository mr = im.getModuleRepository();
        if (mr == null) {
            /*
             * If the repository doesn't exist (perhaps from a missing repository.xml
             * file, then create a fallback response with this server as the
             * master.
             */
            logger.warning("ModuleManager: null repository for module: " + moduleName);
            logger.warning("ModuleManager: sending this server as fallback: " + hostname);
            ModuleRepository newRepository = new ModuleRepository();
            Repository rep = new Repository();
            rep.url = hostname;
            rep.isServer = true;
            newRepository.setMaster(rep);
 
            /* Write the XML encoding to a writer and return it */
            StringWriter sw = new StringWriter();
            try {
                /* Formulate the HTTP response and send the string */
                newRepository.encode(sw);
                ResponseBuilder rb = Response.ok(sw.toString());
                return rb.build();
            } catch (javax.xml.bind.JAXBException excp) {
                /* Log an error and return an error response */
                logger.warning("ModuleManager: unable to serialize repository for " + moduleName);
                ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
                return rb.build();
            }
        }
        
        /* Since we potentially edit fields below, make a copy of the repository */
        ModuleRepository newRepository = new ModuleRepository(mr);
        
        /* Replace the master if its string is the special %WL_SERVER% */
        if (newRepository.getMaster() != null && newRepository.getMaster().url.compareTo(ModuleRepository.WL_SERVER) == 0) {
            Repository rep = new Repository();
            rep.url = hostname;
            rep.isServer = true;
            newRepository.setMaster(rep);
        }
        
        /* Replace the mirrors if its string is the special %WL_SERVER% */
        Repository mirrors[] = newRepository.getMirrors();
        if (mirrors != null) {
            for (int i = 0; i < mirrors.length; i++) {
                if (mirrors[i] != null && mirrors[i].url.compareTo(ModuleRepository.WL_SERVER) == 0) {
                    Repository rep = new Repository();
                    rep.url = hostname;
                    rep.isServer = true;
                    mirrors[i] = rep;
                }
            }
            newRepository.setMirrors(mirrors);
        }
        
        /* Write the XML encoding to a writer and return it */
        StringWriter sw = new StringWriter();
        try {
            /* Formulate the HTTP response and send the string */
            newRepository.encode(sw);
            ResponseBuilder rb = Response.ok(sw.toString());
            return rb.build();
        } catch (javax.xml.bind.JAXBException excp) {
            /* Log an error and return an error response */
            logger.warning("ModuleManager: unable to serialize repository for " + moduleName);
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();
        }
    }
}