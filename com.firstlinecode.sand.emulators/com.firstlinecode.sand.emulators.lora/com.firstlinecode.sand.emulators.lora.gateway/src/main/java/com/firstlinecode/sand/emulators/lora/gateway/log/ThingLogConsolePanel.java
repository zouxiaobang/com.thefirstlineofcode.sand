package com.firstlinecode.sand.emulators.lora.gateway.log;

import java.awt.event.WindowEvent;
import java.util.Map;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.obm.ObmData;
import com.firstlinecode.sand.emulators.lora.thing.AbstractLoraThingEmulator;
import com.firstlinecode.sand.protocols.core.ModeDescriptor;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class ThingLogConsolePanel extends AbstractLogConsolePanel
		implements ICommunicationListener<LoraAddress, LoraAddress, byte[]> {
	private static final long serialVersionUID = 506009089461387655L;

	public ThingLogConsolePanel(AbstractLoraThingEmulator thing, ModeDescriptor modeDescriptor) {
		((ICommunicator<LoraAddress, LoraAddress, byte[]>)thing.getCommunicator()).addCommunicationListener(this);
		addProtocolToTypes(modeDescriptor);
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
	
	public void thingRemoved(AbstractLoraThingEmulator thing) {
		((ICommunicator<LoraAddress, LoraAddress, byte[]>)thing.getCommunicator()).removeCommunicationListener(this);
	}

	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
		log(String.format("D(%s)<=N, D(%s)=>N", oldAddress, newAddress));
	}

	private void addProtocolToTypes(ModeDescriptor modeDescriptor) {
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
