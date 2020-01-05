package com.firstlinecode.sand.client.dummyblub;

import javax.swing.JFrame;

public class Main {	
	public static void main(String[] args) {
		new Main().run();
	}
	
	public void run() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DummyBlub blub = new DummyBlub();
				
				JFrame frame = new JFrame(blub.getName());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
				frame.setContentPane(blub.getPanel());	
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
