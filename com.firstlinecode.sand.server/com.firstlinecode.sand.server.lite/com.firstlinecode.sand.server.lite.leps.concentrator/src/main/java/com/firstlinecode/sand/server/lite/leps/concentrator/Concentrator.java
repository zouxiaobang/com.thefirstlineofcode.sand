package com.firstlinecode.sand.server.lite.leps.concentrator;

import com.firstlinecode.sand.server.framework.devices.Device;
import com.firstlinecode.sand.server.framework.devices.concentrator.IConcentrator;
import com.firstlinecode.sand.server.framework.devices.concentrator.Node;

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
