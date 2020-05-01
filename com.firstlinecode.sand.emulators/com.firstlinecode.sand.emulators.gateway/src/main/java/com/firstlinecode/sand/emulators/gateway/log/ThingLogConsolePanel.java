package com.firstlinecode.sand.emulators.gateway.log;

import java.awt.event.WindowEvent;

import com.firstlinecode.chalk.IOrder;
import com.firstlinecode.sand.client.things.ThingsUtils;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.obm.ObmData;
import com.firstlinecode.sand.emulators.thing.IThingEmulator;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class ThingLogConsolePanel extends AbstractLogConsolePanel
		implements ICommunicationListener<LoraAddress, LoraAddress, ObmData>, IOrder {
	private static final long serialVersionUID = 506009089461387655L;

	@SuppressWarnings("unchecked")
	public ThingLogConsolePanel(IThingEmulator thing) {
		((ICommunicator<LoraAddress, LoraAddress, ObmData>)thing.getCommunicator()).addCommunicationListener(this);
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {
		// No-Op
		
	}

	@Override
	public void sent(LoraAddress to, ObmData data) {
		log(String.format("-->%s:\n" +
				"    O: %s\n" +
				"    B: %s",
				to, data.getProtocolObjectInfoString(), ThingsUtils.getHexString(data.getBinary())));
	}

	@Override
	public void received(LoraAddress from, ObmData data) {
		log(String.format("<--%s:\n" +
				"    O: %s\n" +
				"    B: %s",
				from, data.getProtocolObjectInfoString(), ThingsUtils.getHexString(data.getBinary())));
	}

	@Override
	public void occurred(CommunicationException e) {
		log(e);
	}
	
	@SuppressWarnings("unchecked")
	public void thingRemoved(IThingEmulator thing) {
		((ICommunicator<LoraAddress, LoraAddress, ObmData>)thing.getCommunicator()).removeCommunicationListener(this);
	}

	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
		log(String.format("D(%s)<=N, D(%s)=>N", oldAddress, newAddress));
	}

	@Override
	public int getOrder() {
		return IOrder.ORDER_NORMAL;
	}
}
