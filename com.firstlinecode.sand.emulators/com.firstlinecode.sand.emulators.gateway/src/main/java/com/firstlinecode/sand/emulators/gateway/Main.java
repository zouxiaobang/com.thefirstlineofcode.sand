package com.firstlinecode.sand.emulators.gateway;

import com.firstlinecode.sand.emulators.blub.BlubEmulatorFactory;
import com.firstlinecode.sand.emulators.lora.LoraNetwork;

public class Main {
	public static void main(String[] args) {
		new Main().run();
	}

	private void run() {
		Gateway<?, ?, ?> gateway = new Gateway<>(new LoraCommunicatorFactory(new LoraNetwork()));
		gateway.registerThingEmulatorFactory(new BlubEmulatorFactory());
		gateway.setVisible(true);
	}
}
