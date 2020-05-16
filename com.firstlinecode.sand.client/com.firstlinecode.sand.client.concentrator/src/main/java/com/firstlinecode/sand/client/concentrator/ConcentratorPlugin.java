package com.firstlinecode.sand.client.concentrator;

import java.util.Properties;

import com.firstlinecode.basalt.oxm.convention.NamingConventionParserFactory;
import com.firstlinecode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.firstlinecode.basalt.protocol.core.ProtocolChain;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.chalk.IChatSystem;
import com.firstlinecode.chalk.IPlugin;
import com.firstlinecode.sand.client.dmr.DmrPlugin;
import com.firstlinecode.sand.client.things.concentrator.IConcentrator;
import com.firstlinecode.sand.protocols.concentrator.CreateNode;
import com.firstlinecode.sand.protocols.concentrator.NodeCreated;

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
				ProtocolChain.first(Iq.PROTOCOL).next(NodeCreated.PROTOCOL),
				new NamingConventionParserFactory<NodeCreated>(
						NodeCreated.class
				)
		);
		
		chatSystem.registerApi(IConcentrator.class, Concentrator.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterApi(IConcentrator.class);
		
		chatSystem.unregisterParser(ProtocolChain.first(Iq.PROTOCOL).next(NodeCreated.PROTOCOL));
		chatSystem.unregisterTranslator(CreateNode.class);
		
		chatSystem.unregister(DmrPlugin.class);
	}

}
