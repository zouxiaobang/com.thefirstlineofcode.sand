package com.firstlinecode.sand.client.operator;

import java.util.Properties;

import com.firstlinecode.chalk.core.IChatSystem;
import com.firstlinecode.chalk.core.IPlugin;
import com.firstlinecode.sand.protocols.operator.AuthorizeDevice;
import com.firstlinecode.sand.protocols.operator.ConfirmConcentration;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;

public class OperatorPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		// TODO Auto-generated method stub
		chatSystem.registerTranslator(AuthorizeDevice.class,
				new NamingConventionTranslatorFactory<AuthorizeDevice>(AuthorizeDevice.class));
		chatSystem.registerTranslator(ConfirmConcentration.class,
				new NamingConventionTranslatorFactory<ConfirmConcentration>(ConfirmConcentration.class));
		
		chatSystem.registerApi(IOperator.class, Operator.class);
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		// TODO Auto-generated method stub
		chatSystem.unregisterApi(IOperator.class);
		
		chatSystem.unregisterTranslator(ConfirmConcentration.class);
		chatSystem.unregisterTranslator(AuthorizeDevice.class);
	}

}
