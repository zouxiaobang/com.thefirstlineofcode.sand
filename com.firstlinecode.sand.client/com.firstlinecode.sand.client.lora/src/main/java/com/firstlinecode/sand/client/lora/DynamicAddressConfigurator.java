package com.firstlinecode.sand.client.lora;

import com.firstlinecode.chalk.IChatClient;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.IObmFactory;
import com.firstlinecode.sand.client.things.commuication.ObmFactory;
import com.firstlinecode.sand.client.things.concentrator.IAddressConfigurator;
import com.firstlinecode.sand.client.things.concentrator.IConcentrator;
import com.firstlinecode.sand.protocols.core.lora.Allocation;
import com.firstlinecode.sand.protocols.core.lora.Confirmation;
import com.firstlinecode.sand.protocols.core.lora.Introduction;

public class DynamicAddressConfigurator implements IAddressConfigurator<IDualLoraChipCommunicator, LoraAddress, byte[]> {
	private static final DualLoraAddress ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS = new DualLoraAddress(
			LoraAddress.MAX_TWO_BYTES_ADDRESS, DualLoraAddress.MAX_CHANNEL);
	
	private enum State {
		WORKING,
		WAITING,
		ALLOCATING,
		CONFIRMING
	}
	
	private IConcentrator<LoraAddress> concentrator;
	private IDualLoraChipCommunicator communicator;
	private DualLoraAddress workingAddress;
	private String nodeDeviceId;
	private LoraAddress nodeAddress;
	private String nodeLanId;
	
	private IObmFactory obmFactory;
	private IChatClient chatClient;
	
	private State state;
	
	public DynamicAddressConfigurator(IConcentrator<LoraAddress> concentrator,
			IDualLoraChipCommunicator communicator, IChatClient chatClient) {
		this.concentrator = concentrator;
		this.communicator = communicator;
		this.chatClient = chatClient;
		
		obmFactory = new ObmFactory();
		workingAddress = communicator.getAddress();
		state = State.WORKING;
	}
	
	public void start() {
		try {
			if (!communicator.getAddress().equals(ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS)) {				
				workingAddress = communicator.getAddress();
				communicator.changeAddress(ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS);
			}
		} catch (CommunicationException e) {
			throw new RuntimeException("Failed to change address.", e);
		}
		state = State.WAITING;
		
		introduce();
	}
	
	public void stop() {
		if (state == State.WORKING)
			return;
		
		state = State.WORKING;
		if (communicator.getAddress().equals(ADDRESS_CONFIGURATION_MODE_DUAL_LORA_ADDRESS))
		try {
			communicator.changeAddress(workingAddress);
		} catch (CommunicationException e) {
			throw new RuntimeException("Failed to change address.", e);
		}
	}
	
	@Override
	public void introduce() {
		if (state == State.WORKING)
			return;
		
		new Thread(new DataReceiver()).start();;
	}
	
	private class DataReceiver implements Runnable {

		@Override
		public void run() {
			while (state != State.WORKING) {
				LoraData data = communicator.receive();
				
				if (data != null) {
					negotiate(data.getAddress(), data.getData());
				}
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
		
	}

	@Override
	public void negotiate(LoraAddress peerAddress, byte[] data) {
		if (state == State.WORKING)
			return;
		
		try {			
			if (state == State.WAITING) {
				Introduction introduction = (Introduction)obmFactory.toObject(Introduction.class, data);
				nodeDeviceId = introduction.getDeviceId();
				nodeAddress = new LoraAddress(introduction.getAddress(), introduction.getFrequencyBand());
				
				Allocation allocation = new Allocation();
				allocation.setGatewayAddress(workingAddress.getSlaveAddress().getAddress());
				allocation.setGatewayFrequencyBand(workingAddress.getSlaveAddress().getFrequencyBand());
				
				int nodesSize = concentrator.getLanIds().length;
				nodeLanId = Integer.toString(nodesSize);
				allocation.setAllocatedAddress(nodesSize);
				allocation.setAllocatedFrequencyBand(LoraAddress.DEFAULT_THING_COMMUNICATION_FREQUENCE_BAND);
				
				byte[] response = obmFactory.toBinary(allocation);
				communicator.send(nodeAddress, response);
				
				state = State.ALLOCATING;
			} else if (state == State.ALLOCATING) {
				Confirmation confirmation = (Confirmation)obmFactory.toObject(Confirmation.class, data);
				
				if (nodeDeviceId == confirmation.getDeviceId()) {
					confirm();
					state = State.CONFIRMING;
				}
			} else {
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}

	@Override
	public void setCommunicator(IDualLoraChipCommunicator communicator) {
		this.communicator = communicator;
	}
	
	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}
}
