package com.firstlinecode.sand.server.emulators.models.internal;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.firstlinecode.sand.emulators.models.Ge01ModelDescriptor;
import com.firstlinecode.sand.emulators.models.Le01ModelDescriptor;
import com.firstlinecode.sand.emulators.models.Le02ModelDescriptor;
import com.firstlinecode.sand.protocols.core.ModelDescriptor;
import com.firstlinecode.sand.server.device.IDeviceModelsProvider;

public class Activator implements BundleActivator {
	private ServiceRegistration<IDeviceModelsProvider> srModelsProvider;
	
	@Override
	public void start(BundleContext context) throws Exception {
		srModelsProvider = context.registerService(IDeviceModelsProvider.class, new DeviceModesProvider(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		srModelsProvider.unregister();
	}
	
	private class DeviceModesProvider implements IDeviceModelsProvider {
		private Map<String, ModelDescriptor> models = new HashMap<>();
		
		public DeviceModesProvider() {
			Ge01ModelDescriptor ge01 = new Ge01ModelDescriptor();
			models.put(ge01.getName(), ge01);
			Le01ModelDescriptor le01 = new Le01ModelDescriptor();
			models.put(le01.getName(), le01);
			
			Le02ModelDescriptor le02 = new Le02ModelDescriptor();
			models.put(le02.getName(), le02);
		}
		
		@Override
		public Map<String, ModelDescriptor> provide() {
			return models;
		}
		
	}
}
