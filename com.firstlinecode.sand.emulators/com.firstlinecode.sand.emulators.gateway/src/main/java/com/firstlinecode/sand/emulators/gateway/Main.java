package com.firstlinecode.sand.emulators.gateway;

import com.firstlinecode.sand.client.lora.DualLoraAddress;
import com.firstlinecode.sand.client.lora.IDualLoraChipCommunicator;
import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.emulators.blub.BlubEmulatorFactory;
import com.firstlinecode.sand.emulators.lora.DualLoraChipCommunicator;
import com.firstlinecode.sand.emulators.lora.LoraChip;
import com.firstlinecode.sand.emulators.lora.LoraChipCreationParams;
import com.firstlinecode.sand.emulators.lora.LoraNetwork;

public class Main {
	public static void main(String[] args) {
		new Main().run();
	}
	
	private void run() {
		LoraNetwork network = new LoraNetwork();
		
		Gateway<?, ?, ?, ?> gateway = createGateway(network);
		gateway.registerThingEmulatorFactory(new BlubEmulatorFactory());
		gateway.setVisible(true);
	}
	
	private Gateway<LoraAddress, byte[], ICommunicator<LoraAddress, byte[]>,
			LoraChipCreationParams> createGateway(LoraNetwork network) {
		IDualLoraChipCommunicator gatewayCommunicator = DualLoraChipCommunicator.createInstance(
				network, DualLoraAddress.randomDualLoraAddress(0), new LoraChipCreationParams(
						LoraChip.Type.HIGH_POWER));
		return new Gateway<LoraAddress, byte[], ICommunicator<LoraAddress, byte[]>,
				LoraChipCreationParams>(network, gatewayCommunicator);
	}
	
}
