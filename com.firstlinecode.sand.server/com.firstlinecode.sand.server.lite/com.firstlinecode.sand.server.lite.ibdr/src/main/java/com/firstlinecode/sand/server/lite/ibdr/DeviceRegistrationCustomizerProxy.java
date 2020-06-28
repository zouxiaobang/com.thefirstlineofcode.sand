package com.firstlinecode.sand.server.lite.ibdr;

import java.util.Map;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.granite.framework.core.connection.IClientConnectionContext;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.ibdr.IDeviceRegistrationCustomizer;
import com.firstlinecode.sand.server.ibdr.IDeviceRegistrationCustomizerProxy;

@Component
@Transactional
public class DeviceRegistrationCustomizerProxy implements IDeviceRegistrationCustomizerProxy, OsgiServiceLifecycleListener {
	private IDeviceRegistrationCustomizer real;
	
	@Override
	public Object executeCustomizedTask(String deviceId, DeviceIdentity identity) {
		if (real == null)
			return null;
		
		return real.executeCustomizedTask(deviceId, identity);
	}

	@Override
	public void processResult(IClientConnectionContext context, Object result) {
		if (real == null)
			return;
		
		real.processResult(context, result);
	}
	
	@Override
	public boolean isBinded() {
		return real != null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void bind(Object service, Map properties) throws Exception {
		this.real = (IDeviceRegistrationCustomizer)service;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void unbind(Object service, Map properties) throws Exception {
		real = null;
	}

}
