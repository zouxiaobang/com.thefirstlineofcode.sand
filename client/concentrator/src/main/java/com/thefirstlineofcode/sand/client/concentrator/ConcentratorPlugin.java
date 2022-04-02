package com.thefirstlineofcode.sand.client.concentrator;

import java.util.Properties;

import com.thefirstlinelinecode.sand.protocols.concentrator.CreateNode;
import com.thefirstlinelinecode.sand.protocols.concentrator.NodeCreated;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.client.things.concentrator.IConcentrator;

public class ConcentratorPlugin implements IPlugin {
	@Override
	public void init(IChatSystem chatSystem, Properties properties) {	
		chatSystem.registerTranslator(
				CreateNode.class,
				new NamingConventionTranslatorFactory<CreateNode>(
						CreateNode.class
				)
		);
		chatSystem.registerParser(
				new IqProtocolChain(NodeCreated.PROTOCOL),
				new NamingConventionParserFactory<NodeCreated>(
						NodeCreated.class
				)
		);
		
		chatSystem.registerApi(IConcentrator.class, Concentrator.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterApi(IConcentrator.class);
		
		chatSystem.unregisterParser(new IqProtocolChain(NodeCreated.PROTOCOL));
		chatSystem.unregisterTranslator(CreateNode.class);
	}

}
