package com.firstlinecode.sand.server.concentrator;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.Conflict;
import com.firstlinecode.basalt.protocol.core.stanza.error.ItemNotFound;
import com.firstlinecode.basalt.protocol.core.stanza.error.NotAcceptable;
import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.granite.framework.processing.IProcessingContext;
import com.firstlinecode.granite.framework.processing.IXepProcessor;
import com.firstlinecode.sand.protocols.concentrator.CreateNode;
import com.firstlinecode.sand.protocols.core.CommunicationNet;
import com.firstlinecode.sand.server.framework.things.Device;
import com.firstlinecode.sand.server.framework.things.IDeviceManager;
import com.firstlinecode.sand.server.framework.things.concentrator.IConcentrator;
import com.firstlinecode.sand.server.framework.things.concentrator.IConcentratorFactory;
import com.firstlinecode.sand.server.framework.things.concentrator.Node;

public class CreateNodeProcessor implements IXepProcessor<Iq, CreateNode> {
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	@Dependency("concentrator.factory")
	private IConcentratorFactory concentratorFactory;
	
	@Override
	public void process(IProcessingContext context, Iq stanza, CreateNode xep) {
		Device parent = deviceManager.getByDeviceName(context.getJid().getName());
		if (parent == null)
			throw new ProtocolException(new ItemNotFound(String.format("Device which's device name is %s not be found.",
					context.getJid().getName())));
		
		if (!deviceManager.isConcentrator(parent.getMode()))
			throw new ProtocolException(new NotAcceptable("Device which's device name is %s isn't a concentrator.",
					context.getJid().getName()));
		
		Node node = new Node();
		node.setParent(parent.getDeviceId());
		node.setDeviceId(xep.getDeviceId());
		node.setLanId(xep.getLanId());
		node.setType(CommunicationNet.valueOf(xep.getCommunicationNet()));
		node.setAddress(xep.getAddress().toString());
		
		IConcentrator concentrator = concentratorFactory.getConcentrator(parent);
		if (concentrator == null)
			throw new RuntimeException("Can't fetch the concentrator.");
		
		if (concentrator.containsNode(node.getDeviceId())) {
			throw new ProtocolException(new Conflict());
		}
		
		concentrator.createNode(node);
		
		context.write(new Iq(Iq.Type.RESULT, stanza.getId()));
	}

}
