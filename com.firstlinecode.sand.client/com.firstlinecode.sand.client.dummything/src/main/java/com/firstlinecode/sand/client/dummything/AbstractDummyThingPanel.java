package com.firstlinecode.sand.client.dummything;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public abstract class AbstractDummyThingPanel extends JPanel implements IDeviceListener {	
	private static final long serialVersionUID = 388916038961904955L;
	
	protected AbstractDummyThing thing;
	protected JLabel statusBar;
	
	public AbstractDummyThingPanel(AbstractDummyThing thing) {
		super(new BorderLayout());
		
		add(createThingCustomizedUi(), BorderLayout.CENTER);
		
		statusBar = creatStatusBar();;
		add(statusBar, BorderLayout.SOUTH);
		
		this.thing = thing;
		this.thing.addDeviceListener(this);
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
	public void powerChanged(PowerEvent event) {
		updateStatus(thing.getThingStatus());
	}
	
	@Override
	public void batteryChanged(BatteryEvent event) {
		updateStatus(thing.getThingStatus());
	}

	protected abstract JPanel createThingCustomizedUi();
}
