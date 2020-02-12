package com.firstlinecode.sand.emulators.gateway.log;

import java.awt.event.WindowEvent;

import com.firstlinecode.sand.client.lora.IDualLoraChipCommunicator;
import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.ThingsUtils;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;

public class GatewayLogConsolePanel extends AbstractLogConsolePanel implements ICommunicationListener<LoraAddress, byte[]> {
	private static final long serialVersionUID = -75294176722668481L;
	
	private IDualLoraChipCommunicator communicator;

	public GatewayLogConsolePanel(IDualLoraChipCommunicator communicator) {
		this.communicator = communicator;
		communicator.getMasterChip().addListener(this);
		communicator.getSlaveChip().addListener(this);
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {
		communicator.getMasterChip().removeListener(this);
		communicator.getSlaveChip().removeListener(this);
	}
	
	public void sent(LoraAddress to, byte[] data) {
		log(String.format("G.M(%s)-->%s:", communicator.getMasterChip().getAddress(), to, ThingsUtils.getHexString(data)));
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		log(String.format("G.M(%s)<--%s: %s", communicator.getMasterChip().getAddress(), from, ThingsUtils.getHexString(data)));
	}

	@Override
	public void occurred(CommunicationException e) {
		log(e);
	}

}
