package com.firstlinecode.sand.server.lite.devices;

import com.firstlinecode.granite.framework.core.adf.data.IIdProvider;
import com.firstlinecode.sand.server.devices.Device;

public class D_Device extends Device implements IIdProvider<String> {
	private String id;
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}
	
}
