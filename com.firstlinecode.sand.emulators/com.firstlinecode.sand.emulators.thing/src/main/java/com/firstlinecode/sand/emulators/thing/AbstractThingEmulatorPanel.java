package com.firstlinecode.sand.emulators.thing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.firstlinecode.sand.client.things.BatteryPowerEvent;
import com.firstlinecode.sand.client.things.IThingListener;

public abstract class AbstractThingEmulatorPanel extends JPanel implements IThingListener {	
	private static final long serialVersionUID = 388916038961904955L;
	
	protected AbstractThingEmulator<?, ?, ?> thingEmulator;
	protected JLabel statusBar;
	
	public AbstractThingEmulatorPanel(AbstractThingEmulator<?, ?, ?> thingEmulator) {
		super(new BorderLayout());
		
		add(createThingCustomizedUi(), BorderLayout.CENTER);
		
		add(creatStatusBarPanel(), BorderLayout.SOUTH);
		
		this.thingEmulator = thingEmulator;
	}
	
	private JPanel creatStatusBarPanel() {
		JPanel statusBarPanel = new JPanel(new BorderLayout());
		
		statusBar = new JLabel();
		statusBar.setHorizontalAlignment(SwingConstants.RIGHT);
		statusBarPanel.add(statusBar, BorderLayout.CENTER);
		
		JButton copy = new JButton("Copy Device ID");
		copy.setToolTipText("Copy thing device ID to clipboard.");
		copy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(thingEmulator.deviceId), null);
				UiUtils.showNotification(getWindow(), "Message", "Device ID has copied to clipboard.");
			}
		});
		statusBarPanel.add(copy, BorderLayout.EAST);
		
		statusBarPanel.setPreferredSize(new Dimension(800, 32));
		
		return statusBarPanel;
	}
	
	private Window getWindow() {
		Container current = this;
		Container parent = null;
		while(true) {
			parent = current.getParent();
			if (parent != null) {
				current = parent;
				continue;
			}
			
			if (current instanceof Window)
				return (Window)current;
		}
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
