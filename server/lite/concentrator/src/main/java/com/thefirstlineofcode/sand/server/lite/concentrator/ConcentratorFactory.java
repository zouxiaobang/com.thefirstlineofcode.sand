package com.thefirstlineofcode.sand.server.lite.concentrator;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thefirstlineofcode.sand.server.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.server.concentrator.IConcentratorFactory;
import com.thefirstlineofcode.sand.server.concentrator.Node;
import com.thefirstlineofcode.sand.server.devices.Device;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

@Component
@Transactional
public class ConcentratorFactory implements IConcentratorFactory, ApplicationContextAware {
	private ApplicationContext applicationContext;
	
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
		
		String concentratorDeviceName = deviceManager.getDeviceNameByDeviceId(device.getDeviceId());
		
		return applicationContext.getBean(Concentrator.class, concentratorDeviceName, sqlSession);
	}

	@Override
	public boolean isLanNode(String deviceId) {
		if (!deviceManager.isRegistered(deviceId)) {			
			return false;
		}
		
		Node node = getConcentrationMapper().selectNodeByDeviceId(deviceId);
		return node != null;
	}
	
	private ConcentrationMapper getConcentrationMapper() {
		return sqlSession.getMapper(ConcentrationMapper.class);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
