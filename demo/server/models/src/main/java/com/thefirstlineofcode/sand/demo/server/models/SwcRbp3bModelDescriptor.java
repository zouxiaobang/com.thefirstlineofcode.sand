package com.thefirstlineofcode.sand.demo.server.models;

import java.util.HashMap;
import java.util.Map;

import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;
import com.thefirstlineofcode.sand.protocols.things.simple.webcam.OpenLiveStreaming;
import com.thefirstlineofcode.sand.protocols.things.simple.webcam.TakePhoto;
import com.thefirstlineofcode.sand.protocols.things.simple.webcam.TakeVideo;

public class SwcRbp3bModelDescriptor extends ModelDescriptor {
	public static final String MODEL_NAME = "SWC-RBP3B";
	public static final String THING_TYPE = "Simple Webcam";

	public SwcRbp3bModelDescriptor() {
		super(MODEL_NAME, true, createSupportedActions(), null);
	}

	private static Map<Protocol, Class<?>> createSupportedActions() {
		Map<Protocol, Class<?>> supportedActions = new HashMap<>();
		supportedActions.put(TakePhoto.PROTOCOL, TakePhoto.class);
		supportedActions.put(TakeVideo.PROTOCOL, TakeVideo.class);
		supportedActions.put(OpenLiveStreaming.PROTOCOL, OpenLiveStreaming.class);
		
		return supportedActions;
	}
}
