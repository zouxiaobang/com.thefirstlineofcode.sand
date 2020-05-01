package com.firstlinecode.sand.emulators.gateway.log;

import java.awt.event.WindowEvent;

import com.firstlinecode.chalk.IOrder;
import com.firstlinecode.sand.client.lora.IDualLoraChipsCommunicator;
import com.firstlinecode.sand.client.things.ThingsUtils;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.obm.ObmData;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class GatewayLogConsolePanel extends AbstractLogConsolePanel {
	private static final long serialVersionUID = -75294176722668481L;
	
	private IDualLoraChipsCommunicator communicator;
	private ICommunicationListener<DualLoraAddress, LoraAddress, ObmData> communicationListener;

	public GatewayLogConsolePanel(IDualLoraChipsCommunicator communicator) {
		this.communicator = communicator;
		communicationListener = new DualLoraChipCommunicationListener();
		communicator.addCommunicationListener(communicationListener);
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {
		communicator.removeCommunicationListener(communicationListener);
	}
	
	private class DualLoraChipCommunicationListener implements ICommunicationListener<DualLoraAddress, LoraAddress, ObmData>, IOrder {
		@Override
		public void sent(LoraAddress to, ObmData data) {
			log(String.format("-->%s: \n" +
					"    O: %s\n" +
					"    B: %s",
					to, data.getProtocolObjectInfoString(), ThingsUtils.getHexString(data.getBinary())));
		}

		@Override
		public void received(LoraAddress from, ObmData data) {
			log(String.format("<--%s: \n" +
					"    O: %s\n" +
					"    B: %s",
					from, data.getProtocolObjectInfoString(), ThingsUtils.getHexString(data.getBinary())));
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

		@Override
		public int getOrder() {
			return IOrder.ORDER_NORMAL;
		}
	}

}
