package com.firstlinecode.sand.emulators.gateway.log;

import java.awt.event.WindowEvent;

import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.ThingsUtils;
import com.firstlinecode.sand.client.things.commuication.ICommunicationNetwork;
import com.firstlinecode.sand.emulators.lora.ILoraNetworkListener;

public class CommunicationNetworkLogConsolePanel extends AbstractLogConsolePanel implements ILoraNetworkListener {
	private static final long serialVersionUID = 4598974878913796627L;
	
	private ICommunicationNetwork<LoraAddress, byte[], ?> network;
	
	public CommunicationNetworkLogConsolePanel(ICommunicationNetwork<LoraAddress, byte[], ?> network) {
		this.network = network;
	}
	
	@Override
	protected void doWindowClosing(WindowEvent e) {
		network.removeListener(this);
	}

	@Override
	public void sent(LoraAddress from, LoraAddress to, byte[] data) {
		log(String.format("D(%s)-->N: ", from) + ThingsUtils.getHexString(data));
	}

	@Override
	public void received(LoraAddress from, LoraAddress to, byte[] data) {
		log(String.format("N-->D: (%s)", from, to) + ThingsUtils.getHexString(data));
		
	}

	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
		log(String.format("D(%s)<=N, D(%s)=>N", oldAddress, newAddress));
	}

	@Override
	public void collided(LoraAddress from, LoraAddress to, byte[] data) {
		log(String.format("?* D(%s)-->D(%s)", from, to));
	}

	@Override
	public void lost(LoraAddress from, LoraAddress to, byte[] data) {
		log(String.format("?& N-->D(%s): ", from, to) + ThingsUtils.getHexString(data));
	}

}
