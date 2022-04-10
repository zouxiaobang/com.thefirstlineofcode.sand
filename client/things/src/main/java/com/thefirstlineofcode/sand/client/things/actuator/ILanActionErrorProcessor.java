package com.thefirstlineofcode.sand.client.things.actuator;

import com.thefirstlineofcode.basalt.protocol.core.IError;

public interface ILanActionErrorProcessor {
	String getModel();
	IError processErrorCode(String errorCode);
}
