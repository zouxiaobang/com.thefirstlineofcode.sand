package com.firstlinecode.sand.client.dummything;

import java.io.Externalizable;

import javax.swing.JPanel;

public interface IDummyThing extends IDummyDevice, Externalizable {
	void setName(String name);
	String getName();
	JPanel getPanel();
}
