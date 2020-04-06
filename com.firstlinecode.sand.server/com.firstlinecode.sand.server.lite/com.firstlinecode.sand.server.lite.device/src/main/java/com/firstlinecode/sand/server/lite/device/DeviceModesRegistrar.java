package com.firstlinecode.sand.server.lite.device;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.firstlinecode.sand.protocols.core.ModeDescriptor;
import com.firstlinecode.sand.server.device.IDeviceManager;
import com.firstlinecode.sand.server.device.IDeviceModesProvider;

@Component
public class DeviceModesRegistrar implements BundleContextAware {
	private BundleContext bundleContext;
	
	@Autowired
	private IDeviceManager deviceManager;
	
	@PostConstruct
	public void init() {
		try {
			ServiceReference<?>[] srs = bundleContext.getAllServiceReferences(IDeviceModesProvider.class.getName(), null);
			for (ServiceReference<?> sr : srs) {
				IDeviceModesProvider modesProvider = (IDeviceModesProvider)bundleContext.getService(sr);
				registerModes(modesProvider);
			}
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
		
		try {
			bundleContext.addServiceListener(new ServiceListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void serviceChanged(ServiceEvent event) {
					if (event.getType() == ServiceEvent.REGISTERED) {
						ServiceReference<IDeviceModesProvider> sr = (ServiceReference<IDeviceModesProvider>)event.getServiceReference();
						IDeviceModesProvider modesProvider = bundleContext.getService(sr);
						registerModes(modesProvider);
					} else if (event.getType() == ServiceEvent.UNREGISTERING) {
						ServiceReference<IDeviceModesProvider> sr = (ServiceReference<IDeviceModesProvider>)event.getServiceReference();
						IDeviceModesProvider modesProvider = bundleContext.getService(sr);
						unregisterModes(modesProvider);
					} else {
						// NO-OP
					}
				}
			}, String.format("(%s=%s)", Constants.OBJECTCLASS, IDeviceModesProvider.class.getName()));
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void registerModes(IDeviceModesProvider modesProvider) {
		Map<String, ModeDescriptor> modes = modesProvider.provide();
		for (String mode : modes.keySet()) {
			deviceManager.registerMode(mode, modes.get(mode));
		}
	}
	
	private void unregisterModes(IDeviceModesProvider modesProvider) {
		Map<String, ModeDescriptor> modes = modesProvider.provide();
		for (String mode : modes.keySet()) {
			deviceManager.unregisterMode(mode);
		}
	}
	
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
}
