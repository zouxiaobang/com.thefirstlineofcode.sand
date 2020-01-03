package com.firstlinecode.sand.client.dummything;

import java.io.Externalizable;

import javax.swing.JPanel;

public interface IDummyThing extends Externalizable {
	void setInstanceName(String instanceName);
	String getInstanceName();
	JPanel getPanel();
}
