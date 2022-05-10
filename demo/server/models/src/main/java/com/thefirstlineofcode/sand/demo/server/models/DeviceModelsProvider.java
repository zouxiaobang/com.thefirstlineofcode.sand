package com.thefirstlineofcode.sand.demo.server.models;

import java.util.HashMap;
import java.util.Map;

import org.pf4j.Extension;

import com.thefirstlineofcode.granite.framework.core.repository.IInitializable;
import com.thefirstlineofcode.sand.emulators.models.Ge01ModelDescriptor;
import com.thefirstlineofcode.sand.emulators.models.Ld01ModelDescriptor;
import com.thefirstlineofcode.sand.emulators.models.Le01ModelDescriptor;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;
import com.thefirstlineofcode.sand.server.devices.IDeviceModelsProvider;

@Extension
public class DeviceModelsProvider implements IDeviceModelsProvider, IInitializable {
	private Map<String, ModelDescriptor> models = new HashMap<>();

	@Override
	public Map<String, ModelDescriptor> provide() {
		return models;
	}

	@Override
	public void init() {
		Ge01ModelDescriptor ge01 = new Ge01ModelDescriptor();
		models.put(ge01.getName(), ge01);
		Le01ModelDescriptor le01 = new Le01ModelDescriptor();
		models.put(le01.getName(), le01);
		
		Ld01ModelDescriptor ld01 = new Ld01ModelDescriptor();
		models.put(ld01.getName(), ld01);
	}
}
