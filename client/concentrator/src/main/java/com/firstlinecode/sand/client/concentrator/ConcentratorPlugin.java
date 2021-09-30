package com.firstlinecode.sand.client.concentrator;

import java.util.Properties;

import com.firstlinecode.chalk.core.IChatSystem;
import com.firstlinecode.chalk.core.IPlugin;
import com.firstlinecode.sand.client.dmr.DmrPlugin;
import com.firstlinecode.sand.client.things.concentrator.IConcentrator;
import com.firstlinecode.sand.protocols.concentrator.CreateNode;
import com.firstlinecode.sand.protocols.concentrator.NodeCreated;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;

public class ConcentratorPlugin implements IPlugin {
	@Override
	public void init(IChatSystem chatSystem, Properties properties) {	
		chatSystem.register(DmrPlugin.class);
		
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
		
		chatSystem.unregister(DmrPlugin.class);
	}

}
