package com.firstlinecode.sand.server.platform.internal;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.firstlinecode.granite.framework.core.commons.osgi.OsgiUtils;
import com.firstlinecode.granite.framework.core.supports.IApplicationComponentService;
import com.firstlinecode.sand.server.platform.SandCommandProvider;

public class Activator implements BundleActivator {
	private ServiceRegistration<CommandProvider> srCommandProvider;
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		srCommandProvider = bundleContext.registerService(CommandProvider.class,
				createSandCommandProvider(bundleContext), null);
	}

	private CommandProvider createSandCommandProvider(BundleContext bundleContext) {
		CommandProvider commandProvider = new SandCommandProvider();
		
		IApplicationComponentService appComponentService = OsgiUtils.getService(bundleContext, IApplicationComponentService.class);
		appComponentService.inject(commandProvider, bundleContext);
		
		return commandProvider;
	}
	
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		if (srCommandProvider != null)
			srCommandProvider.unregister();
	}

}
