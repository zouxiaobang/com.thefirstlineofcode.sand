package com.thefirstlineofcode.sand.demo.server;

import java.util.HashMap;
import java.util.Map;

import com.thefirstlineofcode.basalt.xmpp.core.Protocol;
import com.thefirstlineofcode.sand.protocols.actuator.actions.Restart;
import com.thefirstlineofcode.sand.protocols.actuator.actions.ShutdownSystem;
import com.thefirstlineofcode.sand.protocols.actuator.actions.Stop;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;
import com.thefirstlineofcode.sand.protocols.things.simple.camera.OpenLiveStreaming;
import com.thefirstlineofcode.sand.protocols.things.simple.camera.TakePhoto;
import com.thefirstlineofcode.sand.protocols.things.simple.camera.TakeVideo;

public class ScRbp3bModelDescriptor extends ModelDescriptor {
	public static final String MODEL_NAME = "SC-RBP3B";
	public static final String THING_TYPE = "Simple Camera";

	public ScRbp3bModelDescriptor() {
		super(MODEL_NAME, true, createSupportedActions(), null);
	}

	private static Map<Protocol, Class<?>> createSupportedActions() {
		Map<Protocol, Class<?>> supportedActions = new HashMap<>();
		supportedActions.put(Stop.PROTOCOL, Stop.class);
		supportedActions.put(Restart.PROTOCOL, Restart.class);
		supportedActions.put(ShutdownSystem.PROTOCOL, ShutdownSystem.class);
		supportedActions.put(TakePhoto.PROTOCOL, TakePhoto.class);
		supportedActions.put(TakeVideo.PROTOCOL, TakeVideo.class);
		supportedActions.put(OpenLiveStreaming.PROTOCOL, OpenLiveStreaming.class);
		
		return supportedActions;
	}
}
