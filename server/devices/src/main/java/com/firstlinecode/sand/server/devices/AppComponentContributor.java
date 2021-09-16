package com.firstlinecode.sand.server.devices;

import org.pf4j.Extension;

import com.firstlinecode.granite.framework.core.adf.IAppComponentsContributor;

@Extension
public class AppComponentContributor implements IAppComponentsContributor {

	@Override
	public Class<?>[] getAppComponentClasses() {
		return new Class<?>[] {
			DeviceAuthorizationDelegator.class
		};
	}

}
