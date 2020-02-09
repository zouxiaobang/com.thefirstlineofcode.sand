package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.concentrator.IAddressConfigurator;

public class DynamicAddressConfigurator implements IAddressConfigurator<ICommunicator<LoraAddress, byte[]>> {
	protected ICommunicator<LoraAddress, byte[]> communicator;

	@Override
	public void setCommunicator(ICommunicator<LoraAddress, byte[]> communicator) {
		this.communicator = communicator;
	}
	
	@Override
	public void introduce() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void negotiate() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void comfirm() {
		// TODO Auto-generated method stub
		
	}
	
}
