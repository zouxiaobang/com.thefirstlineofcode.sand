package com.firstlinecode.sand.emulators.gateway.log;

import java.awt.event.WindowEvent;

import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.ThingsUtils;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.emulators.thing.IThingEmulator;

public class ThingLogConsolePanel extends AbstractLogConsolePanel implements ICommunicationListener<LoraAddress, byte[]> {
	private static final long serialVersionUID = 506009089461387655L;

	@SuppressWarnings("unchecked")
	public ThingLogConsolePanel(IThingEmulator thing) {
		((ICommunicator<LoraAddress, byte[]>)thing.getCommunicator()).addCommunicationListener(this);
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {
		// No-Op
		
	}

	@Override
	public void sent(LoraAddress to, byte[] data) {
		log(String.format("-->%s", to, ThingsUtils.getHexString(data)));
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		
	}

	@Override
	public void occurred(CommunicationException e) {
		log(e);
	}
	
	@SuppressWarnings("unchecked")
	public void thingRemoved(IThingEmulator thing) {
		((ICommunicator<LoraAddress, byte[]>)thing.getCommunicator()).removeCommunicationListener(this);
	}

}
