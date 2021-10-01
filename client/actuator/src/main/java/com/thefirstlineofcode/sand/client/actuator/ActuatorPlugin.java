package com.thefirstlineofcode.sand.client.actuator;

import java.util.Properties;

import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.client.dmr.DmrPlugin;
import com.thefirstlineofcode.sand.client.things.autuator.IActuator;
import com.thefirstlineofcode.sand.protocols.actuator.Execute;
import com.thefirstlineofcode.sand.protocols.actuator.oxm.ExecutionParserFactory;

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
