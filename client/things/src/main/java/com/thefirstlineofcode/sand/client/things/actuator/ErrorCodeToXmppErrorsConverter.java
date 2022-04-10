package com.thefirstlineofcode.sand.client.things.actuator;

import java.util.Map;

import com.thefirstlineofcode.basalt.protocol.core.IError;

public class ErrorCodeToXmppErrorsConverter implements ILanActionErrorProcessor {
	private String model;
	private Map<String, Class<?>> errorCodeToXmppErrorTypes;
	
	public ErrorCodeToXmppErrorsConverter(String model, Map<String, Class<?>> errorCodeToXmppErrorTypes) {
		this.model = model;
		this.errorCodeToXmppErrorTypes = errorCodeToXmppErrorTypes;
	}
	
	@Override
	public String getModel() {
		return model;
	}
	
	@Override
	public IError processErrorCode(String errorCode) {
		Class<?> errorType = errorCodeToXmppErrorTypes.get(errorCode);
		
		try {
			return (IError)errorType.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error.", e);
		}
	}
	
}
