package com.firstlinecode.sand.emulators.thing;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractThing implements IThing {
	protected String thingName;
	protected String deviceId;
	protected String lanId;
	protected String name;
	protected int battery;
	protected boolean powered;
	protected List<IDeviceListener> deviceListeners;
	
	public AbstractThing(String thingName) {
		this.thingName = thingName;
		deviceId = ThingsUtils.generateRandomDeviceId();
		battery = 100;
		powered = false;
		
		deviceListeners = new ArrayList<>();
		
		BatteryTimer timer = new BatteryTimer();
		timer.start();
	}
	
	public String getThingStatus() {
		StringBuilder sb = new StringBuilder();
		if (powered) {
			sb.append("Power On, ");
		} else {
			sb.append("Power Off, ");
		}
		
		if (lanId == null) {
			sb.append("Uncontrolled").append(", ");
		} else {
			sb.append("Controlled: ").append(lanId).append(", ");
		}
		
		sb.append("Battery: ").append(battery).append("%, ");
		
		sb.append("Device ID: ").append(deviceId);
		
		return sb.toString();

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
			synchronized (AbstractThing.this) {
				if (powered) {
					if (battery == 0)
						return;
					
					int oldBattery = battery;
					if (battery != 10) {
						battery -= 2;
					} else {
						battery = 100;
					}
					
					for (IDeviceListener deviceListener : deviceListeners) {
						deviceListener.batteryChanged(new BatteryEvent(AbstractThing.this, oldBattery, oldBattery));
					}
				}
				
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
		out.writeObject(thingName);
		out.writeObject(deviceId);
		out.writeObject(name);
		out.writeObject(lanId);
		out.writeInt(battery);
		out.writeBoolean(powered);
		
		doWriteExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		thingName = (String)in.readObject();
		deviceId = (String)in.readObject();
		name = (String)in.readObject();
		lanId = (String)in.readObject();
		battery = in.readInt();
		powered = in.readBoolean();
		
		doReadExternal(in);
	}
	
	@Override
	public void powerOn() {
		this.powered = true;
		doPowerOn();
		
		for (IDeviceListener deviceListener : deviceListeners) {
			deviceListener.powerChanged(new PowerEvent(this, PowerEvent.Type.POWER_ON));
		}
	}
	
	@Override
	public void powerOff() {
		this.powered = false;
		doPowerOff();
		
		for (IDeviceListener deviceListener : deviceListeners) {
			deviceListener.powerChanged(new PowerEvent(this, PowerEvent.Type.POWER_OFF));
		}
	}
	
	@Override
	public boolean isPowered() {
		return powered;
	}
	
	@Override
	public void reset() {
		deviceId = ThingsUtils.generateRandomDeviceId();
		lanId = null;
		
		doReset();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!getClass().equals(obj.getClass()))
			return false;
		
		return deviceId.equals(((IThing)obj).getDeviceId());
	}
	
	@Override
	public void addDeviceListener(IDeviceListener listener) {
		deviceListeners.add(listener);
	}
	
	@Override
	public boolean removeDeviceListener(IDeviceListener listener) {
		return deviceListeners.remove(listener);
	}
	
	protected abstract void doWriteExternal(ObjectOutput out) throws IOException;
	protected abstract void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException;
	protected abstract void doPowerOn();
	protected abstract void doPowerOff();
	protected abstract void doReset();
}
