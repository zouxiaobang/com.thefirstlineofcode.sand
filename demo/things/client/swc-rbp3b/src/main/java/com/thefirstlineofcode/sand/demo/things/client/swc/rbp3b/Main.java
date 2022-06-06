package com.thefirstlineofcode.sand.demo.things.client.swc.rbp3b;

import com.thefirstlineofcode.chalk.logger.LogConfigurator;
import com.thefirstlineofcode.chalk.logger.LogConfigurator.LogLevel;

public class Main {
	public static void main(String[] args) {
		new Main().run(args);
	}
	
	private void run(String[] args) {
		new LogConfigurator().configure("simple-webcam", LogLevel.INFO);
		new Webcam().start();
	}
}
