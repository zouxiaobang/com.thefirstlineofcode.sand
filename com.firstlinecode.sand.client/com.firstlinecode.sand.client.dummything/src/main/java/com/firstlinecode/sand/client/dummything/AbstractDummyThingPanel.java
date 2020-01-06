package com.firstlinecode.sand.client.dummything;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public abstract class AbstractDummyThingPanel extends JPanel implements IDeviceListener {	
	private static final long serialVersionUID = 388916038961904955L;
	
	protected AbstractDummyThing thing;
	protected StatusBar statusBar;
	
	public AbstractDummyThingPanel(AbstractDummyThing thing) {
		super(new BorderLayout());
		
		add(createThingCustomizedUi(), BorderLayout.CENTER);
		
		statusBar = new StatusBar();
		add(statusBar, BorderLayout.SOUTH);
		
		this.thing = thing;
		this.thing.addDeviceListener(this);
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
