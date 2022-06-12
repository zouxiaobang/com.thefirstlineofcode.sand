package com.thefirstlineofcode.sand.client.remoting;

import java.util.Properties;

import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;

public class RemotingPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		// TODO Auto-generated method stub
		chatSystem.registerParser(new IqProtocolChain(Execution.PROTOCOL),
				new NamingConventionParserFactory<Execution>(Execution.class));
		chatSystem.registerTranslator(Execution.class, new NamingConventionTranslatorFactory<>(Execution.class));
		chatSystem.registerApi(IRemoting.class, Remoting.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		// TODO Auto-generated method stub
		chatSystem.unregisterApi(IRemoting.class);
		chatSystem.unregisterTranslator(Execution.class);
		chatSystem.unregisterParser(new IqProtocolChain(Execution.PROTOCOL));
	}

}
