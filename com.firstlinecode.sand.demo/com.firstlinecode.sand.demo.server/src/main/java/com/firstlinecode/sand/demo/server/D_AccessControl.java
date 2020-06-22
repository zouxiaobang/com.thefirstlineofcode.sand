package com.firstlinecode.sand.demo.server;

import com.firstlinecode.granite.framework.core.supports.data.IIdProvider;
import com.firstlinecode.sand.demo.protocols.acl.AccessControl;

public class D_AccessControl extends AccessControl implements IIdProvider<String> {
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
