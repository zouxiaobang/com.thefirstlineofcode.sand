package com.thefirstlineofcode.sand.emulators.models;

import java.util.HashMap;
import java.util.Map;

import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.UnexpectedRequest;
import com.thefirstlineofcode.sand.emulators.things.ILight;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;
import com.thefirstlineofcode.sand.protocols.devices.light.Flash;

public class Le01ModelDescriptor extends ModelDescriptor {
	public static final String MODEL_NAME = "LE01";

	public Le01ModelDescriptor() {
		super(MODEL_NAME, false, createSupportedActions(), null);
	}

	private static Map<Protocol, Class<?>> createSupportedActions() {
		Map<Protocol, Class<?>> supportedActions = new HashMap<>();
		supportedActions.put(Flash.PROTOCOL, Flash.class);
		
		return supportedActions;
	}
	
	public static Map<String, Class<?>> getLe01ErrorCodeToXmppErrors() {
		Map<String, Class<?>> errorCodeToXmppErrors = new HashMap<>();
		errorCodeToXmppErrors.put(ILight.ERROR_CODE_NOT_REMOTE_CONTROL_STATE,
				UnexpectedRequest.class);
		
		return errorCodeToXmppErrors;
	}
}
