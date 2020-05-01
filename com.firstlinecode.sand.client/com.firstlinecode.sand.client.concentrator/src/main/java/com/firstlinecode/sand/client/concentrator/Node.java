package com.firstlinecode.sand.client.concentrator;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.firstlinecode.sand.protocols.core.CommunicationNet;

public class Node implements Externalizable {
	private String deviceId;
	private String lanId;
	private String mode;
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

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
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
		out.writeObject(mode);
		out.writeObject(communicationNet);
		out.writeObject(address);
		out.writeBoolean(confirmed);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		deviceId = (String)in.readObject();
		lanId = (String)in.readObject();
		mode = (String)in.readObject();
		communicationNet = (CommunicationNet)in.readObject();
		address = (String)in.readObject();
		confirmed = in.readBoolean();
	}
}
