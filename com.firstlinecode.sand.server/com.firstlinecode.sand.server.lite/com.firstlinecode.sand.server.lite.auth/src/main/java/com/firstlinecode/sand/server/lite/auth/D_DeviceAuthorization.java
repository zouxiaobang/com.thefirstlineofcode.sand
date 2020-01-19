package com.firstlinecode.sand.server.lite.auth;

import com.firstlinecode.granite.framework.core.supports.data.IIdProvider;
import com.firstlinecode.sand.server.framework.auth.DeviceAuthorization;

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
