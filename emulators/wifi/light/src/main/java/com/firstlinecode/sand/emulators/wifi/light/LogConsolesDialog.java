package com.firstlinecode.sand.emulators.wifi.light;

import javax.swing.JFrame;

import com.firstlinecode.sand.emulators.things.ui.AbstractLogConsolesDialog;

public class LogConsolesDialog extends AbstractLogConsolesDialog {
	private static final long serialVersionUID = -2688096815046190393L;
	
	public LogConsolesDialog(JFrame parent) {
		super(parent);
		
		createPreinstlledLogConsoles();
	}

	@Override
	protected void createPreinstlledLogConsoles() {
		createInternetLogConsole();
	}

}
