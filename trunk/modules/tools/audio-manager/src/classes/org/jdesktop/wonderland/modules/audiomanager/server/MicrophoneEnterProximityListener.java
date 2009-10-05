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
package org.jdesktop.wonderland.modules.audiomanager.server;

import com.sun.mpk20.voicelib.app.AudioGroup;
import com.sun.mpk20.voicelib.app.AudioGroupListener;
import com.sun.mpk20.voicelib.app.AudioGroupPlayerInfo;
import com.sun.mpk20.voicelib.app.AudioGroupSetup;
import com.sun.mpk20.voicelib.app.FullVolumeSpatializer;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.Spatializer;
import com.sun.mpk20.voicelib.app.VoiceManager;

import com.sun.sgs.app.AppContext;


import java.lang.String;

import java.util.logging.Logger;

import org.jdesktop.wonderland.common.cell.CallID;
import org.jdesktop.wonderland.common.cell.CellID;

import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.ProximityListenerSrv;

import com.jme.bounding.BoundingVolume;

import java.io.Serializable;

import org.jdesktop.wonderland.common.cell.security.ViewAction;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.server.cell.CellResourceManager;
import org.jdesktop.wonderland.server.security.ActionMap;
import org.jdesktop.wonderland.server.security.Resource;
import org.jdesktop.wonderland.server.security.ResourceMap;
import org.jdesktop.wonderland.server.security.SecureTask;
import org.jdesktop.wonderland.server.security.SecurityManager;

/**
 * A server cell that provides a microphone proximity listener
 * @author jprovino
 */
public class MicrophoneEnterProximityListener implements ProximityListenerSrv, Serializable,
	AudioGroupListener {

    private static final Logger logger =
            Logger.getLogger(MicrophoneEnterProximityListener.class.getName());

    private CellID cellID;
    private String name;
    private double volume;

    public MicrophoneEnterProximityListener(CellMO cellMO, String name, double volume) {
	cellID = cellMO.getCellID();
        this.name = name;
	this.volume = volume;
    }

    public void viewEnterExit(boolean entered, CellID cellID,
            CellID viewCellID, BoundingVolume proximityVolume,
            int proximityIndex) {

	logger.info("viewEnterExit bounds:  " + entered + " cellID " + cellID
	    + " viewCellID " + viewCellID);

	new Exception("viewEnterExit bounds:  " + entered + " cellID " + cellID
	    + " viewCellID " + viewCellID + " bounds " + proximityVolume).printStackTrace();

	String callId = CallID.getCallID(viewCellID);

	if (entered) {
	    cellEntered(callId);
	} else {
	    cellExited(callId);
	}
    }

    private void cellEntered(String callId) {
        /*
         * The avatar has entered the Microphone cell.
         * Set the public and incoming spatializers for the avatar to be
         * the zero volume spatializer.
         * Set a private spatializer for the given fullVolume radius
         * for all the other avatars in the cell.
         * For each avatar already in the cell, set a private spatializer
         * for this avatar.
         */
        logger.info(callId + " entered microphone " + name);

        // get the security manager
        SecurityManager security = AppContext.getManager(SecurityManager.class);
        CellResourceManager crm = AppContext.getManager(CellResourceManager.class);

        // create a request
        Action viewAction = new ViewAction();
        Resource resource = crm.getCellResource(this.cellID);
        if (resource != null) {
            // there is security on this cell perform the enter notification
            // securely
            ActionMap am = new ActionMap(resource, new Action[] { viewAction });
            ResourceMap request = new ResourceMap();
            request.put(resource.getId(), am);

            // perform the security check
            security.doSecure(request, new CellBoundsEnteredTask(resource.getId(), callId));
        } else {
            // no security, just make the call directly
            cellBoundsEntered(callId);
        }
    }

    private class CellBoundsEnteredTask implements SecureTask, Serializable {
        private String resourceID;
        private String callId;

        public CellBoundsEnteredTask(String resourceID, String callId) {
            this.resourceID = resourceID;
            this.callId = callId;
        }

        public void run(ResourceMap granted) {
            ActionMap am = granted.get(resourceID);
            if (am != null && !am.isEmpty()) {
                // request was granted -- the user has permission to enter
                cellBoundsEntered(callId);
            } else {
                logger.warning("Access denied to enter microphone bounds");
            }
        }
    }

    private void cellBoundsEntered(String callId) {
        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        Player player = vm.getPlayer(callId);

        if (player == null) {
            logger.warning("Can't find player for " + callId);
            return;
        }

        AudioGroup audioGroup = vm.getAudioGroup(name);

        if (audioGroup == null) {
            AudioGroupSetup ags = new AudioGroupSetup();

            ags.spatializer = new FullVolumeSpatializer();

            ags.spatializer.setAttenuator(Spatializer.DEFAULT_MAXIMUM_VOLUME);

	    ags.audioGroupListener = this;

            audioGroup = vm.createAudioGroup(name, ags);
        }

        audioGroup.addPlayer(player, new AudioGroupPlayerInfo(false,
            AudioGroupPlayerInfo.ChatType.PUBLIC));
    }

    public void playerAdded(AudioGroup audioGroup, Player player, AudioGroupPlayerInfo info) {
        player.attenuateOtherGroups(audioGroup, 0, 0);
    }

    private void cellExited(String callId) {
        logger.info(callId + " exited microphone " + name);

        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        AudioGroup audioGroup = vm.getAudioGroup(name);

        if (audioGroup == null) {
            logger.warning("Not a member of audio group " + name);
            return;
        }

        Player player = vm.getPlayer(callId);

        if (player == null) {
            logger.warning("Can't find player for " + callId);
            return;
        }

        audioGroup.removePlayer(player);
    }

    public void playerRemoved(AudioGroup audioGroup, Player player, AudioGroupPlayerInfo info) {
	logger.info("Attenuate other groups... " + audioGroup + " player " + player
	    + " info " + info);

        player.attenuateOtherGroups(audioGroup, AudioGroup.DEFAULT_SPEAKING_ATTENUATION,
            AudioGroup.DEFAULT_LISTEN_ATTENUATION);

        if (audioGroup.getNumberOfPlayers() == 0) {
            AppContext.getManager(VoiceManager.class).removeAudioGroup(audioGroup);
        }
    }

    public void changeName(String name) {
	if (this.name.equals(name)) {
	    return;
	}

        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        AudioGroup audioGroup = vm.getAudioGroup(name);

	this.name = name;

	AudioGroupSetup ags = new AudioGroupSetup();

        ags.spatializer = new FullVolumeSpatializer();

        ags.spatializer.setAttenuator(Spatializer.DEFAULT_MAXIMUM_VOLUME);

        AudioGroup newAudioGroup = vm.createAudioGroup(name, ags);

        if (audioGroup != null) {
	    Player[] players = audioGroup.getPlayers();

	    for (int i = 0; i < players.length; i++) {
		AudioGroupPlayerInfo info = audioGroup.getPlayerInfo(players[i]);

		audioGroup.removePlayer(players[i]);
		newAudioGroup.addPlayer(players[i], info);
	    }
	}
    }

    public void remove() {
        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        AudioGroup audioGroup = vm.getAudioGroup(name);

        if (audioGroup == null) {
            return;
        }

        vm.removeAudioGroup(audioGroup);
    }

}
