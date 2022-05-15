package com.thefirstlineofcode.sand.emulators.commons.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.thefirstlineofcode.sand.client.core.IDevice;

public class StatusBar extends JPanel {
	private static final long serialVersionUID = -4540556323673700464L;
	
	private JLabel text;
	
	public StatusBar(IDevice device) {
		super(new BorderLayout());
		
		JPanel statusBarPanel = new JPanel();
		statusBarPanel.add(new CopyDeviceIdOrShowQrCodeButton(device));
		
		text = new JLabel();
		text.setHorizontalAlignment(SwingConstants.LEFT);
		statusBarPanel.add(text);
		
		add(statusBarPanel, BorderLayout.WEST);	
		setPreferredSize(new Dimension(720, 48));
	}
	
	public void setText(String status) {
		text.setText(status);
	}
}
