package com.thefirstlineofcode.sand.server.actuator;

import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IXepProcessor;
import com.thefirstlineofcode.sand.protocols.actuator.Execute;

public class ExecutionProcessor implements IXepProcessor<Iq, Execute> {

	@Override
	public void process(IProcessingContext context, Iq iq, Execute xep) {
		// TODO Auto-generated method stub
		
	}

}
