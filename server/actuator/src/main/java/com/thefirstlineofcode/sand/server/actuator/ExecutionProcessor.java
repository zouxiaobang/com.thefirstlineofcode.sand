package com.thefirstlineofcode.sand.server.actuator;

import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IXepProcessor;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;

public class ExecutionProcessor implements IXepProcessor<Iq, Execution> {

	@Override
	public void process(IProcessingContext context, Iq iq, Execution xep) {
		// TODO Auto-generated method stub
		
	}

}
