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
package org.jdesktop.wonderland.modules.audiomanager.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;

/**
 * The component server state
 * @author jprovino
 */
@XmlRootElement(name="audio-treatment-component")
@ServerState
//public class AudioTreatmentComponentServerState extends CellComponentServerState {
public class AudioTreatmentComponentServerState extends AudioParticipantComponentServerState {

    public enum PlayWhen {
	ALWAYS,
	FIRST_IN_RANGE,
	MANUAL
    }

    @XmlElement(name="groupId")
    private String groupId = "";

    @XmlElements({
	@XmlElement(name="treatment")
    })
    private String[] treatments = new String[0];

    @XmlElement(name="volume")
    private double volume = 1;

    @XmlElement(name="playWhen")
    private PlayWhen playWhen = PlayWhen.ALWAYS;

    @XmlElement(name="extent")
    private double extent = 10;

    @XmlElement(name="fullVolumeAreaPercent")
    private double fullVolumeAreaPercent = 25;

    @XmlElement(name="distanceAttenuated")
    private boolean distanceAttenuated = true;

    @XmlElement(name="falloff")
    private double falloff = 50;

    public AudioTreatmentComponentServerState() {
	super(false, false);
    }

    public void setGroupId(String groupId) {
	this.groupId = groupId;
    }

    @XmlTransient
    public String getGroupId() {
	return groupId;
    }

    public void setTreatments(String[] treatments) {
	this.treatments = treatments;
    }

    @XmlTransient
    public String[] getTreatments() {
	return treatments;
    }

    public void setVolume(double volume) {
	this.volume = volume;
    }

    @XmlTransient
    public double getVolume() {
	return volume;
    }
   
    public void setPlayWhen(PlayWhen playWhen) {
	this.playWhen = playWhen;
    }

    @XmlTransient
    public PlayWhen getPlayWhen() {
	return playWhen;
    }

    public void setExtent(double extent) {
	this.extent = extent;
    }

    @XmlTransient
    public double getExtent() {
	return extent;
    }

    public void setFullVolumeAreaPercent(double fullVolumeAreaPercent) {
	this.fullVolumeAreaPercent = fullVolumeAreaPercent;
    }

    @XmlTransient
    public double getFullVolumeAreaPercent() {
	return fullVolumeAreaPercent;
    }
    
    public void setDistanceAttenuated(boolean distanceAttenuated) {
	this.distanceAttenuated = distanceAttenuated;
    }

    @XmlTransient
    public boolean getDistanceAttenuated() {
	return distanceAttenuated;
    }

    public void setFalloff(double falloff) {
	this.falloff = falloff;
    }

    @XmlTransient
    public double getFalloff() {
	return falloff;
    }

    public String getServerComponentClassName() {
	return "org.jdesktop.wonderland.modules.audiomanager.server.AudioTreatmentComponentMO";
    }

}