package com.firstlinecode.sand.server.emulator.modes.internal;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.firstlinecode.sand.protocols.core.ModeDescriptor;
import com.firstlinecode.sand.server.device.IDeviceModesProvider;

public class Activator implements BundleActivator {
	// GE01: Gateway emulator 01
	private static final String MODE_GE01 = "GE01";
	// BE01: Light emulator 01
	private static final String MODE_BE01 = "LE01";
	
	private ServiceRegistration<IDeviceModesProvider> srModesProvider;
	
	@Override
	public void start(BundleContext context) throws Exception {
		srModesProvider = context.registerService(IDeviceModesProvider.class, new DeviceModesProvider(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		srModesProvider.unregister();
	}
	
	private class DeviceModesProvider implements IDeviceModesProvider {
		private Map<String, ModeDescriptor> modes;
		
		public DeviceModesProvider() {
			// TODO Auto-generated constructor stub
			modes = new HashMap<>();
			modes.put(MODE_GE01,
					new ModeDescriptor(true, getGE01SupportedActions(), getGE01SupportedEvents()));
			modes.put(MODE_BE01,
					new ModeDescriptor(true, getBE01SupportedActions(), getBE01SupportedEvents()));
		}
		
		private Map<String, Class<?>> getBE01SupportedEvents() {
			// TODO Auto-generated method stub
			return new HashMap<String, Class<?>>();
		}

		private Map<String, Class<?>> getBE01SupportedActions() {
			// TODO Auto-generated method stub
			return new HashMap<String, Class<?>>();
		}

		private Map<String, Class<?>> getGE01SupportedEvents() {
			// TODO Auto-generated method stub
			return new HashMap<String, Class<?>>();
		}

		private Map<String, Class<?>> getGE01SupportedActions() {
			// TODO Auto-generated method stub
			return new HashMap<String, Class<?>>();
		}
		
		@Override
		public Map<String, ModeDescriptor> provide() {
			return modes;
		}
		
	}
}
