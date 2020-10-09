package com.firstlinecode.sand.emulators.lora.gateway.log;

import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Map;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.client.things.commuication.ICommunicationNetwork;
import com.firstlinecode.sand.client.things.obm.ObmData;
import com.firstlinecode.sand.emulators.lora.network.ILoraNetworkListener;
import com.firstlinecode.sand.emulators.things.ui.AbstractLogConsolePanel;
import com.firstlinecode.sand.protocols.core.ModelDescriptor;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class CommunicationNetworkLogConsolePanel extends AbstractLogConsolePanel implements ILoraNetworkListener {
	private static final long serialVersionUID = 4598974878913796627L;
	
	private ICommunicationNetwork<LoraAddress, byte[], ?> network;
	
	public CommunicationNetworkLogConsolePanel(ICommunicationNetwork<LoraAddress, byte[], ?> network, Map<String, ModelDescriptor> models) {
		this.network = network;
		addProtocolToTypes(models);
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
	protected void doWindowClosing(WindowEvent e) {
		network.removeListener(this);
	}

	@Override
	public void sent(LoraAddress from, LoraAddress to, byte[] data) {
		ObmData obmData = new ObmData(parseProtocol(data), data);
		log(String.format("D(%s)-->N-->D(%s): \n" +
						"    O: %s\n" +
						"    B: %s",
				from, to, obmData.getProtocolObjectInfoString(), obmData.getHexString()));
	}

	@Override
	public void received(LoraAddress from, LoraAddress to, byte[] data) {
		ObmData obmData = new ObmData(parseProtocol(data), data);
		log(String.format("D(%s)<--N<--D(%s)\n" +
						"    O: %s\n" +
						"    B: %s",
				to, from, obmData.getProtocolObjectInfoString(), obmData.getHexString()));
		
	}

	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
		log(String.format("D(%s)<=N, D(%s)=>N", oldAddress, newAddress));
	}

	@Override
	public void collided(LoraAddress from, LoraAddress to, byte[] data) {
		ObmData obmData = new ObmData(parseProtocol(data), data);
		log(String.format("?* D(%s)-->N-->D(%s)\n" +
						"    O: %s\n" +
						"    B: %s",
				from, to, obmData.getProtocolObjectInfoString(), obmData.getHexString()));
	}

	@Override
	public void lost(LoraAddress from, LoraAddress to, byte[] data) {
		ObmData obmData = new ObmData(parseProtocol(data), data);
		log(String.format("?& D(%s)->N-->D(%s)\n" +
						"    O: %s\n" +
						"    B: %s",
				from, to, obmData.getProtocolObjectInfoString(), obmData.getHexString()));
	}

}
