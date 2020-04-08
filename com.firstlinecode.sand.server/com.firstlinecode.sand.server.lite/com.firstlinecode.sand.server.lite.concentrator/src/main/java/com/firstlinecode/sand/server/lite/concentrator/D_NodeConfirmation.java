package com.firstlinecode.sand.server.lite.concentrator;

import com.firstlinecode.granite.framework.core.supports.data.IIdProvider;
import com.firstlinecode.sand.server.concentrator.NodeConfirmation;

public class D_NodeConfirmation extends NodeConfirmation implements IIdProvider<String> {
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
