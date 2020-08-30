package com.firstlinecode.sand.emulators.lora.thing;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.firstlinecode.basalt.oxm.binary.BinaryUtils;
import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.gem.protocols.bxmpp.BinaryMessageProtocolReader;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.obm.IObmFactory;
import com.firstlinecode.sand.client.things.obm.ObmFactory;
import com.firstlinecode.sand.emulators.lora.network.LoraCommunicator;
import com.firstlinecode.sand.emulators.thing.AbstractCommunicationNetworkThingEmulator;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public abstract class AbstractLoraThingEmulator extends AbstractCommunicationNetworkThingEmulator<LoraAddress, LoraAddress, byte[]> {
	private static final String PATTERN_LAN_ID = "%02d";
	
	protected DynamicAddressConfigurator addressConfigurator;
		
	protected LoraAddress gatewayUplinkAddress;
	protected LoraAddress gatewayDownlinkAddress;
	protected LoraAddress thingAddress;
	
	protected String lanId;
	
	protected IObmFactory obmFactory = ObmFactory.createInstance();
	
	protected BinaryMessageProtocolReader bMessageProtocolReader;
		
	public AbstractLoraThingEmulator(String mode, ICommunicator<LoraAddress, LoraAddress, byte[]> communicator) {
		super(mode, communicator);
		
		ObmFactory obmFactory = (ObmFactory)ObmFactory.createInstance();
		bMessageProtocolReader = new BinaryMessageProtocolReader(obmFactory.getBinaryXmppProtocolConverter());
	}
	
	public String getThingStatus() {
		StringBuilder sb = new StringBuilder();
		if (powered) {
			sb.append("Power On, ");
		} else {
			sb.append("Power Off, ");
		}
		
		if (!isAddressConfigured()) {
			sb.append("Unconfigured").append(", ");
		} else if (lanId == null) {
			sb.append("Configured: ").append(String.format(PATTERN_LAN_ID, thingAddress.getAddress())).append(", ");
		} else {
			sb.append("Controlled: ").append(lanId).append(", ");			
		}
		
		sb.append("Battery: ").append(batteryPower).append("%, ");
		
		sb.append("Device ID: ").append(deviceId);
		
		return sb.toString();
	}

	@Override
	protected void doWriteExternal(ObjectOutput out) throws IOException {
		out.writeObject(gatewayUplinkAddress);
		out.writeObject(gatewayDownlinkAddress);
		out.writeObject(thingAddress);
	}
	
	@Override
	protected void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		gatewayUplinkAddress = (LoraAddress)in.readObject();
		gatewayDownlinkAddress = (LoraAddress)in.readObject();
		thingAddress = (LoraAddress)in.readObject();
	}
	
	@Override
	protected void doPowerOn() {
		if (lanId != null) {
			// Node has added to concentrator. Start to receive data from concentrator.
			startToReceiveData();			
		} else {			
			if (!isAddressConfigured()) {
				if (addressConfigurator == null) {
					addressConfigurator = new DynamicAddressConfigurator(this, (LoraCommunicator)communicator);
				}
				
				addressConfigurator.introduce();
			}
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
		if (isDataReceiving)
			return;
		
		communicator.addCommunicationListener(this);
		doStartToReceiveData();
		isDataReceiving = true;
	}
	
	@Override
	public void stopDataReceving() {
		if (!isDataReceiving)
			return;
		
		doStopDataReceiving();
		communicator.removeCommunicationListener(this);
		
		isDataReceiving = false;
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
	
	protected void addressConfigured(LoraAddress gatewayUplinkAddress, LoraAddress gatewayDownlinkAddress,
			LoraAddress thingAddress) {
		this.gatewayUplinkAddress = gatewayUplinkAddress;
		this.gatewayDownlinkAddress = gatewayDownlinkAddress;
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
	
	protected Protocol readProtocol(byte[] data) {
		return bMessageProtocolReader.readProtocol(data);
	}
	
	protected <A> A readAction(Class<A> actionType, byte[] data) {
		return (A)obmFactory.toObject(actionType, data);
	}
	
	protected String getDataInfoString(byte[] data) {
		return BinaryUtils.getHexStringFromBytes(data);
	}
}
