package com.firstlinecode.sand.server.lite.leps.concentrator;

import com.firstlinecode.sand.server.framework.devices.Device;
import com.firstlinecode.sand.server.framework.devices.concentrator.IConcentrator;
import com.firstlinecode.sand.server.framework.devices.concentrator.IConcentratorFactory;

public class ConcentratorFactory implements IConcentratorFactory {
	@Override
	public boolean isConcentrator(Device device) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IConcentrator getConcentrator(Device device) {
		if (!isConcentrator(device))
			throw new IllegalArgumentException("Not a concentrator.");
		
		// TODO Auto-generated method stub
		return null;
	}

}
