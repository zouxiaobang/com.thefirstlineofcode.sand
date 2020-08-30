package com.firstlinecode.sand.emulators.lora.gateway.log;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.client.lora.IDualLoraChipsCommunicator;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.obm.ObmData;
import com.firstlinecode.sand.protocols.core.ModeDescriptor;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Map;

public class GatewayLogConsolePanel extends AbstractLogConsolePanel implements ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> {
	private static final long serialVersionUID = -75294176722668481L;
	
	private IDualLoraChipsCommunicator communicator;

	public GatewayLogConsolePanel(IDualLoraChipsCommunicator communicator, Map<String, ModeDescriptor> modes) {
		addProtocolToTypes(modes);
		this.communicator = communicator;
		communicator.addCommunicationListener(this);
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {
		communicator.removeCommunicationListener(this);
	}

	public void addProtocolToTypes(Map<String, ModeDescriptor> modes) {
		Collection<ModeDescriptor> modeDescriptors = modes.values();
		for (ModeDescriptor modeDescriptor : modeDescriptors) {
			Map<Protocol, Class<?>> supportedActions = modeDescriptor.getSupportedActions();
			for (Map.Entry<Protocol, Class<?>> entry : supportedActions.entrySet()) {
				Protocol protocol = entry.getKey();
				if (!protocolToTypes.containsKey(protocol)) {
					protocolToTypes.put(protocol, entry.getValue());
				}
			}

			Map<Protocol, Class<?>> supportedEvents = modeDescriptor.getSupportedEvents();
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
						"    B: %s",
				to, obmData.getProtocolObjectInfoString(), obmData.getHexString()));
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		ObmData obmData = new ObmData(parseProtocol(data), data);
		log(String.format("<--%s\n" +
						"    O: %s\n" +
						"    B: %s",
				from, obmData.getProtocolObjectInfoString(), obmData.getHexString()));
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
