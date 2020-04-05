package com.firstlinecode.sand.emulators.thing;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.firstlinecode.sand.client.things.BatteryPowerEvent;
import com.firstlinecode.sand.client.things.IThingListener;
import com.firstlinecode.sand.client.things.ThingsUtils;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.emulators.lora.LoraCommunicator;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public abstract class AbstractThingEmulator implements IThingEmulator {
	protected String thingName;
	protected ICommunicator<?, ?, ?> communicator;
	protected DynamicAddressConfigurator addressConfigurator;
	
	protected String deviceId;
	protected String lanId;
	protected String deviceMode;
	protected int batteryPower;
	protected boolean powered;
	protected List<IThingListener> thingListeners;
	
	protected LoraAddress gatewayUplinkAddress;
	protected LoraAddress gatewayDownlinkAddress;
	
	public AbstractThingEmulator(String mode, LoraCommunicator communicator) {
		if (mode == null)
			throw new IllegalArgumentException("Null device mode.");
		
		this.deviceMode = mode;
		this.thingName = getThingName() + " - " + mode;
		this.communicator = communicator;

		deviceId = generateDeviceId();
		batteryPower = 100;
		powered = true;
		
		addressConfigurator = new DynamicAddressConfigurator(deviceId, (LoraCommunicator)communicator);
		
		thingListeners = new ArrayList<>();
		
		BatteryTimer timer = new BatteryTimer();
		timer.start();
	}

	protected String generateDeviceId() {
		return getDeviceMode() + ThingsUtils.generateRandomId(8);
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
		
		sb.append("Battery: ").append(batteryPower).append("%, ");
		
		sb.append("Device ID: ").append(deviceId);
		
		return sb.toString();

	}
	
	private class BatteryTimer {
		private Timer timer = new Timer();
		
		public void start() {
			timer.schedule(new BatteryPowerTimerTask(), 1000 * 10, 1000 * 10);
		}
	}
	
	private class BatteryPowerTimerTask extends TimerTask {
		@Override
		public void run() {
			synchronized (AbstractThingEmulator.this) {
				if (powered) {
					if (batteryPower == 0)
						return;
					
					if (batteryPower != 10) {
						batteryPower -= 2;
					} else {
						batteryPower = 100;
					}
					
					for (IThingListener deviceListener : thingListeners) {
						deviceListener.batteryPowerChanged(new BatteryPowerEvent(AbstractThingEmulator.this, batteryPower));
					}
				}
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
	public String getDeviceMode() {
		return deviceMode;
	}
	
	@Override
	public synchronized void setBatteryPower(int batteryPower) {
		if (batteryPower <= 0 || batteryPower > 100) {
			throw new IllegalArgumentException("Battery power value must be in the range of 0 to 100.");
		}
		this.batteryPower = batteryPower;
	}
	
	@Override
	public int getBatteryPower() {
		return batteryPower;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(deviceMode);
		out.writeObject(deviceId);
		out.writeObject(lanId);
		out.writeInt(batteryPower);
		out.writeBoolean(powered);
		
		doWriteExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		deviceMode = (String)in.readObject();
		deviceId = (String)in.readObject();
		lanId = (String)in.readObject();
		batteryPower = in.readInt();
		powered = in.readBoolean();
		
		doReadExternal(in);
	}
	
	@Override
	public void powerOn() {
		this.powered = true;
		addressConfigurator.introduce();
		
		doPowerOn();
		
		for (IThingEmulatorListener thingEmulatorListener : getThingEmulatorListeners()) {
			thingEmulatorListener.powerChanged(new PowerEvent(this, PowerEvent.Type.POWER_ON));
		}
	}
	
	private List<IThingEmulatorListener> getThingEmulatorListeners() {
		List<IThingEmulatorListener> thingEmulatorListeners = new ArrayList<>();
		for (IThingListener listener : thingListeners) {
			if (listener instanceof IThingEmulatorListener) {
				thingEmulatorListeners.add((IThingEmulatorListener)listener);
			}
		}
		
		return thingEmulatorListeners;
	}

	@Override
	public void powerOff() {
		this.powered = false;
		addressConfigurator.stop();
		
		doPowerOff();
		
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
		lanId = null;
		
		doReset();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!getClass().equals(obj.getClass()))
			return false;
		
		return deviceId.equals(((IThingEmulator)obj).getDeviceId());
	}
	
	@Override
	public void addThingListener(IThingListener listener) {
		thingListeners.add(listener);
	}
	
	@Override
	public boolean removeThingListener(IThingListener listener) {
		return thingListeners.remove(listener);
	}
	
	@Override
	public ICommunicator<?, ?, ?> getCommunicator() {
		return communicator;
	}
	
	@Override
	public void addressConfigured(LoraAddress gatewayUplinkAddress, LoraAddress gatewayDownlinkAddress) {
		this.gatewayUplinkAddress = gatewayUplinkAddress;
		this.gatewayDownlinkAddress = gatewayDownlinkAddress;
	}
	
	protected abstract void doWriteExternal(ObjectOutput out) throws IOException;
	protected abstract void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException;
	protected abstract void doPowerOn();
	protected abstract void doPowerOff();
	protected abstract void doReset();
}
