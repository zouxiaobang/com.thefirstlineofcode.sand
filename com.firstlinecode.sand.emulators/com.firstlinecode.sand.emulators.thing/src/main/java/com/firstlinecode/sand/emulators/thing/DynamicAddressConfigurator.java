package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.concentrator.IAddressConfigurator;
import com.firstlinecode.sand.emulators.lora.LoraCommunicator;

public class DynamicAddressConfigurator implements IAddressConfigurator<ICommunicator<LoraAddress, byte[]>,
		LoraAddress, byte[]>, ICommunicationListener<LoraAddress, byte[]> {
	private ICommunicator<LoraAddress, byte[]> communicator;
	
	private enum State {
		NONE,
		INTRUDUCED,
		NEGOTIATED,
		CONFIRMED
	}
	
	public DynamicAddressConfigurator(LoraCommunicator communicator) {
		this.communicator = communicator;
	}

	@Override
	public void setCommunicator(ICommunicator<LoraAddress, byte[]> communicator) {
		this.communicator = communicator;
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
				communicator.send(LoraAddress.DEFAULLT_ADDRESS_CONFIGURATOR_LORA_ADDRESS, new byte[] {0xF, 0xF});
				
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
	
}
