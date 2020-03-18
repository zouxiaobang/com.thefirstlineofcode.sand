package com.firstlinecode.sand.client.concentrator;

import java.util.Properties;

import com.firstlinecode.basalt.oxm.convention.NamingConventionParserFactory;
import com.firstlinecode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.firstlinecode.basalt.protocol.core.ProtocolChain;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.chalk.IChatSystem;
import com.firstlinecode.chalk.IPlugin;
import com.firstlinecode.sand.protocols.concentrator.NodeCreationConfirmation;
import com.firstlinecode.sand.protocols.concentrator.NodeCreationRequest;

public class ConcentratorPlugin implements IPlugin {
	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.registerParser(
				ProtocolChain.first(Iq.PROTOCOL).next(NodeCreationConfirmation.PROTOCOL),
				new NamingConventionParserFactory<NodeCreationConfirmation>(
						NodeCreationConfirmation.class
				)
		);
		
		chatSystem.registerTranslator(
				NodeCreationRequest.class,
				new NamingConventionTranslatorFactory<NodeCreationRequest>(
						NodeCreationRequest.class
				)
		);
		
		chatSystem.registerApi(IConcentrator.class, Concentrator.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterApi(IConcentrator.class);
		
		chatSystem.unregisterTranslator(NodeCreationRequest.class);
		chatSystem.unregisterParser(ProtocolChain.first(Iq.PROTOCOL).next(NodeCreationConfirmation.PROTOCOL));
	}

}
