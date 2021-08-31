package com.firstlinecode.sand.server.lite.ibdr;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.granite.framework.core.adf.IApplicationComponentService;
import com.firstlinecode.granite.framework.core.adf.IApplicationComponentServiceAware;
import com.firstlinecode.granite.framework.core.repository.IInitializable;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.ibdr.IDeviceRegistrationCustomizer;
import com.firstlinecode.sand.server.ibdr.IDeviceRegistrationCustomizerProxy;

@Component
@Transactional
public class DeviceRegistrationCustomizerProxy implements IDeviceRegistrationCustomizerProxy,
			IInitializable, IApplicationComponentServiceAware {
	private static final Logger logger = LoggerFactory.getLogger(DeviceRegistrationCustomizerProxy.class);
	
	private IApplicationComponentService appComponentService;
	private IDeviceRegistrationCustomizer real;
	
	@Override
	public Object executeCustomizedTask(String deviceId, DeviceIdentity identity) {
		if (real == null)
			return null;
		
		return real.executeCustomizedTask(deviceId, identity);
	}
	
	@Override
	public boolean isBinded() {
		return real != null;
	}

	@Override
	public void init() {
		List<Class<? extends IDeviceRegistrationCustomizer>> registrationCustomizerClasses =
				appComponentService.getExtensionClasses(IDeviceRegistrationCustomizer.class);
		if (registrationCustomizerClasses == null || registrationCustomizerClasses.size() == 0) {
			logger.info("No registration customizer found.");
			return;
		}
		
		if (registrationCustomizerClasses.size() != 1) {
			logger.warn("Multiple device registration customizer found. Ignore them all.");
			return;
		}
		
		real = appComponentService.createExtension(IDeviceRegistrationCustomizer.class);
		
		if (logger.isInfoEnabled()) {
			logger.info("Found a device registration customizer which's type is {}.", real.getClass().getName());
		}
	}
	
	@Override
	public void setApplicationComponentService(IApplicationComponentService appComponentService) {
		this.appComponentService = appComponentService;
	}

}
