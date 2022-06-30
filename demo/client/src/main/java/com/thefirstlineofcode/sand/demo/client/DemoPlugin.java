package com.thefirstlineofcode.sand.demo.client;

import java.util.Properties;

import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.basalt.xmpp.core.IqProtocolChain;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;
import com.thefirstlineofcode.sand.demo.protocols.AuthorizedDevices;

public class DemoPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.registerParser(new IqProtocolChain(AccessControlList.PROTOCOL),
				new NamingConventionParserFactory<AccessControlList>(AccessControlList.class));
		chatSystem.registerTranslator(AccessControlList.class,
				new NamingConventionTranslatorFactory<AccessControlList>(AccessControlList.class));
		chatSystem.registerApi(IAclService.class, AclService.class);
		
		chatSystem.registerParser(new IqProtocolChain(AuthorizedDevices.PROTOCOL),
				new NamingConventionParserFactory<AuthorizedDevices>(AuthorizedDevices.class));
		chatSystem.registerTranslator(AuthorizedDevices.class,
				new NamingConventionTranslatorFactory<AuthorizedDevices>(AuthorizedDevices.class));
		chatSystem.registerApi(IAuthorizedDevicesService.class, AuthorizedDevicesService.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterApi(IAuthorizedDevicesService.class);
		chatSystem.unregisterTranslator(AuthorizedDevices.class);
		chatSystem.unregisterParser(new IqProtocolChain(AuthorizedDevices.PROTOCOL));
		
		chatSystem.unregisterApi(IAclService.class);
		chatSystem.unregisterTranslator(AccessControlList.class);
		chatSystem.unregisterParser(new IqProtocolChain(AccessControlList.PROTOCOL));
	}

}
