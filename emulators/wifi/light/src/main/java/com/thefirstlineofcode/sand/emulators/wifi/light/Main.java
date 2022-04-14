package com.thefirstlineofcode.sand.emulators.wifi.light;

import com.thefirstlineofcode.chalk.utils.LogConfigurator;
import com.thefirstlineofcode.chalk.utils.LogConfigurator.LogLevel;

public class Main {
	private static final String APP_NAME_SAND_WIFI_LIGHT = "sand-wifi-light";
	
	public static void main(String[] args) {
		new Main().run();
	}

	private void run() {
		new LogConfigurator().configure(APP_NAME_SAND_WIFI_LIGHT, LogLevel.DEBUG);
		new LightFrame().setVisible(true);
	}
}
