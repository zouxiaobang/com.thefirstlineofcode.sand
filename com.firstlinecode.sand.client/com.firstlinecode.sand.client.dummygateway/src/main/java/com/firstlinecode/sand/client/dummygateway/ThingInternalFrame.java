package com.firstlinecode.sand.client.dummygateway;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import com.firstlinecode.sand.client.dummything.IDummyThing;

public class ThingInternalFrame extends JInternalFrame {
	private static final long serialVersionUID = 4975138886817512398L;
	
	private IDummyThing thing;
	
	public ThingInternalFrame(IDummyThing thing) {
		super(thing.getName(), false, false, false, false);
		
		this.thing = thing;
		JPanel panel = thing.getPanel();
		setContentPane(panel);
	}
	
	public IDummyThing getThing() {
		return thing;
	}
}
