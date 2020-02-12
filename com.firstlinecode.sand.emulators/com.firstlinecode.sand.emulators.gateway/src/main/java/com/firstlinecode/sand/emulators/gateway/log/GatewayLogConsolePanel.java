package com.firstlinecode.sand.emulators.gateway.log;

import java.awt.event.WindowEvent;

import com.firstlinecode.sand.client.lora.IDualLoraChipCommunicator;
import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.ThingsUtils;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;

public class GatewayLogConsolePanel extends AbstractLogConsolePanel {
	private static final long serialVersionUID = -75294176722668481L;
	
	private IDualLoraChipCommunicator communicator;

	public GatewayLogConsolePanel(IDualLoraChipCommunicator communicator) {
		this.communicator = communicator;
		communicator.getMasterChip().addListener(new DualLoraChipCommunicatorListener(true));
		communicator.getSlaveChip().addListener(new DualLoraChipCommunicatorListener(false));
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {
		communicator.getMasterChip().removeListener(new DualLoraChipCommunicatorListener(true));
		communicator.getSlaveChip().removeListener(new DualLoraChipCommunicatorListener(false));
	}
	
	private class DualLoraChipCommunicatorListener implements ICommunicationListener<LoraAddress, byte[]> {
		private boolean master;
		
		public DualLoraChipCommunicatorListener(boolean master) {
			this.master = master;
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

		@Override
		public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
			log(String.format("G.%s(%s)<=N, G.%s(%s)=>N", master ? "M" : "S", oldAddress, master ? "M" : "S", newAddress));
		}
		
	}

}
