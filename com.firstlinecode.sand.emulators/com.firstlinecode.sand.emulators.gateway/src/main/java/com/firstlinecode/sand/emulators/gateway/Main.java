package com.firstlinecode.sand.emulators.gateway;

import com.firstlinecode.sand.client.lora.IDualLoraChipsCommunicator;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.emulators.light.LightEmulatorFactory;
import com.firstlinecode.sand.emulators.lora.DualLoraChipsCommunicator;
import com.firstlinecode.sand.emulators.lora.LoraChip;
import com.firstlinecode.sand.emulators.lora.LoraChipCreationParams;
import com.firstlinecode.sand.emulators.lora.LoraNetwork;
import com.firstlinecode.sand.protocols.lora.DualLoraAddress;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class Main {
	public static void main(String[] args) {
		new Main().run();
	}
	
	private void run() {
		configureLogDir();
		
		Gateway<?, ?> gateway = createGateway(new LoraNetwork());
		gateway.registerThingEmulatorFactory(new LightEmulatorFactory());
		
		gateway.setVisible(true);
	}
	
	private void configureLogDir() {
		System.setProperty("sand.log.dir", System.getProperty("user.home") + "/.com.firstlinecode.sand/logs");
	}
	
	private Gateway<ICommunicator<DualLoraAddress, LoraAddress, byte[]>, LoraChipCreationParams> createGateway(LoraNetwork network) {
		IDualLoraChipsCommunicator gatewayCommunicator = DualLoraChipsCommunicator.createInstance(
				network, DualLoraAddress.randomDualLoraAddress(0), new LoraChipCreationParams(
						LoraChip.Type.HIGH_POWER));
		return new Gateway<ICommunicator<DualLoraAddress, LoraAddress, byte[]>, LoraChipCreationParams>(network, gatewayCommunicator);
	}
	
}
