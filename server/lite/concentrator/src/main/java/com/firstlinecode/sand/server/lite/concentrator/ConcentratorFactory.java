package com.firstlinecode.sand.server.lite.concentrator;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.granite.framework.core.adf.IApplicationComponentService;
import com.firstlinecode.granite.framework.core.adf.IApplicationComponentServiceAware;
import com.firstlinecode.sand.server.concentrator.IConcentrator;
import com.firstlinecode.sand.server.concentrator.IConcentratorFactory;
import com.firstlinecode.sand.server.devices.Device;
import com.firstlinecode.sand.server.devices.IDeviceManager;

@Component
@Transactional
public class ConcentratorFactory implements IConcentratorFactory, IApplicationComponentServiceAware {
	private IApplicationComponentService appComponentService;
	
	@Autowired
	private IDeviceManager deviceManager;
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public boolean isConcentrator(Device device) {
		if (!deviceManager.isRegistered(device.getDeviceId())) {			
			return false;
		}
		
		return deviceManager.isConcentrator(device.getModel());
	}

	@Override
	public IConcentrator getConcentrator(Device device) {		
		if (!isConcentrator(device))
			throw new IllegalArgumentException(String.format("Device[%s] isn't a concentrator.", device.getDeviceId()));
		
		Concentrator concentrator = new Concentrator(device.getDeviceId(), sqlSession);
		appComponentService.inject(concentrator);
		
		return concentrator;
		
	}

	@Override
	public void setApplicationComponentService(IApplicationComponentService appComponentService) {
		this.appComponentService = appComponentService;
	}

}
