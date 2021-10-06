package com.thefirstlineofcode.sand.emulators.lora.gateway.log;

import java.awt.event.WindowEvent;
import java.util.Map;

import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.sand.client.things.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicationListener;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicator;
import com.thefirstlineofcode.sand.client.things.obm.ObmData;
import com.thefirstlineofcode.sand.emulators.lora.things.AbstractLoraThingEmulator;
import com.thefirstlineofcode.sand.emulators.things.ui.AbstractLogConsolePanel;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public class ThingLogConsolePanel extends AbstractLogConsolePanel
		implements ICommunicationListener<LoraAddress, LoraAddress, byte[]> {
	private static final long serialVersionUID = 506009089461387655L;

	public ThingLogConsolePanel(AbstractLoraThingEmulator thing, ModelDescriptor modelDescriptor) {
		((ICommunicator<LoraAddress, LoraAddress, byte[]>)thing.getCommunicator()).addCommunicationListener(this);
		addProtocolToTypes(modelDescriptor);
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {
		// No-Op
		
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
	
	public void thingRemoved(AbstractLoraThingEmulator thing) {
		((ICommunicator<LoraAddress, LoraAddress, byte[]>)thing.getCommunicator()).removeCommunicationListener(this);
	}

	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
		log(String.format("D(%s)<=N, D(%s)=>N", oldAddress, newAddress));
	}

	private void addProtocolToTypes(ModelDescriptor modelDescriptor) {
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