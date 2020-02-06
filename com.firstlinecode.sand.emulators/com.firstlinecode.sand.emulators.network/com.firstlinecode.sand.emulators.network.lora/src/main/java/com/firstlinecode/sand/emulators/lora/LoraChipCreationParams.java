package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.things.commuication.ParamsMap;

public class LoraChipCreationParams extends ParamsMap {
	private static final String PARAM_NAME_TYPE = "type";

	public void setType(ILoraChip.Type type) {
		addParams(PARAM_NAME_TYPE, type);
	}
	
	public ILoraChip.Type getType() {
		return getParams(PARAM_NAME_TYPE);
	}

}
