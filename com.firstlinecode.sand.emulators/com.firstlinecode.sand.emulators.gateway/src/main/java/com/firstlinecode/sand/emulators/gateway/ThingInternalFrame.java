package com.firstlinecode.sand.emulators.gateway;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import com.firstlinecode.sand.emulators.thing.IThingEmulator;

public class ThingInternalFrame extends JInternalFrame {
	private static final long serialVersionUID = 4975138886817512398L;
	
	private IThingEmulator thing;
	
	public ThingInternalFrame(IThingEmulator thing, String title) {
		super(title, false, false, false, false);
		
		this.thing = thing;
		JPanel panel = thing.getPanel();
		setContentPane(panel);
	}
	
	public IThingEmulator getThing() {
		return thing;
	}
}
