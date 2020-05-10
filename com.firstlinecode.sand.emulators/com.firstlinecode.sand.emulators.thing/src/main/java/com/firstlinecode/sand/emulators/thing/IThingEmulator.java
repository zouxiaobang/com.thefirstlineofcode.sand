package com.firstlinecode.sand.emulators.thing;

import java.io.Externalizable;

import com.firstlinecode.sand.client.things.IThing;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public interface IThingEmulator extends IThing, Externalizable {
	boolean isAddressConfigured();
	void startToReceiveData();
	void stopDataReceving();
	void addressConfigured(LoraAddress gatewayUplinkAddress, LoraAddress gatewayDownloadLinkAddress,
			LoraAddress thingAddress);
	void nodeAdded(String lanId);
	void setDeviceId(String deviceId);
	void setBatteryPower(int batteryPower) ;
	void powerChanged(PowerEvent event);
	void reset();
	AbstractThingEmulatorPanel getPanel();
	ICommunicator<?, ?, ?> getCommunicator();
	String getThingName();
}
