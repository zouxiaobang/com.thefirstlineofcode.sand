package com.firstlinecode.sand.client.ibdr;

import java.util.Properties;

import com.firstlinecode.chalk.IChatSystem;
import com.firstlinecode.chalk.IPlugin;
import com.firstlinecode.chalk.core.stream.StandardStreamConfig;
import com.firstlinecode.chalk.core.stream.StreamConfig;

public class IbdrPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		StreamConfig streamConfig = chatSystem.getStreamConfig();
		
		if (!(streamConfig instanceof StandardStreamConfig)) {
			throw new IllegalArgumentException(String.format("IBR plugin needs a StandardStreamConfig."));
		}
		
		Properties apiProperties = new Properties();
		apiProperties.put("streamConfig", streamConfig);
		
		chatSystem.registerApi(IRegistration.class, Registration.class, apiProperties, false);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterApi(IRegistration.class);
	}

}
