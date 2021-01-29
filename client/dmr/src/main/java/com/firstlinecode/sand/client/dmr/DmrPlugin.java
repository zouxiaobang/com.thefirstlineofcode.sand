package com.firstlinecode.sand.client.dmr;

import java.util.Properties;

import com.firstlinecode.chalk.core.IChatSystem;
import com.firstlinecode.chalk.core.IPlugin;

public class DmrPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.registerApi(IModelRegistrar.class, ModelRegistrar.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterApi(IModelRegistrar.class);
	}

}
