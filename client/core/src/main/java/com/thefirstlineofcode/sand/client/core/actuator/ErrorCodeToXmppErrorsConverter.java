package com.thefirstlineofcode.sand.client.core.actuator;

import java.util.Map;

import com.thefirstlineofcode.basalt.protocol.core.IError;
import com.thefirstlineofcode.basalt.protocol.core.LangText;
import com.thefirstlineofcode.sand.client.core.ThingsUtils;

public class ErrorCodeToXmppErrorsConverter implements ILanExecutionErrorProcessor {
	private String model;
	private Map<String, Class<? extends IError>> errorCodeToXmppErrorTypes;
	
	public ErrorCodeToXmppErrorsConverter(String model, Map<String, Class<? extends IError>> errorCodeToXmppErrorTypes) {
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
			IError error = (IError)errorType.newInstance();
			error.setText(new LangText(ThingsUtils.getExecutionErrorDescription(model, errorCode)));
			
			return error;
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error.", e);
		}
	}
}
