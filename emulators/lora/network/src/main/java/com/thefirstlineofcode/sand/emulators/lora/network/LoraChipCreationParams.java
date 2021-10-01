package com.thefirstlineofcode.sand.emulators.lora.network;

import com.thefirstlineofcode.sand.client.things.commuication.ParamsMap;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public class LoraChipCreationParams extends ParamsMap {
	public static final String PARAM_NAME_TYPE = "type";
	public static final String PARAM_NAME_ADDRESS = "address";	
	
	public LoraChipCreationParams() {
		addParams(PARAM_NAME_TYPE, LoraChip.PowerType.NORMAL);
		addParams(PARAM_NAME_ADDRESS, LoraAddress.randomLoraAddress());
	}
	
	public LoraChipCreationParams(LoraChip.PowerType type) {
		this(type, null);
	}
	
	public LoraChipCreationParams(LoraAddress address) {
		this(null, address);
	}

	
	public LoraChipCreationParams(LoraChip.PowerType type, LoraAddress address) {
		if (type != null)
			addParams(PARAM_NAME_TYPE, type);
		
		if (address != null)
			addParams(PARAM_NAME_ADDRESS, address);
	}

	public void setType(LoraChip.PowerType type) {
		addParams(PARAM_NAME_TYPE, type);
	}
	
	public LoraChip.PowerType getType() {
		return getParam(PARAM_NAME_TYPE);
	}

}
