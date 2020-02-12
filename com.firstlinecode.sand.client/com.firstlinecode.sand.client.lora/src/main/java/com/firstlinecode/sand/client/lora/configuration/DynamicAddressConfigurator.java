package com.firstlinecode.sand.client.lora.configuration;

import com.firstlinecode.sand.client.lora.DualLoraAddress;
import com.firstlinecode.sand.client.lora.IDualLoraChipCommunicator;
import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.lora.LoraData;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.IObmFactory;
import com.firstlinecode.sand.client.things.commuication.ObmFactory;
import com.firstlinecode.sand.client.things.concentrator.IAddressConfigurator;

public class DynamicAddressConfigurator implements IAddressConfigurator<IDualLoraChipCommunicator, LoraAddress, byte[]> {
	private static final DualLoraAddress CONFIGURATION_ADDRESS = new DualLoraAddress(
			LoraAddress.MAX_TWO_BYTES_ADDRESS, DualLoraAddress.MAX_CHANNEL);
	
	public enum DynamicConfigurationState {
		WORKING,
		WAITING,
		NEGOTIATING,
		CONFIRMING
	}
	
	private IDualLoraChipCommunicator communicator;
	private DualLoraAddress workingAddress;
	private String nodeDeviceId;
	private LoraAddress nodeAddress;
	
	private IObmFactory obmFactory;
	
	private DynamicConfigurationState state;
	
	public DynamicAddressConfigurator(IDualLoraChipCommunicator communicator) {
		this.communicator = communicator;
		obmFactory = new ObmFactory();
		
		workingAddress = communicator.getAddress();
		
		state = DynamicConfigurationState.WORKING;
	}
	
	public void start() {
		try {
			communicator.changeAddress(CONFIGURATION_ADDRESS);
		} catch (CommunicationException e) {
			throw new RuntimeException("Failed to change address.", e);
		}
		state = DynamicConfigurationState.WAITING;
		
		introduce();
	}
	
	public void stop() {
		state = DynamicConfigurationState.WORKING;
		try {
			communicator.changeAddress(workingAddress);
		} catch (CommunicationException e) {
			throw new RuntimeException("Failed to change address.", e);
		}
	}
	
	@Override
	public void introduce() {
		if (state == DynamicConfigurationState.WORKING)
			return;
		
		new Thread(new DataReceiver()).start();;
	}
	
	private class DataReceiver implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (state != DynamicConfigurationState.WORKING) {
				LoraData data = communicator.receive();
				
				if (data != null) {
					negotiate(data.getAddress(), data.getData());
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
		
	}

	@Override
	public void negotiate(LoraAddress peerAddress, byte[] data) {
		System.out.println(data);
	}

	@Override
	public void comfirm(LoraAddress peerAddress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCommunicator(IDualLoraChipCommunicator communicator) {
		this.communicator = communicator;
	}
}
