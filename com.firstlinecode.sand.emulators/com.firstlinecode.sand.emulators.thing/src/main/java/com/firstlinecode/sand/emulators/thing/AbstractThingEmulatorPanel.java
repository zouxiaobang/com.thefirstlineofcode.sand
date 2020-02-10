package com.firstlinecode.sand.emulators.thing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.firstlinecode.sand.client.things.BatteryPowerEvent;
import com.firstlinecode.sand.client.things.IThingListener;

public abstract class AbstractThingEmulatorPanel extends JPanel implements IThingListener {	
	private static final long serialVersionUID = 388916038961904955L;
	
	protected AbstractThingEmulator thingEmulator;
	protected JLabel statusBar;
	
	public AbstractThingEmulatorPanel(AbstractThingEmulator thingEmulator) {
		super(new BorderLayout());
		
		add(createThingCustomizedUi(), BorderLayout.CENTER);
		
		statusBar = creatStatusBar();;
		add(statusBar, BorderLayout.SOUTH);
		
		this.thingEmulator = thingEmulator;
	}
	
	private JLabel creatStatusBar() {
		JLabel statusBar = new JLabel();
		statusBar.setHorizontalAlignment(SwingConstants.RIGHT);
		statusBar.setPreferredSize(new Dimension(640, 48));
		
		return statusBar;
	}

	public void updateStatus(String status) {
		statusBar.setText(status);
	}
	
	@Override
	public void batteryPowerChanged(BatteryPowerEvent event) {
		updateStatus(thingEmulator.getThingStatus());
	}

	protected abstract JPanel createThingCustomizedUi();
}
