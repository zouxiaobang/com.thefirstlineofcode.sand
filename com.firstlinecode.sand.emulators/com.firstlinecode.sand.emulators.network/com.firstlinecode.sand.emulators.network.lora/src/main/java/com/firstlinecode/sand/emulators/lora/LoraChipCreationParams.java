package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.commuication.ParamsMap;

public class LoraChipCreationParams extends ParamsMap {
	public static final String PARAM_NAME_TYPE = "type";
	public static final String PARAM_NAME_ADDRESS = "address";	
	public static final int DEFAULT_FREQUENCY_BAND = 0;
	
	public LoraChipCreationParams() {
		addParams(PARAM_NAME_TYPE, LoraChip.Type.NORMAL);
		addParams(PARAM_NAME_ADDRESS, LoraAddress.randomLoraAddress(DEFAULT_FREQUENCY_BAND));
	}
	
	public LoraChipCreationParams(LoraChip.Type type) {
		this(type, null);
	}
	
	public LoraChipCreationParams(LoraAddress address) {
		this(null, address);
	}

	
	public LoraChipCreationParams(LoraChip.Type type, LoraAddress address) {
		if (type != null)
			addParams(PARAM_NAME_TYPE, type);
		
		if (address != null)
			addParams(PARAM_NAME_ADDRESS, address);
	}

	public void setType(LoraChip.Type type) {
		addParams(PARAM_NAME_TYPE, type);
	}
	
	public LoraChip.Type getType() {
		return getParam(PARAM_NAME_TYPE);
	}

}
