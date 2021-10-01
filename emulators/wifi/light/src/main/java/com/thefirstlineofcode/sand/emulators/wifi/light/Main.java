package com.thefirstlineofcode.sand.emulators.wifi.light;

public class Main {
	public static void main(String[] args) {
		new Main().run();
	}

	private void run() {
		configureLogDir();
		new LightFrame().setVisible(true);
	}
	
	private void configureLogDir() {
		System.setProperty("sand.log.dir", System.getProperty("user.home") + "/.com.thefirstlineofcode.sand/logs");
	}
}
