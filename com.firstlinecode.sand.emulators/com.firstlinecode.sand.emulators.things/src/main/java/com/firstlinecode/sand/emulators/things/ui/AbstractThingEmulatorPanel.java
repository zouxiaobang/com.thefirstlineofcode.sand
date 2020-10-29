package com.firstlinecode.sand.emulators.things.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.firstlinecode.sand.client.things.BatteryPowerEvent;
import com.firstlinecode.sand.client.things.IDeviceListener;
import com.firstlinecode.sand.emulators.things.emulators.ISwitchStateListener;
import com.firstlinecode.sand.emulators.things.emulators.IThingEmulator;

public abstract class AbstractThingEmulatorPanel<T extends IThingEmulator> extends JPanel implements IDeviceListener {	
	private static final long serialVersionUID = 388916038961904955L;
	
	protected T thingEmulator;
	protected JLabel statusBar;
	
	protected ISwitchStateListener switchStateListener;
	
	public AbstractThingEmulatorPanel(T thingEmulator) {
		super(new BorderLayout());
		
		this.thingEmulator = thingEmulator;
		add(createThingCustomizedUi(thingEmulator), BorderLayout.CENTER);
		
		add(creatStatusBarPanel(), BorderLayout.SOUTH);
		
	}
	
	private JPanel creatStatusBarPanel() {
		JPanel statusBarPanel = new JPanel(new BorderLayout());
		
		statusBar = new JLabel();
		statusBar.setHorizontalAlignment(SwingConstants.RIGHT);
		statusBarPanel.add(statusBar, BorderLayout.CENTER);
		
		statusBarPanel.add(new CopyDeviceIdOrShowQrCodeButton(thingEmulator), BorderLayout.EAST);
		
		statusBarPanel.setPreferredSize(new Dimension(800, 32));
		
		return statusBarPanel;
	}

	public void updateStatus(String status) {
		statusBar.setText(status);
	}
	
	@Override
	public void batteryPowerChanged(BatteryPowerEvent event) {
		updateStatus(thingEmulator.getThingStatus());
	}
	
	public void setSwitchStateListener(ISwitchStateListener switchStateListener) {
		this.switchStateListener = switchStateListener;
	}

	protected abstract JPanel createThingCustomizedUi(T thingEmulator);
}
