package com.firstlinecode.sand.client.dummygateway;

import com.firstlinecode.sand.client.dummyblub.DummyBlubFactory;

public class Main {
	public static void main(String[] args) {
		new Main().run();
	}

	private void run() {
		DummyGateway gateway = new DummyGateway();
		gateway.registerThingFactory(new DummyBlubFactory());
		gateway.createAndShowGUI();
	}
}
