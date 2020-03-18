package com.firstlinecode.sand.server.lite.concentrator;

import com.firstlinecode.sand.server.framework.things.Device;
import com.firstlinecode.sand.server.framework.things.concentrator.IConcentrator;
import com.firstlinecode.sand.server.framework.things.concentrator.IConcentratorFactory;

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
