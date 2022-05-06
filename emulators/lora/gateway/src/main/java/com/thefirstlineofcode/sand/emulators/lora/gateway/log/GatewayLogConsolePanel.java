package com.thefirstlineofcode.sand.emulators.lora.gateway.log;

import java.awt.event.WindowEvent;

import com.thefirstlineofcode.sand.client.lora.IDualLoraChipsCommunicator;
import com.thefirstlineofcode.sand.client.things.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicationListener;
import com.thefirstlineofcode.sand.client.things.obm.IObmFactory;
import com.thefirstlineofcode.sand.client.things.obm.ObmData;
import com.thefirstlineofcode.sand.emulators.commons.ui.AbstractLogConsolePanel;
import com.thefirstlineofcode.sand.protocols.lora.DualLoraAddress;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public class GatewayLogConsolePanel extends AbstractLogConsolePanel implements ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> {
	private static final long serialVersionUID = -75294176722668481L;
	
	private IDualLoraChipsCommunicator communicator;

	public GatewayLogConsolePanel(IDualLoraChipsCommunicator communicator, IObmFactory obmFactory) {
		super(obmFactory);
		
		this.communicator = communicator;
		communicator.addCommunicationListener(this);
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {
		communicator.removeCommunicationListener(this);
	}

	@Override
	public void sent(LoraAddress to, byte[] data) {
		ObmData obmData = new ObmData(toObject(data), data);
		log(String.format("-->%s\n" +
						"    O: %s\n" +
						"    B(%d bytes): %s",
				to, obmData.getProtocolObjectInfoString(), obmData.getBinary().length, obmData.getHexString()));
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		ObmData obmData = new ObmData(toObject(data), data);
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
