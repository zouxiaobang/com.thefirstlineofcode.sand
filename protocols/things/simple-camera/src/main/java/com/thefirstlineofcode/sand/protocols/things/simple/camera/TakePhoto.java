package com.thefirstlineofcode.sand.protocols.things.simple.camera;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

@ProtocolObject(namespace="urn:leps:iot:actuator:simple-camera", localName="take-photo")
public class TakePhoto {
	public static Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator:simple-camera", "take-photo");
	
	private int prepareTime;
	private String photoUrl;
	
	public TakePhoto() {}
	
	public TakePhoto(int prepareTime) {
		this.prepareTime = prepareTime;
	}
	
	public TakePhoto(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	
	public int getPrepareTime() {
		return prepareTime;
	}

	public void setPrepareTime(int prepareTime) {
		this.prepareTime = prepareTime;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}
	
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
}
