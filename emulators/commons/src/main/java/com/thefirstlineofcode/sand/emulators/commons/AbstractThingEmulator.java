package com.thefirstlineofcode.sand.emulators.commons;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.thefirstlineofcode.sand.client.things.BatteryPowerEvent;
import com.thefirstlineofcode.sand.client.things.IDeviceListener;
import com.thefirstlineofcode.sand.client.things.ThingsUtils;

public abstract class AbstractThingEmulator implements IThingEmulator, Externalizable {
	private static final long serialVersionUID = 5777576412420781910L;

	private static final int BATTERY_POWER_DOWN_INTERVAL = 1000 * 10;
	
	protected String name;
	
	protected String deviceId;
	protected String type;
	protected String model;
	protected int batteryPower;
	protected boolean powered;
	protected List<IDeviceListener> deviceListeners = new ArrayList<>();
	
	protected BatteryTimer batteryTimer;
	
	public AbstractThingEmulator() {}
	
	public AbstractThingEmulator(String type, String model) {
		if (type == null || model == null)
			throw new IllegalArgumentException("Null type of model.");
		
		this.type = type;
		this.model = model;
		
		this.name = type + " - " + model;
		
		deviceId = generateDeviceId();
		batteryPower = 100;
		powered = false;
	}

	protected void startBatteryTimer() {
		if (batteryTimer == null)
			batteryTimer = new BatteryTimer(getDeviceName(), deviceId);
		
		batteryTimer.start();
	}
	
	protected void stopBatteryTimer() {
		batteryTimer.stop();
		batteryTimer = null;
	}

	protected String generateDeviceId() {
		return getDeviceModel() + ThingsUtils.generateRandomId(8);
	}
	
	public String getThingStatus() {
		StringBuilder sb = new StringBuilder();
		if (powered) {
			sb.append("Power On, ");
		} else {
			sb.append("Power Off, ");
		}
		
		sb.append("Battery: ").append(batteryPower).append("%, ");
		
		sb.append("Device ID: ").append(deviceId);
		
		return sb.toString();

	}
	
	private class BatteryTimer {
		private String thingName;
		private String deviceId;
		private Timer timer;
		
		public BatteryTimer(String thingName, String deviceId) {
			this.thingName = thingName;
			this.deviceId = deviceId;
		}
		
		public boolean isWorking() {
			return timer != null;
		}
		
		public void start() {
			timer = new Timer(String.format("%s '%s' Battery Timer", thingName, deviceId));
			timer.schedule(new BatteryPowerTimerTask(), BATTERY_POWER_DOWN_INTERVAL, BATTERY_POWER_DOWN_INTERVAL);
		}
		
		public void stop() {
			timer.cancel();
			timer = null;
		}
	}
	
	private class BatteryPowerTimerTask extends TimerTask {
		@Override
		public void run() {
			synchronized (AbstractThingEmulator.this) {
				if (powered && downBatteryPower()) {
					getPanel().updateStatus(getThingStatus());
					
					for (IDeviceListener deviceListener : deviceListeners) {
						deviceListener.batteryPowerChanged(new BatteryPowerEvent(AbstractThingEmulator.this, batteryPower));
					}
				}
			}
		}
	}
	
	protected boolean downBatteryPower() {
		if (batteryPower == 0)
			return false;
		
		if (batteryPower != 10) {
			batteryPower -= 2;
		} else {
			batteryPower = 100;
		}
		
		return true;
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
	public synchronized void setBatteryPower(int batteryPower) {
		if (batteryPower <= 0 || batteryPower > 100) {
			throw new IllegalArgumentException("Battery power value must be in the range of 0 to 100.");
		}
		
		if (this.batteryPower != batteryPower) {			
			this.batteryPower = batteryPower;
			getPanel().updateStatus(getThingStatus());
		}
	}
	
	@Override
	public int getBatteryPower() {
		return batteryPower;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(type);
		out.writeObject(model);
		out.writeObject(name);
		out.writeObject(deviceId);
		out.writeInt(batteryPower);
		out.writeBoolean(powered);
		
		doWriteExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		type = (String)in.readObject();
		model = (String)in.readObject();
		name = (String)in.readObject();
		deviceId = (String)in.readObject();
		batteryPower = in.readInt();
		powered = in.readBoolean();
		
		doReadExternal(in);
		
		if (powered) {
			startBatteryTimer();
		}
		
	}

	@Override
	public void powerOn() {
		if (powered)
			return;
		
		if (batteryTimer == null || !batteryTimer.isWorking())
			startBatteryTimer();
		
		this.powered = true;
		doPowerOn();
		
		getPanel().updateStatus(getThingStatus());
	}

	@Override
	public void powerOff() {
		if (!powered)
			return;
		
		this.powered = false;
		doPowerOff();
		
		stopBatteryTimer();
		
		getPanel().updateStatus(getThingStatus());
	}

	@Override
	public boolean isPowered() {
		return powered;
	}
	
	@Override
	public void reset() {
		deviceId = generateDeviceId();
		doReset();
		
		getPanel().updateStatus(getThingStatus());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!getClass().equals(obj.getClass()))
			return false;
		
		return deviceId.equals(((IThingEmulator)obj).getDeviceId());
	}
	
	@Override
	public void addDeviceListener(IDeviceListener listener) {
		deviceListeners.add(listener);
	}
	
	@Override
	public boolean removeDeviceListener(IDeviceListener listener) {
		return deviceListeners.remove(listener);
	}
	
	@Override
	public String getDeviceType() {
		return type;
	}
	
	@Override
	public String getDeviceModel() {
		return model;
	}
	
	@Override
	public String getDeviceName() {
		return name;
	}

	protected abstract void doWriteExternal(ObjectOutput out) throws IOException;
	protected abstract void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException;
	protected abstract void doPowerOn();
	protected abstract void doPowerOff();
	protected abstract void doReset();
}
