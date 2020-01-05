package com.firstlinecode.sand.client.dummything;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public abstract class AbstractDummyThingPanel extends JPanel {	
	private static final long serialVersionUID = 388916038961904955L;
	
	protected StatusBar statusBar;
	
	public AbstractDummyThingPanel() {
		super(new BorderLayout());
		
		add(createThingCustomizedUi(), BorderLayout.CENTER);
		
		statusBar = new StatusBar();
		add(statusBar, BorderLayout.SOUTH);
	}
	
	public void updateStatus(String status) {
		statusBar.setText(status);
	}

	protected abstract JPanel createThingCustomizedUi();
}
