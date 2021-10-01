package com.thefirstlineofcode.sand.server.lite.devices;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thefirstlineofcode.granite.framework.core.adf.IApplicationComponentService;
import com.thefirstlineofcode.granite.framework.core.adf.IApplicationComponentServiceAware;
import com.thefirstlineofcode.granite.framework.core.repository.IInitializable;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;
import com.thefirstlineofcode.sand.server.devices.IDeviceModelsProvider;

@Component
public class DeviceModelsRegistrar implements IApplicationComponentServiceAware, IInitializable {
	private IApplicationComponentService appComponentService;
	
	@Autowired
	private IDeviceManager deviceManager;
	
	@Override
	public void init() {
		List<IDeviceModelsProvider> modelsProviders = appComponentService.getPluginManager().getExtensions(IDeviceModelsProvider.class);
		if (modelsProviders == null || modelsProviders.size() == 0)
			return;
		
		for (IDeviceModelsProvider modelsProvider : modelsProviders) {
			registerModels(modelsProvider);
		}
	}
	
	private void registerModels(IDeviceModelsProvider modesProvider) {
		Map<String, ModelDescriptor> models = modesProvider.provide();
		for (String model : models.keySet()) {
			deviceManager.registerModel(model, models.get(model));
		}
	}

	@Override
	public void setApplicationComponentService(IApplicationComponentService appComponentService) {
		this.appComponentService = appComponentService;
	}
}
