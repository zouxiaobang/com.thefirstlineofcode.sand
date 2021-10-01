package com.thefirstlineofcode.sand.server.lite.devices;

import com.thefirstlineofcode.granite.framework.core.adf.data.IIdProvider;
import com.thefirstlineofcode.sand.server.devices.DeviceAuthorization;

public class D_DeviceAuthorization extends DeviceAuthorization implements IIdProvider<String> {
	private String id;

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

}
