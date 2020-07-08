package com.firstlinecode.sand.demo.client;

import java.util.Properties;

import com.firstlinecode.basalt.oxm.convention.NamingConventionParserFactory;
import com.firstlinecode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.firstlinecode.basalt.protocol.core.ProtocolChain;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.chalk.IChatSystem;
import com.firstlinecode.chalk.IPlugin;
import com.firstlinecode.sand.demo.protocols.AccessControlList;

public class DemoPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.registerParser(ProtocolChain.first(Iq.PROTOCOL).next(AccessControlList.PROTOCOL),
				new NamingConventionParserFactory<AccessControlList>(AccessControlList.class));
		chatSystem.registerTranslator(AccessControlList.class,
				new NamingConventionTranslatorFactory<AccessControlList>(AccessControlList.class));
		chatSystem.registerApi(IAclService.class, AclService.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterApi(IAclService.class);
		chatSystem.unregisterTranslator(AccessControlList.class);
		chatSystem.unregisterParser(ProtocolChain.first(Iq.PROTOCOL).next(AccessControlList.PROTOCOL));
	}

}
