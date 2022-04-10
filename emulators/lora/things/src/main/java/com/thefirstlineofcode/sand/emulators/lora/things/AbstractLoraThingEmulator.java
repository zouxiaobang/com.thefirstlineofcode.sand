package com.thefirstlineofcode.sand.emulators.lora.things;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.thefirstlineofcode.sand.client.lora.ILoraChip.PowerType;
import com.thefirstlineofcode.sand.client.things.commuication.CommunicationException;
import com.thefirstlineofcode.sand.client.things.commuication.ICommunicator;
import com.thefirstlineofcode.sand.emulators.lora.network.LoraChipCreationParams;
import com.thefirstlineofcode.sand.emulators.lora.network.LoraCommunicator;
import com.thefirstlineofcode.sand.emulators.lora.network.LoraCommunicatorFactory;
import com.thefirstlineofcode.sand.emulators.things.emulators.AbstractCommunicationNetworkThingEmulator;
import com.thefirstlineofcode.sand.protocols.actuator.LanExecute;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public abstract class AbstractLoraThingEmulator extends AbstractCommunicationNetworkThingEmulator<LoraAddress, LoraAddress> {
	private static final String PATTERN_LAN_ID = "%02d";
	
	protected NodeDynamicalAddressConfigurator addressConfigurator;
	
	protected LoraAddress gatewayUplinkAddress;
	protected LoraAddress gatewayDownlinkAddress;
	protected LoraAddress thingAddress;
	
	protected String lanId;
	
	public AbstractLoraThingEmulator() {}
	
	public AbstractLoraThingEmulator(String type, String model, ICommunicator<LoraAddress, LoraAddress, byte[]> communicator) {
		super(type, model, communicator);
		
		init();
	}

	private void init() {
		if (dataReceiving) {
			dataReceiving = false;
			startToReceiveData();
		}
	}
	
	public String getThingStatus() {
		StringBuilder sb = new StringBuilder();
		if (powered) {
			sb.append("Power On. ");
		} else {
			sb.append("Power Off. ");
		}
		
		if (!isAddressConfigured()) {
			sb.append("Unconfigured").append(". ");
		} else if (lanId == null) {
			sb.append("Configured: ").append(String.format(PATTERN_LAN_ID, thingAddress.getAddress())).append(". ");
		} else {
			sb.append("Controlled: ").append(lanId).append(". ");
		}
		
		sb.append("Battery: ").append(batteryPower).append("%. ");
		
		sb.append("Device ID: ").append(deviceId);
		
		return sb.toString();
	}

	@Override
	protected void doWriteExternal(ObjectOutput out) throws IOException {
		super.doWriteExternal(out);
		
		LoraCommunicator loraCommunicator = (LoraCommunicator)communicator;
		out.writeObject(loraCommunicator.getChip().getPowerType());
		out.writeObject(loraCommunicator.getChip().getAddress());
		out.writeObject(gatewayUplinkAddress);		
		out.writeObject(gatewayDownlinkAddress);
		out.writeObject(thingAddress);
		out.writeObject(lanId);
	}
	
	@Override
	protected void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.doReadExternal(in);
		
		communicator = LoraCommunicatorFactory.getInstance().createLoraCommunicator(new LoraChipCreationParams(
				(PowerType)in.readObject(), (LoraAddress)in.readObject()));
		gatewayUplinkAddress = (LoraAddress)in.readObject();
		gatewayDownlinkAddress = (LoraAddress)in.readObject();
		thingAddress = (LoraAddress)in.readObject();
		lanId = (String)in.readObject();
		
		init();
	}
	
	@Override
	protected void doPowerOn() {
		if (lanId != null) {
			// Node has already added to concentrator. Start to receive data from concentrator.
			startToReceiveData();			
		} else {			
			if (isAddressConfigured()) {
				throw new IllegalStateException(String.format("Node device which's device ID is '%s' is in a illegal state. Address has already configured, but LAN ID is still null.",
						deviceId));
			}
			
			if (addressConfigurator == null) {
				addressConfigurator = new NodeDynamicalAddressConfigurator(this, (LoraCommunicator)communicator, obmFactory);
			}
			
			addressConfigurator.introduce();
		}
	}
	
	public void nodeAdded(String lanId) {
		this.lanId = lanId;
		
		if (isPowered()) {
			startToReceiveData();
		}
	}
	
	@Override
	public void startToReceiveData() {
		if (dataReceiving)
			return;
		
		communicator.addCommunicationListener(this);
		doStartToReceiveData();
		dataReceiving = true;
	}
	
	@Override
	public void stopDataReceving() {
		if (!dataReceiving)
			return;
		
		doStopDataReceiving();
		communicator.removeCommunicationListener(this);
		
		dataReceiving = false;
	}
	
	protected abstract void doStartToReceiveData();
	protected abstract void doStopDataReceiving();

	@Override
	protected void doPowerOff() {
		if (isAddressConfigured() && addressConfigurator != null) {
			addressConfigurator.stop();
			addressConfigurator = null;
		} else {
			stopDataReceving();
		}
	}
	
	@Override
	protected void doReset() {
		gatewayUplinkAddress = null;
		gatewayDownlinkAddress = null;
		thingAddress = null;
	}
	
	protected void addressConfigured(LoraAddress gatewayDownlinkAddress, LoraAddress gatewayUplinkAddress,
			LoraAddress thingAddress) {
		this.gatewayDownlinkAddress = gatewayDownlinkAddress;
		this.gatewayUplinkAddress = gatewayUplinkAddress;
		this.thingAddress = thingAddress;
		
		if (addressConfigurator != null) {
			addressConfigurator.stop();
			addressConfigurator = null;
		}
		
		getPanel().updateStatus(getThingStatus());
	}
	
	public boolean isAddressConfigured() {
		return gatewayUplinkAddress != null && gatewayDownlinkAddress != null && thingAddress != null;
	}

	@Override
	public void occurred(CommunicationException e) {}
	
	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {}
	
	protected void sendToPeer(LoraAddress from, LanExecute response) {
		try {
			communicator.send(gatewayUplinkAddress, obmFactory.toBinary(response));
		} catch (CommunicationException ce) {
			ce.printStackTrace();
		}
	}
}
