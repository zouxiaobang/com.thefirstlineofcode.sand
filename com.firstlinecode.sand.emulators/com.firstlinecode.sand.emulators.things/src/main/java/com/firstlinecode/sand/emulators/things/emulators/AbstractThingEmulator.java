package com.firstlinecode.sand.emulators.things.emulators;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.firstlinecode.sand.client.things.BatteryPowerEvent;
import com.firstlinecode.sand.client.things.IDeviceListener;
import com.firstlinecode.sand.client.things.ThingsUtils;
import com.firstlinecode.sand.emulators.things.PowerEvent;

public abstract class AbstractThingEmulator implements IThingEmulator {
	private static final int BATTERY_POWER_DOWN_INTERVAL = 1000 * 10;
	
	protected String thingName;
	
	protected String deviceId;
	protected String model;
	protected int batteryPower;
	protected boolean powered;
	protected List<IDeviceListener> deviceListeners;
	
	protected BatteryTimer batteryTimer;
	
	public AbstractThingEmulator(String model) {
		if (model == null)
			throw new IllegalArgumentException("Null device model.");
		
		this.model = model;
		this.thingName = getThingName() + " - " + model;
		
		deviceId = generateDeviceId();
		batteryPower = 100;
		powered = false;
		
		deviceListeners = new ArrayList<>();
	}

	protected void startBatteryTimer() {
		if (batteryTimer == null)
			batteryTimer = new BatteryTimer(getThingName(), deviceId);
		
		batteryTimer.start();
	}
	
	protected void stopBatteryTimer() {
		batteryTimer.stop();
		batteryTimer = null;
	}

	protected String generateDeviceId() {
		return getModel() + ThingsUtils.generateRandomId(8);
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
	public String getModel() {
		return model;
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
		out.writeObject(model);
		out.writeObject(deviceId);
		out.writeInt(batteryPower);
		out.writeBoolean(powered);
		
		doWriteExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		model = (String)in.readObject();
		deviceId = (String)in.readObject();
		batteryPower = in.readInt();
		powered = in.readBoolean();
		
		doReadExternal(in);
	}
	
	@Override
	public void powerOn() {
		if (powered)
			return;
		
		this.powered = true;
		doPowerOn();
		
		startBatteryTimer();
		getPanel().updateStatus(getThingStatus());
		
		for (IThingEmulatorListener thingEmulatorListener : getThingEmulatorListeners()) {
			thingEmulatorListener.powerChanged(new PowerEvent(this, PowerEvent.Type.POWER_ON));
		}
	}

	private List<IThingEmulatorListener> getThingEmulatorListeners() {
		List<IThingEmulatorListener> thingEmulatorListeners = new ArrayList<>();
		for (IDeviceListener listener : deviceListeners) {
			if (listener instanceof IThingEmulatorListener) {
				thingEmulatorListeners.add((IThingEmulatorListener)listener);
			}
		}
		
		return thingEmulatorListeners;
	}

	@Override
	public void powerOff() {
		if (powered == false)
			return;
		
		this.powered = false;
		doPowerOff();
		
		stopBatteryTimer();
		
		getPanel().updateStatus(getThingStatus());
		
		for (IThingEmulatorListener thingEmulatorListener : getThingEmulatorListeners()) {
			thingEmulatorListener.powerChanged(new PowerEvent(this, PowerEvent.Type.POWER_OFF));
		}
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

	protected abstract void doWriteExternal(ObjectOutput out) throws IOException;
	protected abstract void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException;
	protected abstract void doPowerOn();
	protected abstract void doPowerOff();
	protected abstract void doReset();
}
