package com.firstlinecode.sand.emulators.wifi.light;

public class Main {
	public static void main(String[] args) {
		new Main().run();
	}

	private void run() {
		configureLogDir();
		new Light().setVisible(true);
	}
	
	private void configureLogDir() {
		System.setProperty("sand.log.dir", System.getProperty("user.home") + "/.com.firstlinecode.sand/logs");
	}
}
