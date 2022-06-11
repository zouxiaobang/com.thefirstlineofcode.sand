package com.thefirstlineofcode.sand.client.location;

import java.util.Properties;

import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.protocols.location.LocateDevices;

public class LocationPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.registerParser(new IqProtocolChain(LocateDevices.PROTOCOL),
				new NamingConventionParserFactory<LocateDevices>(LocateDevices.class));
		chatSystem.registerTranslator(LocateDevices.class,
				new NamingConventionTranslatorFactory<LocateDevices>(LocateDevices.class));
		chatSystem.registerApi(IDeviceLocator.class, DeviceLocator.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterApi(IDeviceLocator.class);
		chatSystem.unregisterTranslator(LocateDevices.class);
		chatSystem.unregisterParser(new IqProtocolChain(LocateDevices.PROTOCOL));
	}

}
