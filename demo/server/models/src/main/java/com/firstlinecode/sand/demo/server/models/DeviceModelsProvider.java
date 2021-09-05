package com.firstlinecode.sand.demo.server.models;

import java.util.HashMap;
import java.util.Map;

import org.pf4j.Extension;

import com.firstlinecode.granite.framework.core.repository.IInitializable;
import com.firstlinecode.sand.emulators.models.Ge01ModelDescriptor;
import com.firstlinecode.sand.emulators.models.Le01ModelDescriptor;
import com.firstlinecode.sand.emulators.models.Le02ModelDescriptor;
import com.firstlinecode.sand.protocols.core.ModelDescriptor;
import com.firstlinecode.sand.server.devices.IDeviceModelsProvider;

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
		
		Le02ModelDescriptor le02 = new Le02ModelDescriptor();
		models.put(le02.getName(), le02);
	}
}
