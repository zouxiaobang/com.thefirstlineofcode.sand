package com.firstlinecode.sand.emulators.thing;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import com.firstlinecode.gem.protocols.bxmpp.BinaryMessageTypeReader;
import com.firstlinecode.gem.protocols.bxmpp.IdentifyBytes;
import com.firstlinecode.sand.client.things.BatteryPowerEvent;
import com.firstlinecode.sand.client.things.IThingListener;
import com.firstlinecode.sand.client.things.ThingsUtils;
import com.firstlinecode.sand.client.things.commuication.CommunicationException;
import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.obm.IObmFactory;
import com.firstlinecode.sand.client.things.obm.ObmData;
import com.firstlinecode.sand.client.things.obm.ObmFactory;
import com.firstlinecode.sand.emulators.lora.LoraCommunicator;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public abstract class AbstractThingEmulator implements IThingEmulator,
		ICommunicationListener<LoraAddress, LoraAddress, ObmData> {
	private static final String PATTERN_LAN_ID = "%02d";
	
	protected String thingName;
	protected ICommunicator<LoraAddress, LoraAddress, ObmData> communicator;
	protected DynamicAddressConfigurator addressConfigurator;
	
	protected String deviceId;
	protected String mode;
	protected int batteryPower;
	protected boolean powered;
	protected List<IThingListener> thingListeners;
	
	protected LoraAddress gatewayUplinkAddress;
	protected LoraAddress gatewayDownlinkAddress;
	protected LoraAddress thingAddress;
	
	protected String lanId;
	
	protected IObmFactory obmFactory = ObmFactory.createInstance();
	
	protected BinaryMessageTypeReader bMessageTypeReader;
	
	protected boolean isDataReceiving;
	
	@SuppressWarnings("unchecked")
	public AbstractThingEmulator(String mode, ICommunicator<?, ?, ?> communicator) {
		if (mode == null)
			throw new IllegalArgumentException("Null device mode.");
		
		this.mode = mode;
		this.thingName = getThingName() + " - " + mode;
		this.communicator = (ICommunicator<LoraAddress, LoraAddress, ObmData>)communicator;

		deviceId = generateDeviceId();
		batteryPower = 100;
		powered = false;
		
		isDataReceiving = false;
		
		bMessageTypeReader = new BinaryMessageTypeReader(getIdentifyBytesToActionTypes());		
		thingListeners = new ArrayList<>();
		
		BatteryTimer timer = new BatteryTimer();
		timer.start();
	}

	protected String generateDeviceId() {
		return getMode() + ThingsUtils.generateRandomId(8);
	}
	
	public String getThingStatus() {
		StringBuilder sb = new StringBuilder();
		if (powered) {
			sb.append("Power On, ");
		} else {
			sb.append("Power Off, ");
		}
		
		if (!isAddressConfigured()) {
			sb.append("Uncontrolled").append(", ");
		} else {
			sb.append("Controlled: ").append(String.format(PATTERN_LAN_ID, thingAddress.getAddress())).append(", ");
		}
		
		sb.append("Battery: ").append(batteryPower).append("%, ");
		
		sb.append("Device ID: ").append(deviceId);
		
		return sb.toString();

	}
	
	private class BatteryTimer {
		private Timer timer = new Timer(String.format("Thing '%s' Battery Timer", deviceId));
		
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
	public String getMode() {
		return mode;
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
		out.writeObject(mode);
		out.writeObject(deviceId);
		out.writeObject(gatewayUplinkAddress);
		out.writeObject(gatewayDownlinkAddress);
		out.writeObject(thingAddress);
		out.writeInt(batteryPower);
		out.writeBoolean(powered);
		
		doWriteExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		mode = (String)in.readObject();
		deviceId = (String)in.readObject();
		gatewayUplinkAddress = (LoraAddress)in.readObject();
		gatewayDownlinkAddress = (LoraAddress)in.readObject();
		thingAddress = (LoraAddress)in.readObject();
		batteryPower = in.readInt();
		powered = in.readBoolean();
		
		doReadExternal(in);
	}
	
	@Override
	public void powerOn() {
		if (powered)
			return;
		
		this.powered = true;
		if (lanId != null) {
			// Node has added to concentrator. Start to receive data from concentrator.
			startToReceiveData();			
		} else {			
			if (!isAddressConfigured()) {
				if (addressConfigurator == null) {
					addressConfigurator = new DynamicAddressConfigurator(this, (LoraCommunicator)communicator);
				}
				
				addressConfigurator.introduce();
			}
		}
		
		doPowerOn();
		
		for (IThingEmulatorListener thingEmulatorListener : getThingEmulatorListeners()) {
			thingEmulatorListener.powerChanged(new PowerEvent(this, PowerEvent.Type.POWER_ON));
		}
	}
	
	@Override
	public void nodeAdded(String lanId) {
		this.lanId = lanId;
		
		if (isPowered()) {
			startToReceiveData();
		}
	}
	
	@Override
	public void startToReceiveData() {
		if (isDataReceiving)
			return;
		
		communicator.addCommunicationListener(this);
		doStartToReceiveData();
		isDataReceiving = true;
	}
	
	@Override
	public void stopDataReceving() {
		if (!isDataReceiving)
			return;
		
		doStopDataReceiving();
		communicator.removeCommunicationListener(this);
		
		isDataReceiving = false;
	}
	
	protected abstract void doStartToReceiveData();
	protected abstract void doStopDataReceiving();

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
		if (powered == false)
			return;
		
		if (isAddressConfigured() && addressConfigurator != null) {
			addressConfigurator.stop();
			addressConfigurator = null;
		} else {
			stopDataReceving();
		}
		
		this.powered = false;
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
		gatewayUplinkAddress = null;
		gatewayDownlinkAddress = null;
		thingAddress = null;
		
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
	public void addressConfigured(LoraAddress gatewayUplinkAddress, LoraAddress gatewayDownlinkAddress,
			LoraAddress thingAddress) {
		this.gatewayUplinkAddress = gatewayUplinkAddress;
		this.gatewayDownlinkAddress = gatewayDownlinkAddress;
		this.thingAddress = thingAddress;
		
		getPanel().updateStatus(getThingStatus());
	}
	
	@Override
	public boolean isAddressConfigured() {
		return gatewayUplinkAddress != null && gatewayDownlinkAddress != null && thingAddress != null;
	}
	
	@Override
	public void sent(LoraAddress to, ObmData data) {}
	
	@Override
	public void received(LoraAddress from, ObmData data) {
		processReceived(from, data.getBinary());
	}
	
	private void processReceived(LoraAddress from, byte[] data) {
		Class<?> actionType = bMessageTypeReader.readType(data);
		
		if (actionType == null) {
			// TODO action not supported
			throw new RuntimeException("Action not supported.");
		}
		
		try {
			processAction(obmFactory.toObject(actionType, data));
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void occurred(CommunicationException e) {}
	
	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {}
	
	protected abstract void doWriteExternal(ObjectOutput out) throws IOException;
	protected abstract void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException;
	protected abstract void doPowerOn();
	protected abstract void doPowerOff();
	protected abstract void doReset();
	protected abstract Map<IdentifyBytes, Class<?>> getIdentifyBytesToActionTypes();
	protected abstract void processAction(Object action) throws ExecutionException;
}
