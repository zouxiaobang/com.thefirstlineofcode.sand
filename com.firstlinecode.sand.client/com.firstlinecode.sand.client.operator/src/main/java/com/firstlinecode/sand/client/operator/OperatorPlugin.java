package com.firstlinecode.sand.client.operator;

import java.util.Properties;

import com.firstlinecode.basalt.oxm.convention.NamingConventionParserFactory;
import com.firstlinecode.basalt.protocol.core.ProtocolChain;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.chalk.IChatSystem;
import com.firstlinecode.chalk.IPlugin;
import com.firstlinecode.sand.protocols.operator.AuthorizeDevice;
import com.firstlinecode.sand.protocols.operator.ConfirmConcentration;

public class OperatorPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		// TODO Auto-generated method stub
		chatSystem.registerParser(ProtocolChain.first(Iq.PROTOCOL).next(AuthorizeDevice.PROTOCOL),
				new NamingConventionParserFactory<AuthorizeDevice>(AuthorizeDevice.class));
		chatSystem.registerParser(ProtocolChain.first(Iq.PROTOCOL).next(ConfirmConcentration.PROTOCOL),
				new NamingConventionParserFactory<ConfirmConcentration>(ConfirmConcentration.class));
		
		chatSystem.registerApi(IOperator.class, Operator.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		// TODO Auto-generated method stub
		chatSystem.unregisterApi(IOperator.class);
		
		chatSystem.unregisterParser(ProtocolChain.first(Iq.PROTOCOL).next(ConfirmConcentration.PROTOCOL));
		chatSystem.unregisterParser(ProtocolChain.first(Iq.PROTOCOL).next(AuthorizeDevice.PROTOCOL));
	}

}
