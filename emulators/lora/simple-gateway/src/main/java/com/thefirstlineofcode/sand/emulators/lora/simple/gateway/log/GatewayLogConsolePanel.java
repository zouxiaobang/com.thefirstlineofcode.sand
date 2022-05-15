package com.thefirstlineofcode.sand.emulators.lora.simple.gateway.log;

import java.awt.event.WindowEvent;

import com.thefirstlineofcode.sand.client.core.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.core.commuication.ICommunicationListener;
import com.thefirstlineofcode.sand.client.core.obx.IObxFactory;
import com.thefirstlineofcode.sand.client.core.obx.ObxData;
import com.thefirstlineofcode.sand.client.lora.IDualLoraChipsCommunicator;
import com.thefirstlineofcode.sand.emulators.commons.ui.AbstractLogConsolePanel;
import com.thefirstlineofcode.sand.protocols.lora.DualLoraAddress;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public class GatewayLogConsolePanel extends AbstractLogConsolePanel implements ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> {
	private static final long serialVersionUID = -75294176722668481L;
	
	private IDualLoraChipsCommunicator communicator;

	public GatewayLogConsolePanel(IDualLoraChipsCommunicator communicator, IObxFactory obmFactory) {
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
		Object obj = toObject(data);
		ObxData obmData = new ObxData(obj, toXml(data), data);
		log(String.format("-->%s\n" +
						"    O: %s\n" +
						"    X(%d bytes): %s\n" +
						"    B(%d bytes): %s",
				to, obmData.getProtocolObjectInfoString(), obmData.getBinary().length,
				obmData.getXml(), obmData.getXml().length(), obmData.getHexString()));
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		ObxData obmData = new ObxData(toObject(data), toXml(data), data);
		log(String.format("<--%s\n" +
						"    O: %s\n" +
						"    X(%d bytes): %s\n" +
						"    B(%d bytes): %s",
				from, obmData.getProtocolObjectInfoString(), obmData.getBinary().length,
				obmData.getXml(), obmData.getXml().length(), obmData.getHexString()));
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
