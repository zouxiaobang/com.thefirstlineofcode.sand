package com.firstlinecode.sand.server.lite.concentrator;

import org.apache.ibatis.session.SqlSession;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.granite.framework.core.commons.osgi.IBundleContextAware;
import com.firstlinecode.granite.framework.core.supports.IApplicationComponentService;
import com.firstlinecode.granite.framework.core.supports.IApplicationComponentServiceAware;
import com.firstlinecode.sand.server.concentrator.IConcentrator;
import com.firstlinecode.sand.server.concentrator.IConcentratorFactory;
import com.firstlinecode.sand.server.device.Device;
import com.firstlinecode.sand.server.device.IDeviceManager;

@Component
@Transactional
public class ConcentratorFactory implements IConcentratorFactory, IBundleContextAware, IApplicationComponentServiceAware {
	private BundleContext bundleContext;
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
		
		return deviceManager.isConcentrator(device.getMode());
	}

	@Override
	public IConcentrator getConcentrator(Device device) {		
		if (!isConcentrator(device))
			throw new IllegalArgumentException(String.format("Device[%s] isn't a concentrator.", device.getDeviceId()));
		
		Concentrator concentrator = new Concentrator(device.getDeviceId(), sqlSession);
		appComponentService.inject(concentrator, bundleContext);
		
		return concentrator;
		
	}

	@Override
	public void setApplicationComponentService(IApplicationComponentService appComponentService) {
		this.appComponentService = appComponentService;
	}

	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

}
