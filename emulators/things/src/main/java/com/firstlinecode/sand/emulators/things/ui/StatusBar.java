package com.firstlinecode.sand.emulators.things.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.firstlinecode.sand.client.things.IDevice;

public class StatusBar extends JPanel {
	private static final long serialVersionUID = -4540556323673700464L;
	
	private JLabel text;
	
	public StatusBar(IDevice device) {
		super(new BorderLayout());
		
		JPanel statusBarPanel = new JPanel();
		text = new JLabel();
		text.setHorizontalAlignment(SwingConstants.RIGHT);
		statusBarPanel.add(text);
		
		statusBarPanel.add(new CopyDeviceIdOrShowQrCodeButton(device));
		
		add(statusBarPanel, BorderLayout.EAST);	
		setPreferredSize(new Dimension(640, 48));
	}
	
	public void setText(String status) {
		text.setText(status);
	}
}
