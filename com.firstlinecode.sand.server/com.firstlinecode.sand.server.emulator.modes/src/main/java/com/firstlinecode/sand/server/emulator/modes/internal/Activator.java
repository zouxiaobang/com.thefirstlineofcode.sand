package com.firstlinecode.sand.server.emulator.modes.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.firstlinecode.granite.framework.core.commons.osgi.OsgiUtils;
import com.firstlinecode.granite.framework.core.supports.IApplicationComponentService;

public class Activator implements BundleActivator {
	private ModesRegistrar modesRegistrar;
	
	@Override
	public void start(BundleContext context) throws Exception {
		modesRegistrar = new ModesRegistrar();
		IApplicationComponentService appComponentService = OsgiUtils.getService(context, IApplicationComponentService.class);
		appComponentService.inject(modesRegistrar, context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		modesRegistrar.unregisterModes();
	}

}
