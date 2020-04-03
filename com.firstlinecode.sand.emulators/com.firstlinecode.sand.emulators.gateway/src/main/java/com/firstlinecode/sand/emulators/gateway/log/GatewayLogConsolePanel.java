package com.firstlinecode.sand.emulators.gateway.log;

import java.awt.event.WindowEvent;

import com.firstlinecode.sand.client.lora.IDualLoraChipsCommunicator;
import com.firstlinecode.sand.client.things.ThingsUtils;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class GatewayLogConsolePanel extends AbstractLogConsolePanel {
	private static final long serialVersionUID = -75294176722668481L;
	
	private IDualLoraChipsCommunicator communicator;
	private ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> communicationListener;
	
	public GatewayLogConsolePanel() {
		communicationListener = new DualLoraChipCommunicationListener();
	}

	public GatewayLogConsolePanel(IDualLoraChipsCommunicator communicator) {
		this.communicator = communicator;
		communicator.addCommunicationListener(communicationListener);
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {
		communicator.removeCommunicationListener(communicationListener);
	}
	
	private class DualLoraChipCommunicationListener implements ICommunicationListener<DualLoraAddress, LoraAddress, byte[]> {
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

		@Override
		public void addressChanged(DualLoraAddress newAddress, DualLoraAddress oldAddress) {
			log(String.format("G.M(%s)<=N, G.M(%s)=>N", "M" , oldAddress.getMasterChipAddress(), newAddress.getMasterChipAddress()));
			log(String.format("G.%s(%s)<=N, G.%s(%s)=>N", "S", oldAddress.getSlaveChipAddress(), newAddress.getSlaveChipAddress()));
		}
		
	}

}
