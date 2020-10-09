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

import com.firstlinecode.sand.protocols.core.ModelDescriptor;
import com.firstlinecode.sand.server.device.IDeviceManager;
import com.firstlinecode.sand.server.device.IDeviceModelsProvider;

@Component
public class DeviceModesRegistrar implements BundleContextAware {
	private BundleContext bundleContext;
	
	@Autowired
	private IDeviceManager deviceManager;
	
	@PostConstruct
	public void init() {
		try {
			ServiceReference<?>[] srs = bundleContext.getAllServiceReferences(IDeviceModelsProvider.class.getName(), null);
			for (ServiceReference<?> sr : srs) {
				IDeviceModelsProvider modelsProvider = (IDeviceModelsProvider)bundleContext.getService(sr);
				registerModes(modelsProvider);
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
						ServiceReference<IDeviceModelsProvider> sr = (ServiceReference<IDeviceModelsProvider>)event.getServiceReference();
						IDeviceModelsProvider modelsProvider = bundleContext.getService(sr);
						registerModes(modelsProvider);
					} else if (event.getType() == ServiceEvent.UNREGISTERING) {
						ServiceReference<IDeviceModelsProvider> sr = (ServiceReference<IDeviceModelsProvider>)event.getServiceReference();
						IDeviceModelsProvider modelsProvider = bundleContext.getService(sr);
						unregisterModels(modelsProvider);
					} else {
						// NO-OP
					}
				}
			}, String.format("(%s=%s)", Constants.OBJECTCLASS, IDeviceModelsProvider.class.getName()));
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void registerModes(IDeviceModelsProvider modesProvider) {
		Map<String, ModelDescriptor> modes = modesProvider.provide();
		for (String mode : modes.keySet()) {
			deviceManager.registerModel(mode, modes.get(mode));
		}
	}
	
	private void unregisterModels(IDeviceModelsProvider modelsProvider) {
		Map<String, ModelDescriptor> models = modelsProvider.provide();
		for (String model : models.keySet()) {
			deviceManager.unregisterMode(model);
		}
	}
	
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
}
