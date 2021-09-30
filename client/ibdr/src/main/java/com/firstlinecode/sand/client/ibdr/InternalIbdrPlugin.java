package com.firstlinecode.sand.client.ibdr;

import java.util.Properties;

import com.firstlinecode.chalk.core.IChatSystem;
import com.firstlinecode.chalk.core.IPlugin;
import com.firstlinecode.sand.protocols.ibdr.DeviceRegister;
import com.firstlinecode.sand.protocols.ibdr.oxm.DeviceRegisterParserFactory;
import com.firstlinecode.sand.protocols.ibdr.oxm.DeviceRegisterTranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;

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
