package com.firstlinecode.sand.emulators.lora.gateway.log;

import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Map;

import com.firstlinecode.sand.client.lora.IDualLoraChipsCommunicator;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.obm.ObmData;
import com.firstlinecode.sand.emulators.things.ui.AbstractLogConsolePanel;
import com.firstlinecode.sand.protocols.core.ModelDescriptor;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

public class GatewayLogConsolePanel extends AbstractLogConsolePanel implements ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> {
	private static final long serialVersionUID = -75294176722668481L;
	
	private IDualLoraChipsCommunicator communicator;

	public GatewayLogConsolePanel(IDualLoraChipsCommunicator communicator, Map<String, ModelDescriptor> modes) {
		addProtocolToTypes(modes);
		this.communicator = communicator;
		communicator.addCommunicationListener(this);
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {
		communicator.removeCommunicationListener(this);
	}

	public void addProtocolToTypes(Map<String, ModelDescriptor> models) {
		Collection<ModelDescriptor> modelDescriptors = models.values();
		for (ModelDescriptor modelDescriptor : modelDescriptors) {
			Map<Protocol, Class<?>> supportedActions = modelDescriptor.getSupportedActions();
			for (Map.Entry<Protocol, Class<?>> entry : supportedActions.entrySet()) {
				Protocol protocol = entry.getKey();
				if (!protocolToTypes.containsKey(protocol)) {
					protocolToTypes.put(protocol, entry.getValue());
				}
			}

			Map<Protocol, Class<?>> supportedEvents = modelDescriptor.getSupportedEvents();
			for (Map.Entry<Protocol, Class<?>> entry : supportedEvents.entrySet()) {
				Protocol protocol = entry.getKey();
				if (!protocolToTypes.containsKey(protocol)) {
					protocolToTypes.put(protocol, entry.getValue());
				}
			}
		}
	}

	@Override
	public void sent(LoraAddress to, byte[] data) {
		ObmData obmData = new ObmData(parseProtocol(data), data);
		log(String.format("-->%s\n" +
						"    O: %s\n" +
						"    B(%d bytes): %s",
				to, obmData.getProtocolObjectInfoString(), obmData.getBinary().length, obmData.getHexString()));
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		ObmData obmData = new ObmData(parseProtocol(data), data);
		log(String.format("<--%s\n" +
						"    O: %s\n" +
						"    B(%d bytes): %s",
				from, obmData.getProtocolObjectInfoString(), obmData.getBinary().length, obmData.getHexString()));
	}

	@Override
	public void occurred(CommunicationException e) {
		log(e);
	}

	@Override
	public void addressChanged(DualLoraAddress newAddress, DualLoraAddress oldAddress) {
		log(String.format("G.M(%s)<=N, G.M(%s)=>N", oldAddress.getMasterChipAddress(), newAddress.getMasterChipAddress()));
		log(String.format("G.S(%s)<=N, G.S(%s)=>N", oldAddress.getSlaveChipAddress(), newAddress.getSlaveChipAddress()));
	}
}
