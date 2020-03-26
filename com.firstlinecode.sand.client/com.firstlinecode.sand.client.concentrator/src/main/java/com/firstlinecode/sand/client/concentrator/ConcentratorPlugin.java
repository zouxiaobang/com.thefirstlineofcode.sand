package com.firstlinecode.sand.client.concentrator;

import java.util.Properties;

import com.firstlinecode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.firstlinecode.chalk.IChatSystem;
import com.firstlinecode.chalk.IPlugin;
import com.firstlinecode.sand.protocols.concentrator.CreateNode;

public class ConcentratorPlugin implements IPlugin {
	@Override
	public void init(IChatSystem chatSystem, Properties properties) {		
		chatSystem.registerTranslator(
				CreateNode.class,
				new NamingConventionTranslatorFactory<CreateNode>(
						CreateNode.class
				)
		);
		
		chatSystem.registerApi(IConcentrator.class, Concentrator.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterApi(IConcentrator.class);
		
		chatSystem.unregisterTranslator(CreateNode.class);
	}

}
