package com.firstlinecode.sand.client.dummything;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public abstract class AbstractDummyThing implements IDummyThing {
	protected String deviceId;
	protected String lanId;
	protected String name;
	protected int battery;
	
	public AbstractDummyThing() {
		deviceId = generateDeviceId(12);
		battery = 100;
		
		BatteryTimer timer = new BatteryTimer();
		timer.start();
	}
	
	protected String generateDeviceId(int length) {
		if (length <= 16) {
			return String.format("%016X", java.util.UUID.randomUUID().getLeastSignificantBits()).substring(16 - length, 16);
		}
		
		if (length > 32) {
			length = 32;
		}
		
		UUID uuid = UUID.randomUUID();
		String uuidHexString = String.format("%016X%016X", uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
				
		return uuidHexString.substring(32 - length, 32); 
	}
	
	protected String getThingStatus() {
		StringBuilder sb = new StringBuilder();
		if (lanId == null) {
			sb.append("Uncontrolled").append(", ");
		} else {
			sb.append("Controlled: ").append(lanId).append(", ");
		}
		
		sb.append("Battery: ").append(battery).append("%, ");
		
		sb.append("Device ID: ").append(deviceId).append(", ");
		
		return sb.toString().substring(0, sb.length() - 2);

	}
	
	private class BatteryTimer {
		private Timer timer = new Timer();
		
		public void start() {
			timer.schedule(new BatteryTimerTask(), 1000 * 10);
		}
	}
	
	private class BatteryTimerTask extends TimerTask {
		private Timer timer = new Timer();
		
		@Override
		public void run() {
			synchronized (AbstractDummyThing.this) {
				if (battery == 0)
					return;
				
				if (battery != 10) {
					battery -= 2;
				} else {
					battery = 100;
				}
				
				batteryChanged(battery);
				
				timer.schedule(new BatteryTimerTask(), 1000 * 10);
			}
		}
	}
	
	@Override
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String getDeviceId() {
		return deviceId;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public synchronized void setBattery(int battery) {
		if (battery <= 0 || battery > 100) {
			throw new IllegalArgumentException("Battery value must be in the range of 0 to 100.");
		}
		this.battery = battery;
	}
	
	@Override
	public int getBattery() {
		return battery;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeChars(deviceId);
		out.writeChars(name);
		
		doWriteExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		deviceId = (String)in.readObject();
		name = (String)in.readObject();
		
		doReadExternal(in);
	}
	
	protected abstract void doWriteExternal(ObjectOutput out) throws IOException;
	protected abstract void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException;
	protected abstract void batteryChanged(int battery);
}
