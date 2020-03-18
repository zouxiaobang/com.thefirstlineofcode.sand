package com.firstlinecode.sand.server.lite.concentrator;

import com.firstlinecode.sand.server.framework.things.Device;
import com.firstlinecode.sand.server.framework.things.concentrator.IConcentrator;
import com.firstlinecode.sand.server.framework.things.concentrator.Node;

public class Concentrator implements IConcentrator {
	private Device device;
	
	public Concentrator(Device device) {
		this.device = device;
	}

	@Override
	public String[] getNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String requestNodeCreation(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String confirmNodeCreation(String deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

}
