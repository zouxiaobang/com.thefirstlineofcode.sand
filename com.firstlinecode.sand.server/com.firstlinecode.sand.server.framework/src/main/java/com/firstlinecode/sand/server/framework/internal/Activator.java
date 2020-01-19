package com.firstlinecode.sand.server.framework.internal;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.firstlinecode.granite.framework.core.supports.IApplicationComponentService;
import com.firstlinecode.sand.server.framework.platform.SandCommandProvider;

public class Activator implements BundleActivator {
	private ServiceRegistration<CommandProvider> srCommandProvider;
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		srCommandProvider = bundleContext.registerService(CommandProvider.class,
				createSandCommandProvider(bundleContext), null);
	}

	private CommandProvider createSandCommandProvider(BundleContext bundleContext) {
		ServiceReference<IApplicationComponentService> srAppComponentService =
				bundleContext.getServiceReference(IApplicationComponentService.class);
		if (srAppComponentService == null)
			throw new IllegalStateException("Can't get application component service.");
		
		IApplicationComponentService appComponentService = bundleContext.getService(srAppComponentService);
		CommandProvider commandProvider = new SandCommandProvider();
		appComponentService.inject(commandProvider, bundleContext);
		
		return commandProvider;
	}
	
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		if (srCommandProvider != null)
			srCommandProvider.unregister();
	}

}
