package com.firstlinecode.sand.emulators.lora;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.firstlinecode.sand.client.lora.ILoraChip;
import com.firstlinecode.sand.client.lora.LoraAddress;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;

public class LoraChip implements ILoraChip {
	public enum Type {
		HIGH_POWER,
		NORMAL
	}
	
	protected ILoraNetwork network;
	protected Type type;
	protected LoraAddress address;
	protected volatile boolean slept;
	
	protected List<ICommunicationListener<LoraAddress, byte[]>> listeners;
	
	public LoraChip(ILoraNetwork network, Type type, LoraAddress address) {
		if (network == null)
			throw new IllegalArgumentException("Null network.");
		
		if (type == null)
			throw new IllegalArgumentException("Null lora chip type.");
		
		if (address == null)
			throw new IllegalArgumentException("Null address.");
		
		this.network = network;
		this.type = type;
		this.address = address;
		
		listeners = new ArrayList<>();
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public LoraAddress getAddress() {
		return address;
	}

	@Override
	public void send(LoraAddress to, byte[] message) {
		network.sendData(this, to, message);
	}
	
	@Override
	public void addListener(ICommunicationListener<LoraAddress, byte[]> listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public boolean removeListener(ICommunicationListener<LoraAddress, byte[]> listener) {
		return listeners.remove(listener);
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash += 31 * network.hashCode();
		hash += 31 * type.hashCode();
		hash += 31 * address.hashCode();
		
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LoraChip) {
			LoraChip other = (LoraChip)obj;
			return network.equals(other.network) && address.equals(other.address);
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("LoraChip[%s, %s, %s]", network, type, address);
	}

	@Override
	public void received(LoraAddress from, byte[] message) {
		for (ICommunicationListener<LoraAddress, byte[]> listener : listeners) {
			listener.received(from, message);
		}
		
		doReceived(from, message);
	}

	protected void doReceived(LoraAddress from, byte[] message) {}

	@Override
	public void changeAddress(LoraAddress address) {
		this.address = address;
		network.changeAddress(this, address);
	}
	
	@Override
	public void sleep() {
		slept = true;
	}
	
	@Override
	public void sleep(int millis) {
		slept = true;
		
		new Timer().schedule(new TimerTask() {		
			@Override
			public void run() {
				wakeUp();
			}
		}, millis);
	}
	
	@Override
	public boolean isSlept() {
		return slept;
	}

	@Override
	public void wakeUp() {
		slept = false;
	}

}
