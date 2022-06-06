package com.thefirstlineofcode.sand.demo.server.models;

import java.util.HashMap;
import java.util.Map;

import org.pf4j.Extension;

import com.thefirstlineofcode.granite.framework.core.repository.IInitializable;
import com.thefirstlineofcode.sand.emulators.models.SgLe01ModelDescriptor;
import com.thefirstlineofcode.sand.emulators.models.SlLe01ModelDescriptor;
import com.thefirstlineofcode.sand.emulators.models.SlWe01ModelDescriptor;
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
		SgLe01ModelDescriptor sgLe01 = new SgLe01ModelDescriptor();
		models.put(sgLe01.getName(), sgLe01);
		SlLe01ModelDescriptor slLe01 = new SlLe01ModelDescriptor();
		models.put(slLe01.getName(), slLe01);
		SlWe01ModelDescriptor slWe01 = new SlWe01ModelDescriptor();
		models.put(slWe01.getName(), slWe01);
	}
}
