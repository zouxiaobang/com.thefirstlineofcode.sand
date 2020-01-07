package com.firstlinecode.sand.emulators.blub;

import javax.swing.JFrame;

public class Main {	
	public static void main(String[] args) {
		new Main().run();
	}
	
	public void run() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Blub blub = new Blub();
				blub.powerOn();
				
				JFrame frame = new JFrame(blub.getName());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
				frame.setContentPane(blub.getPanel());	
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
