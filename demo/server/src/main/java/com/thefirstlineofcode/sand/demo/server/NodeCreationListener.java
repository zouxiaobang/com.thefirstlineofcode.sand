package com.thefirstlineofcode.sand.demo.server;

import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventListener;
import com.thefirstlineofcode.sand.server.concentrator.NodeCreationEvent;

public class NodeCreationListener extends AbstractDeviceActivationEventListener
		implements IEventListener<NodeCreationEvent> {

	@Override
	public void process(IEventContext context, NodeCreationEvent event) {
		process(context, event.getNodeDeviceId(), event.getConfirmer());
	}
}
