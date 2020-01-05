package com.firstlinecode.sand.client.dummything;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class StatusBar extends JLabel {
	private static final long serialVersionUID = 5332326077696737355L;
	
	public StatusBar() {
		super();
		setPreferredSize(new Dimension(640, 32));
		setHorizontalAlignment(SwingConstants.RIGHT);
	}
	
}
