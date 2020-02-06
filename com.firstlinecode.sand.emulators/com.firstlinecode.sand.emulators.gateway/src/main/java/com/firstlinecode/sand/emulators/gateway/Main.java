package com.firstlinecode.sand.emulators.gateway;

import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.emulators.blub.BlubEmulatorFactory;
import com.firstlinecode.sand.emulators.lora.DualLoraChipCommunicator;
import com.firstlinecode.sand.emulators.lora.LoraAddress;
import com.firstlinecode.sand.emulators.lora.LoraChipCreationParams;
import com.firstlinecode.sand.emulators.lora.LoraNetwork;

public class Main {
	public static void main(String[] args) {
		new Main().run();
	}
	
	private void run() {
		LoraNetwork network = new LoraNetwork();
		
		Gateway<?, ?, ?> gateway = createGateway(network, createGatewayCommunicator(network));
		gateway.registerThingEmulatorFactory(new BlubEmulatorFactory());
		gateway.setVisible(true);
	}
	
	private Gateway<LoraAddress, byte[], LoraChipCreationParams> createGateway(LoraNetwork network,
			ICommunicator<?, ?> communicator) {
		return new Gateway<LoraAddress, byte[], LoraChipCreationParams>(createGatewayCommunicator(network),
				new LoraCommunicatorFactory(network));
	}
	
	private ICommunicator<?, ?> createGatewayCommunicator(LoraNetwork network) {
		LoraAddress masterAddress = LoraAddress.randomLoraAddress(0);
		LoraAddress slaveAddress = new LoraAddress(masterAddress.getAddress(), 63);
		
		return new DualLoraChipCommunicator(network, masterAddress, slaveAddress);
	}
}
