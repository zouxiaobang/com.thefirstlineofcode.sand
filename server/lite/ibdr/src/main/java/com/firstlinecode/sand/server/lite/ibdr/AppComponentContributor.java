package com.firstlinecode.sand.server.lite.ibdr;

import org.pf4j.Extension;

import com.firstlinecode.granite.framework.core.adf.IAppComponentsContributor;

@Extension
public class AppComponentContributor implements IAppComponentsContributor {

	@Override
	public Class<?>[] getAppComponentClasses() {
		return new Class<?>[] {
			DeviceRegistrar.class
		};
	}

}
