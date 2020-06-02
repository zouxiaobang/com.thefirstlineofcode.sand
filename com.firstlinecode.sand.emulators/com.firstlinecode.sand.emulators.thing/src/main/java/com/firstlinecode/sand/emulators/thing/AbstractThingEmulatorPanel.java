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
		
		this.thingEmulator = thingEmulator;
		add(createThingCustomizedUi(), BorderLayout.CENTER);
		
		add(creatStatusBarPanel(), BorderLayout.SOUTH);
		
	}
	
	private JPanel creatStatusBarPanel() {
		JPanel statusBarPanel = new JPanel(new BorderLayout());
		
		statusBar = new JLabel();
		statusBar.setHorizontalAlignment(SwingConstants.RIGHT);
		statusBarPanel.add(statusBar, BorderLayout.CENTER);
		
		statusBarPanel.add(new CopyDeviceIdOrShowQrCodeButton(thingEmulator.getDeviceId()), BorderLayout.EAST);
		
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

	protected abstract JPanel createThingCustomizedUi();
}
