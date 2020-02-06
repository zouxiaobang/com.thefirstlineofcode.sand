package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.things.commuication.ParamsMap;

public class LoraChipCreationParams extends ParamsMap {
	private static final String PARAM_NAME_TYPE = "type";
	
	public LoraChipCreationParams() {
		addParams(PARAM_NAME_TYPE, null);
	}
	
	public LoraChipCreationParams(LoraChip.Type type) {
		if (type != null)
			addParams(PARAM_NAME_TYPE, type);
	}

	public void setType(LoraChip.Type type) {
		addParams(PARAM_NAME_TYPE, type);
	}
	
	public LoraChip.Type getType() {
		return getParams(PARAM_NAME_TYPE);
	}

}
