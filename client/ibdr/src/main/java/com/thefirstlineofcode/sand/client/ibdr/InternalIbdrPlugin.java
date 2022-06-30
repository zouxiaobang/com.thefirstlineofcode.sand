package com.thefirstlineofcode.sand.client.ibdr;

import java.util.Properties;

import com.thefirstlineofcode.basalt.xmpp.core.IqProtocolChain;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.protocols.ibdr.DeviceRegister;
import com.thefirstlineofcode.sand.protocols.ibdr.oxm.DeviceRegisterParserFactory;
import com.thefirstlineofcode.sand.protocols.ibdr.oxm.DeviceRegisterTranslatorFactory;

public class InternalIbdrPlugin implements IPlugin {
	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.registerParser(
				new IqProtocolChain(DeviceRegister.PROTOCOL),
				new DeviceRegisterParserFactory()
		);
		
		chatSystem.registerTranslator(
				DeviceRegister.class,
				new DeviceRegisterTranslatorFactory()
		);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterTranslator(DeviceRegister.class);
		
		chatSystem.unregisterParser(
				new IqProtocolChain(DeviceRegister.PROTOCOL)
		);
	}
}
