package com.firstlinecode.sand.server.stream;

import org.pf4j.Extension;

import com.firstlinecode.granite.framework.core.repository.IComponentsContributor;

@Extension
public class ComponentContributor implements IComponentsContributor {

	@Override
	public Class<?>[] getComponentClasses() {
		return new Class<?>[] {
			DeviceSocketMessageReceiver.class
		};
	}

}
