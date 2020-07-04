package com.firstlinecode.sand.demo.server.lite;

import com.firstlinecode.granite.framework.core.supports.data.IIdProvider;
import com.firstlinecode.sand.demo.protocols.AccessControlEntry;

public class D_AccessControlEntry extends AccessControlEntry implements IIdProvider<String> {
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
