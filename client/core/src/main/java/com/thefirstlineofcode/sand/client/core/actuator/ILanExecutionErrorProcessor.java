package com.thefirstlineofcode.sand.client.core.actuator;

import com.thefirstlineofcode.basalt.xmpp.core.IError;

public interface ILanExecutionErrorProcessor {
	String getModel();
	IError processErrorCode(String errorCode);
}
