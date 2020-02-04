package com.firstlinecode.sand.emulators.gateway;

import com.firstlinecode.sand.emulators.blub.BlubEmulatorFactory;

public class Main {
	public static void main(String[] args) {
		new Main().run();
	}

	private void run() {
		Gateway gateway = new Gateway();
		gateway.registerThingFactory(new BlubEmulatorFactory());
		gateway.setVisible(true);
	}
}
