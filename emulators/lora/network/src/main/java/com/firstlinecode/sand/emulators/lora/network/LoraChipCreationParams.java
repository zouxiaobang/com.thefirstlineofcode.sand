package com.firstlinecode.sand.emulators.lora.network;

import com.firstlinecode.sand.client.things.commuication.ParamsMap;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class LoraChipCreationParams extends ParamsMap {
	public static final String PARAM_NAME_TYPE = "type";
	public static final String PARAM_NAME_ADDRESS = "address";	
	
	public LoraChipCreationParams() {
		addParams(PARAM_NAME_TYPE, LoraChip.Type.NORMAL);
		addParams(PARAM_NAME_ADDRESS, LoraAddress.randomLoraAddress());
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
