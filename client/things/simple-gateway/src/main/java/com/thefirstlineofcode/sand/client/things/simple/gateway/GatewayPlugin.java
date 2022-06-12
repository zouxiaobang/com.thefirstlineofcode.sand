package com.thefirstlineofcode.sand.client.things.simple.gateway;

import java.util.Properties;

import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.client.remoting.RemotingPlugin;
import com.thefirstlineofcode.sand.protocols.things.simple.gateway.ChangeMode;

public class GatewayPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.register(RemotingPlugin.class);
		chatSystem.registerTranslator(ChangeMode.class,
				new NamingConventionTranslatorFactory<ChangeMode>(ChangeMode.class));
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterTranslator(ChangeMode.class);		
		chatSystem.unregister(RemotingPlugin.class);
	}

}
