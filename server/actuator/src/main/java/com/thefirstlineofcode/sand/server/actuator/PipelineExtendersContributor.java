package com.thefirstlineofcode.sand.server.actuator;

import java.util.Map.Entry;

import org.pf4j.Extension;

import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.IPipelineExtendersConfigurator;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.PipelineExtendersConfigurator;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.parsing.ProtocolParserFactory;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.routing.ProtocolTranslatorFactory;
import com.thefirstlineofcode.granite.framework.core.repository.IInitializable;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;
import com.thefirstlineofcode.sand.protocols.actuator.oxm.ExecutionParserFactory;
import com.thefirstlineofcode.sand.protocols.actuator.oxm.ExecutionTranslatorFactory;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

@Extension
public class PipelineExtendersContributor extends PipelineExtendersConfigurator implements IInitializable {
	@BeanDependency
	private IDeviceManager deviceManager;
	
	private IPipelineExtendersConfigurator configurator;
	
	@Override
	protected void configure(IPipelineExtendersConfigurator configurator) {
		ExecutionListener executionListener = new ExecutionListener();
		
		configurator.
			registerParserFactory(
					new ProtocolParserFactory<>(new IqProtocolChain(Execution.PROTOCOL), new ExecutionParserFactory())).
			registerSingletonXepProcessor(
					new IqProtocolChain(Execution.PROTOCOL), new ExecutionProcessor()).
			registerTranslatorFactory(
					new ProtocolTranslatorFactory<>(Execution.class, new ExecutionTranslatorFactory())).
			registerEventListener(
					ExecutionEvent.class, executionListener).
			registerIqResultProcessor(
					executionListener);
		
		this.configurator = configurator;
	}

	@Override
	public void init() {
		for (String model : deviceManager.getModels()) {
			ModelDescriptor modelDescriptor = deviceManager.getModelDescriptor(model);
			if (modelDescriptor.isActuator()) {
				for (Entry<Protocol, Class<?>> entry : modelDescriptor.getSupportedActions().entrySet()) {
					configurator.registerNamingConventionParser(
							new IqProtocolChain(Execution.PROTOCOL).next(entry.getKey()), entry.getValue());
					configurator.registerNamingConventionTranslator(entry.getValue());
				}
			}
		}
	}
}
