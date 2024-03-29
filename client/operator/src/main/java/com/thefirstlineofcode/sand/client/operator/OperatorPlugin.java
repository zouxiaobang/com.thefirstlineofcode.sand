package com.thefirstlineofcode.sand.client.operator;

import java.util.Properties;

import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.protocols.operator.AuthorizeDevice;
import com.thefirstlineofcode.sand.protocols.operator.ConfirmConcentration;

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
