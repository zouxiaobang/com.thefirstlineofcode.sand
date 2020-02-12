package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.commuication.IObmFactory;
import com.firstlinecode.sand.client.things.commuication.ObmFactory;
import com.firstlinecode.sand.client.things.concentrator.IAddressConfigurator;
import com.firstlinecode.sand.emulators.lora.LoraCommunicator;
import com.firstlinecode.sand.protocols.core.lora.Introduction;

public class DynamicAddressConfigurator implements IAddressConfigurator<ICommunicator<LoraAddress, byte[]>,
		LoraAddress, byte[]>, ICommunicationListener<LoraAddress, byte[]> {
	private ICommunicator<LoraAddress, byte[]> communicator;
	private IObmFactory obmFactory;
	
	private enum State {
		NONE,
		INTRUDUCED,
		NEGOTIATED,
		CONFIRMED
	}
	
	public DynamicAddressConfigurator(LoraCommunicator communicator) {
		this.communicator = communicator;
		obmFactory = new ObmFactory();
		
		communicator.addCommunicationListener(this);
	}

	@Override
	public void setCommunicator(ICommunicator<LoraAddress, byte[]> communicator) {
		this.communicator = communicator;
		communicator.addCommunicationListener(this);
	}
	
	@Override
	public void introduce() {
		new Thread(new AddressConfigurationThread()).start();
	}
	
	private class AddressConfigurationThread implements Runnable {

		@Override
		public void run() {
			State state = State.NONE;
			
			while (state != State.CONFIRMED) {
				Introduction introduction = new Introduction();
				introduction.setAddress(LoraAddress.DEFAULT_DYANAMIC_ADDRESS_CONFIGURATOR_ADDRESS);
				introduction.setFrequencyBand(LoraAddress.DEFAULT_DYANAMIC_ADDRESS_CONFIGURATOR_SLAVE_CHIP_FREQUENCE_BAND);
				communicator.send(LoraAddress.DEFAULLT_ADDRESS_CONFIGURATOR_NEGOTIATION_ADDRESS,
						obmFactory.toBinary(introduction));
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
		
	}
	
	@Override
	public void negotiate(LoraAddress peerAddress, byte[] data) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void comfirm(LoraAddress peerAddress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sent(LoraAddress to, byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void received(LoraAddress from, byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void occurred(CommunicationException e) {
		// TODO Auto-generated method stub
		
	}
	
	public void done(LoraAddress address) {
		
	}

	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
		// TODO Auto-generated method stub
		
	}
	
}
