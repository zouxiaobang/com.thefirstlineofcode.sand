package com.thefirstlineofcode.sand.client.things.concentrator;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.thefirstlineofcode.sand.protocols.core.CommunicationNet;

public class Node implements Externalizable {
	private String deviceId;
	private String lanId;
	private String model;
	private CommunicationNet communicationNet;
	private String address;
	private boolean confirmed;
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getLanId() {
		return lanId;
	}

	public void setLanId(String lanId) {
		this.lanId = lanId;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public CommunicationNet getCommunicationNet() {
		return communicationNet;
	}

	public void setCommunicationNet(CommunicationNet communicationNet) {
		this.communicationNet = communicationNet;
	}

	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(deviceId);
		out.writeObject(lanId);
		out.writeObject(model);
		out.writeObject(communicationNet);
		out.writeObject(address);
		out.writeBoolean(confirmed);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		deviceId = (String)in.readObject();
		lanId = (String)in.readObject();
		model = (String)in.readObject();
		communicationNet = (CommunicationNet)in.readObject();
		address = (String)in.readObject();
		confirmed = in.readBoolean();
	}
}
