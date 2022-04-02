package com.thefirstlineofcode.sand.client.actuator;

import java.util.Properties;

import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.client.things.actuator.IActuator;
import com.thefirstlineofcode.sand.protocols.actuator.Execute;
import com.thefirstlineofcode.sand.protocols.actuator.oxm.ExecuteParserFactory;

public class ActuatorPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.registerParser(
				new IqProtocolChain(Execute.PROTOCOL),
				new ExecuteParserFactory());
		chatSystem.registerApi(IActuator.class, Actuator.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterApi(IActuator.class);
		chatSystem.unregisterParser(new IqProtocolChain(Execute.PROTOCOL));
	}

}
