package com.thefirstlineofcode.sand.emulators.commons;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Timer;
import java.util.TimerTask;

import com.thefirstlineofcode.sand.client.things.AbstractDevice;
import com.thefirstlineofcode.sand.client.things.BatteryPowerEvent;
import com.thefirstlineofcode.sand.client.things.IDeviceListener;
import com.thefirstlineofcode.sand.client.things.ThingsUtils;

public abstract class AbstractThingEmulator extends AbstractDevice implements IThingEmulator, Externalizable {
	private static final long serialVersionUID = 5777576412420781910L;

	private static final int BATTERY_POWER_DOWN_INTERVAL = 1000 * 10;
	
	protected BatteryTimer batteryTimer;
	
	public AbstractThingEmulator() {}
	
	public AbstractThingEmulator(String type, String model) {
		super(type, model);
		
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
		if (isPowered() || batteryPower != 0) {
			sb.append("Power On, ");
		} else {
			sb.append("Power Off, ");
		}
		
		sb.append("Battery: ").append(getBatteryPower()).append("%, ");
		
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
					for (IDeviceListener listener : listeners) {
						listener.batteryPowerChanged(new BatteryPowerEvent(AbstractThingEmulator.this, batteryPower));
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
		
		this.powered = true;
		doPowerOn();
		
		if (batteryTimer == null || !batteryTimer.isWorking())
			startBatteryTimer();
		
		getPanel().updateStatus(getThingStatus());
	}

	@Override
	public void powerOff() {
		if (!powered)
			return;
		
		stopBatteryTimer();
		
		doPowerOff();
		this.powered = false;
		
		getPanel().updateStatus(getThingStatus());
	}

	@Override
	public boolean isPowered() {
		return powered && batteryPower != 0;
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

	protected abstract void doWriteExternal(ObjectOutput out) throws IOException;
	protected abstract void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException;
	protected abstract void doPowerOn();
	protected abstract void doPowerOff();
	protected abstract void doReset();
}
