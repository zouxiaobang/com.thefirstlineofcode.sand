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
	public boolean isConcentrator(String deviceId) {
		if (!deviceManager.isRegistered(deviceId)) {			
			return false;
		}
		
		Device device = deviceManager.getByDeviceId(deviceId);
		return deviceManager.isConcentrator(device.getModel());
	}

	@Override
	public IConcentrator getConcentrator(String deviceId) {		
		if (!isConcentrator(deviceId))
			throw new IllegalArgumentException(String.format("Device[%s] isn't a concentrator.", deviceId));
		
		String concentratorDeviceName = deviceManager.getDeviceNameByDeviceId(deviceId);
		
		return applicationContext.getBean(Concentrator.class, concentratorDeviceName, sqlSession);
	}

	@Override
	public boolean isLanNode(String nodeDeviceId) {
		if (!deviceManager.isRegistered(nodeDeviceId)) {			
			return false;
		}
		
		return getConcentrationMapper().selectCountByNode(nodeDeviceId) != 0;
	}
	
	private ConcentrationMapper getConcentrationMapper() {
		return sqlSession.getMapper(ConcentrationMapper.class);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public String getConcentratorDeviceNameByNodeDeviceId(String nodeDeviceId) {
		D_Concentration concentration = getConcentrationMapper().selectConcentrationByNode(nodeDeviceId);
		if (concentration == null)
			return null;
		
		return concentration.getConcentratorDeviceName();
	}

}
