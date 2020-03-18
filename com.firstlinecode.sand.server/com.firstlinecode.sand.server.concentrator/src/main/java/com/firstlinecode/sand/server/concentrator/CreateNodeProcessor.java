package com.firstlinecode.sand.server.concentrator;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.BadRequest;
import com.firstlinecode.basalt.protocol.core.stanza.error.ItemNotFound;
import com.firstlinecode.basalt.protocol.core.stanza.error.NotAcceptable;
import com.firstlinecode.granite.framework.processing.IProcessingContext;
import com.firstlinecode.granite.framework.processing.IXepProcessor;
import com.firstlinecode.sand.protocols.concentrator.NodeCreationRequest;
import com.firstlinecode.sand.protocols.core.CommunicationNet;
import com.firstlinecode.sand.protocols.lora.LoraAddress;
import com.firstlinecode.sand.server.framework.things.Device;
import com.firstlinecode.sand.server.framework.things.IDeviceManager;
import com.firstlinecode.sand.server.framework.things.concentrator.IConcentrator;
import com.firstlinecode.sand.server.framework.things.concentrator.IConcentratorFactory;
import com.firstlinecode.sand.server.framework.things.concentrator.Node;

public class CreateNodeProcessor implements IXepProcessor<Iq, NodeCreationRequest> {
	private IDeviceManager deviceManager;
	private IConcentratorFactory concentratorManager;
	
	@Override
	public void process(IProcessingContext context, Iq stanza, NodeCreationRequest xep) {
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
		node.setType(xep.getAddress().getCommunicationNet());
		node.setAddress(addressToString(xep.getAddress().getCommunicationNet(), xep.getAddress().getAddress()));
		
		IConcentrator concentrator = concentratorManager.getConcentrator(parent);
		concentrator.requestNodeCreation(node);
	}
	
	protected String addressToString(CommunicationNet communicationNet, Object address) {
		if (communicationNet != CommunicationNet.LORA)
			throw new ProtocolException(new BadRequest("Only LORA communication net is supported now."));
		
		LoraAddress loraAddress = (LoraAddress)address;
		
		return String.format("la$%s:%s", loraAddress.getAddress(), loraAddress.getFrequencyBand());
	}
	
	protected Object stringToAddress(CommunicationNet communicationNet, String address) {
		if (communicationNet != CommunicationNet.LORA) {
			throw new IllegalArgumentException("Only LORA communication net is supported now.");
		}
		
		if (!address.startsWith("la$")) {
			throw new IllegalArgumentException("Invalid LORA address.");
		}
		
		int conlonIndex = address.indexOf(':');
		if (conlonIndex == -1)
			throw new IllegalArgumentException("Invalid LORA address.");
		
		String addressPart = address.substring(3, conlonIndex);
		String frequencyPart = address.substring(conlonIndex + 1);
		
		LoraAddress loraAddress = new LoraAddress();		
		try {			
			loraAddress.setAddress(Long.parseLong(addressPart));
			loraAddress.setFrequencyBand(Integer.parseInt(frequencyPart));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid LORA address.", e);
		}
		
		return loraAddress;
	}

}
