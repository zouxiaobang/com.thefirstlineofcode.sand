package com.firstlinecode.sand.server.lite.concentrator;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.sand.server.framework.things.Device;
import com.firstlinecode.sand.server.framework.things.IDeviceManager;
import com.firstlinecode.sand.server.framework.things.concentrator.IConcentrator;
import com.firstlinecode.sand.server.framework.things.concentrator.IConcentratorFactory;

@Component
@Transactional
public class ConcentratorFactory implements IConcentratorFactory {
	@Autowired
	private IDeviceManager deviceManager;
	
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public boolean isConcentrator(Device device) {
		if (!deviceManager.isRegistered(device.getDeviceId())) {			
			return false;
		}
		
		return deviceManager.isConcentrator(device.getMode());
	}

	@Override
	public IConcentrator getConcentrator(Device device) {		
		if (!isConcentrator(device))
			throw new IllegalArgumentException(String.format("Device[%s] isn't a concentrator.", device.getDeviceId()));
		
		return new Concentrator(device.getDeviceId(), sqlSession);
		
	}

}
