package com.thefirstlineofcode.sand.emulators.lora.gateway;

import com.thefirstlineofcode.chalk.utils.LogConfigurator;
import com.thefirstlineofcode.chalk.utils.LogConfigurator.LogLevel;
import com.thefirstlineofcode.sand.client.lora.IDualLoraChipsCommunicator;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicator;
import com.thefirstlineofcode.sand.emulators.lora.light.LightEmulatorFactory;
import com.thefirstlineofcode.sand.emulators.lora.network.DualLoraChipsCommunicator;
import com.thefirstlineofcode.sand.emulators.lora.network.LoraChip;
import com.thefirstlineofcode.sand.emulators.lora.network.LoraChipCreationParams;
import com.thefirstlineofcode.sand.emulators.lora.network.LoraNetwork;
import com.thefirstlineofcode.sand.protocols.lora.DualLoraAddress;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public class Main {
	private static final String APP_NAME_SAND_LORA_GATEWAY = "sand-lora-gateway";

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