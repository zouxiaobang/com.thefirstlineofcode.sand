package com.firstlinecode.sand.server.actuator;

import com.firstlinecode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.IXepProcessor;
import com.firstlinecode.sand.protocols.actuator.Execute;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;

public class ExecutionProcessor implements IXepProcessor<Iq, Execute> {

	@Override
	public void process(IProcessingContext context, Iq iq, Execute xep) {
		// TODO Auto-generated method stub
		
	}

}
