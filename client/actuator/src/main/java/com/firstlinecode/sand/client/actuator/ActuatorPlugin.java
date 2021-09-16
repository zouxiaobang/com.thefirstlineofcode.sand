package com.firstlinecode.sand.client.actuator;

import java.util.Properties;

import com.firstlinecode.basalt.protocol.core.IqProtocolChain;
import com.firstlinecode.chalk.core.IChatSystem;
import com.firstlinecode.chalk.core.IPlugin;
import com.firstlinecode.sand.client.dmr.DmrPlugin;
import com.firstlinecode.sand.client.things.autuator.IActuator;
import com.firstlinecode.sand.protocols.actuator.Execute;
import com.firstlinecode.sand.protocols.actuator.oxm.ExecutionParserFactory;

public class ActuatorPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.register(DmrPlugin.class);
		chatSystem.registerParser(
				new IqProtocolChain(Execute.PROTOCOL),
				new ExecutionParserFactory());
		chatSystem.registerApi(IActuator.class, Actuator.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterApi(IActuator.class);
		chatSystem.unregisterParser(new IqProtocolChain(Execute.PROTOCOL));
		chatSystem.unregister(DmrPlugin.class);
	}

}
