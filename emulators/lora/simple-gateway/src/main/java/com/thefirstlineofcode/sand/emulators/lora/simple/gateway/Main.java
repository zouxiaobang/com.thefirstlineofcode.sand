package com.thefirstlineofcode.sand.emulators.lora.simple.gateway;

import com.thefirstlineofcode.chalk.logger.LogConfigurator;
import com.thefirstlineofcode.chalk.logger.LogConfigurator.LogLevel;
import com.thefirstlineofcode.sand.client.core.commuication.ICommunicator;
import com.thefirstlineofcode.sand.client.lora.IDualLoraChipsCommunicator;
import com.thefirstlineofcode.sand.emulators.lora.network.DualLoraChipsCommunicator;
import com.thefirstlineofcode.sand.emulators.lora.network.LoraChip;
import com.thefirstlineofcode.sand.emulators.lora.network.LoraChipCreationParams;
import com.thefirstlineofcode.sand.emulators.lora.network.LoraNetwork;
import com.thefirstlineofcode.sand.emulators.lora.simple.light.LightEmulatorFactory;
import com.thefirstlineofcode.sand.protocols.lora.DualLoraAddress;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public class Main {
	private static final String APP_NAME_SAND_LORA_GATEWAY = "sand-emulators-lora-simple-gateway";

	public static void main(String[] args) {
		new Main().run();
	}
	
	private void run() {
		new LogConfigurator().configure(APP_NAME_SAND_LORA_GATEWAY, LogLevel.DEBUG);
		
		Gateway<?, ?> gateway = createGateway(new LoraNetwork());
		gateway.registerThingEmulatorFactory(new LightEmulatorFactory());
		
		gateway.setVisible(true);
	}
	
	private Gateway<ICommunicator<DualLoraAddress, LoraAddress, byte[]>, LoraChipCreationParams> createGateway(LoraNetwork network) {
		IDualLoraChipsCommunicator gatewayCommunicator = DualLoraChipsCommunicator.createInstance(
				network, DualLoraAddress.randomDualLoraAddress(0), new LoraChipCreationParams(
						LoraChip.PowerType.HIGH_POWER));
		return new Gateway<ICommunicator<DualLoraAddress, LoraAddress, byte[]>, LoraChipCreationParams>(network, gatewayCommunicator);
	}
	
}
