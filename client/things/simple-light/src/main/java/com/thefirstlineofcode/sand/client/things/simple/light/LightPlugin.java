package com.thefirstlineofcode.sand.client.things.simple.light;

import java.util.Properties;

import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.client.remoting.RemotingPlugin;
import com.thefirstlineofcode.sand.protocols.things.simple.light.Flash;

public class LightPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.register(RemotingPlugin.class);
		chatSystem.registerTranslator(Flash.class,
				new NamingConventionTranslatorFactory<Flash>(Flash.class));
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterTranslator(Flash.class);
		chatSystem.unregister(RemotingPlugin.class);
	}

}
