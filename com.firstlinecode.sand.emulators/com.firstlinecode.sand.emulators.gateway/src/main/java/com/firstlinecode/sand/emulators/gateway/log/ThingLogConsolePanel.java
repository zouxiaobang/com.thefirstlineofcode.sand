package com.firstlinecode.sand.emulators.gateway.log;

import java.awt.event.WindowEvent;

import com.firstlinecode.sand.client.things.ThingsUtils;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.emulators.thing.IThingEmulator;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class ThingLogConsolePanel extends AbstractLogConsolePanel implements ICommunicationListener<LoraAddress, LoraAddress, byte[]> {
	private static final long serialVersionUID = 506009089461387655L;

	@SuppressWarnings("unchecked")
	public ThingLogConsolePanel(IThingEmulator thing) {
		((ICommunicator<LoraAddress, LoraAddress, byte[]>)thing.getCommunicator()).addCommunicationListener(this);
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {
		// No-Op
		
	}

	@Override
	public void sent(LoraAddress to, byte[] data) {
		log(String.format("-->%s: %s", to, ThingsUtils.getHexString(data)));
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		log(String.format("<--%s: %s", from, ThingsUtils.getHexString(data)));
	}

	@Override
	public void occurred(CommunicationException e) {
		log(e);
	}
	
	@SuppressWarnings("unchecked")
	public void thingRemoved(IThingEmulator thing) {
		((ICommunicator<LoraAddress, LoraAddress, byte[]>)thing.getCommunicator()).removeCommunicationListener(this);
	}

	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
		log(String.format("D(%s)<=N, D(%s)=>N", oldAddress, newAddress));
	}

}
