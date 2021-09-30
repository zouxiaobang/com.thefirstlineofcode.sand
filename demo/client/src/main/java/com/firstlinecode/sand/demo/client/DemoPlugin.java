package com.firstlinecode.sand.demo.client;

import java.util.Properties;

import com.firstlinecode.chalk.core.IChatSystem;
import com.firstlinecode.chalk.core.IPlugin;
import com.firstlinecode.sand.demo.protocols.AccessControlList;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;

public class DemoPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.registerParser(new IqProtocolChain(AccessControlList.PROTOCOL),
				new NamingConventionParserFactory<AccessControlList>(AccessControlList.class));
		chatSystem.registerTranslator(AccessControlList.class,
				new NamingConventionTranslatorFactory<AccessControlList>(AccessControlList.class));
		chatSystem.registerApi(IAclService.class, AclService.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterApi(IAclService.class);
		chatSystem.unregisterTranslator(AccessControlList.class);
		chatSystem.unregisterParser(new IqProtocolChain(AccessControlList.PROTOCOL));
	}

}
