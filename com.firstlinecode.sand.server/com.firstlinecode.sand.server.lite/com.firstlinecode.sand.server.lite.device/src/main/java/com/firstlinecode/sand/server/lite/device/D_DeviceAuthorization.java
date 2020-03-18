package com.firstlinecode.sand.server.lite.device;

import com.firstlinecode.granite.framework.core.supports.data.IIdProvider;
import com.firstlinecode.sand.server.framework.things.DeviceAuthorization;

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
