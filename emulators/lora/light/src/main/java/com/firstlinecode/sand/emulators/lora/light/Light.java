package com.firstlinecode.sand.emulators.lora.light;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.client.lora.ILoraChip;
import com.firstlinecode.sand.client.lora.ILoraChip.PowerType;
import com.firstlinecode.sand.emulators.lora.network.LoraChipCreationParams;
import com.firstlinecode.sand.emulators.lora.network.LoraCommunicator;
import com.firstlinecode.sand.emulators.lora.network.LoraCommunicatorFactory;
import com.firstlinecode.sand.emulators.lora.things.AbstractLoraThingEmulator;
import com.firstlinecode.sand.emulators.models.Le01ModelDescriptor;
import com.firstlinecode.sand.emulators.things.ILight;
import com.firstlinecode.sand.emulators.things.NotRemoteControlStateException;
import com.firstlinecode.sand.emulators.things.NotTurnedOffStateException;
import com.firstlinecode.sand.emulators.things.PowerEvent;
import com.firstlinecode.sand.emulators.things.emulators.ILightEmulator;
import com.firstlinecode.sand.emulators.things.emulators.ILightStateListener;
import com.firstlinecode.sand.emulators.things.emulators.ISwitchStateListener;
import com.firstlinecode.sand.emulators.things.ui.AbstractThingEmulatorPanel;
import com.firstlinecode.sand.emulators.things.ui.LightEmulatorPanel;
import com.firstlinecode.sand.protocols.devices.light.Flash;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class Light extends AbstractLoraThingEmulator implements ILightEmulator, ISwitchStateListener {
	public static final String THING_NAME = "Light Emulator";
	public static final String THING_MODEL = "LE01";
	public static final String SOFTWARE_VERSION = "0.1.0.RELEASE";
	
	private static final SwitchState DEFAULT_SWITCH_STATE = SwitchState.OFF;
	private static final LightState DEFAULT_LIGHT_STATE = LightState.OFF;
	
	private static final int DATA_RECEIVING_INTERVAL = 1000;
	
	private SwitchState switchState = DEFAULT_SWITCH_STATE;
	private LightState lightState = DEFAULT_LIGHT_STATE;
	
	private LightEmulatorPanel panel;
	
	private Timer dataReceivingTimer;
	
	private List<ISwitchStateListener> switchStateListeners = new ArrayList<>();
	private List<ILightStateListener> lightStateListeners= new ArrayList<>();
	
	private Light() {}
	
	private Light(LoraCommunicator communicator) {
		this(communicator, DEFAULT_SWITCH_STATE, DEFAULT_LIGHT_STATE);
	}
	
	private Light(LoraCommunicator communicator, SwitchState switchState) {
		this(communicator, switchState, switchState == SwitchState.ON ? LightState.ON : LightState.OFF);
	}
	
	private Light(LoraCommunicator communicator, SwitchState switchState, LightState lightState) {
		super(THING_MODEL, communicator);
		
		if (switchState == null)
			throw new IllegalArgumentException("Null switch state.");
		
		if (lightState == null)
			throw new IllegalArgumentException("Null light state.");
		
		if (switchState == SwitchState.ON && lightState == LightState.OFF ||
				switchState == SwitchState.OFF && lightState == LightState.ON) {
			throw new IllegalStateException(String.format("Invalid light states. Switch state: %s. Light state: %s.", switchState, lightState));
		}
		
		this.switchState = switchState;
		this.lightState = lightState;
	}
	
	@Override
	public String getSoftwareVersion() {
		return SOFTWARE_VERSION;
	}

	@Override
	protected void doWriteExternal(ObjectOutput out) throws IOException {
		super.doWriteExternal(out);
		
		out.writeObject(lightState);
		out.writeObject(switchState);
		
		ILoraChip chip = ((LoraCommunicator)communicator).getChip();
		out.writeObject(chip.getPowerType());
		
		LoraAddress loraAddress = chip.getAddress();
		out.writeLong(loraAddress.getAddress());
		out.writeInt(loraAddress.getFrequencyBand());
	}

	@Override
	protected void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.doReadExternal(in);
		
		lightState = (LightState)in.readObject();
		switchState = (SwitchState)in.readObject();
		
		PowerType powerType = (PowerType)in.readObject();
		LoraAddress address = LoraAddress.create(in.readLong(), in.readInt());
		
		communicator = (LoraCommunicator)LoraCommunicatorFactory.getInstance().createCommunicator(
				new LoraChipCreationParams(powerType, address));
	}
	
	public SwitchState getSwitchState() {
		return switchState;
	}

	@Override
	public LightState getLightState() {
		return lightState;
	}

	@Override
	public void turnOn() throws NotRemoteControlStateException {
		if (switchState != SwitchState.CONTROL)
			throw new NotRemoteControlStateException(switchState);
		
		doTurnOn();
	}

	private void doTurnOn() {
		switchState = SwitchState.ON;
		panel.turnOn();
	}

	@Override
	public void turnOff() throws NotRemoteControlStateException {
		if (switchState != SwitchState.CONTROL)
			throw new NotRemoteControlStateException(switchState);
		
		doTurnOff();
	}

	private void doTurnOff() {
		panel.turnOff();
	}

	@Override
	public void flash() throws NotRemoteControlStateException, NotTurnedOffStateException {
		if (switchState != SwitchState.CONTROL)
			throw new NotRemoteControlStateException(switchState);
		
		if (lightState != LightState.OFF)
			throw new NotTurnedOffStateException();
		
		panel.flash();
	}

	@Override
	public AbstractThingEmulatorPanel<?> getPanel() {
		if (panel == null) {
			panel = new LightEmulatorPanel(this);
		}
		panel.setSwitchStateListener(this);
		
		return panel;
	}

	@Override
	protected void doReset() {
		doTurnOff();
		
		super.doReset();
	}

	@Override
	protected void doPowerOn() {
		if (switchState == SwitchState.ON || lightState == LightState.ON) {
			lightState = LightState.ON;					
			panel.turnOn();
		}
		
		super.doPowerOn();
	}

	@Override
	protected void doPowerOff() {
		if (lightState == LightState.OFF)
			panel.turnOff();
		
		super.doPowerOff();
	}

	@Override
	public void powerChanged(PowerEvent event) {
		panel.refreshFlashButtionStatus();
	}

	@Override
	public void configure(String key, Object value) {}

	@Override
	public Map<String, Object> getConfiguration() {
		return Collections.emptyMap();
	}

	@Override
	public String getThingName() {
		return THING_NAME;
	}

	@Override
	protected void doStartToReceiveData() {
		dataReceivingTimer = new Timer(String.format("%s '%s' Data Receiving Timer", getThingName(), deviceId));
		dataReceivingTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				((LoraCommunicator)communicator).receive();
			}
		}, 100, DATA_RECEIVING_INTERVAL);
	}

	@Override
	protected void doStopDataReceiving() {
		if (dataReceivingTimer != null) {
			dataReceivingTimer.cancel();
			dataReceivingTimer = null;
		}
	}

	@Override
	protected void processAction(Object action) throws ExecutionException {
		if (action instanceof Flash) {
			executeFlash((Flash)action);
		} else {
			throw new ExecutionException(new IllegalArgumentException(String.format("Unsupported action type: %s", action.getClass())));
		}
	}

	private void executeFlash(Flash flash) {
		int repeat = flash.getRepeat();
		if (repeat == 0)
			repeat = 1;
		
		if (repeat == 1) {
			executeFlash();
		} else {
			for (int i = 0; i < repeat; i++) {
				executeFlash();
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// Ignore
				}
			}
		}
	}

	private void executeFlash() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					flash();
				} catch (NotRemoteControlStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotTurnedOffStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	protected Map<Protocol, Class<?>> createSupportedActions() {
		return new Le01ModelDescriptor().getSupportedActions();
	}
	
	@Override
	public boolean changeSwitchState(SwitchState switchState) {
		if (this.switchState == switchState)
			return false;
		
		this.switchState = switchState;
		LightState oldLightState = lightState;
		if (switchState == ILight.SwitchState.ON && lightState == ILight.LightState.OFF) {
			lightState = ILight.LightState.ON;			
			panel.turnOn();
		} else if (switchState != ILight.SwitchState.ON && lightState == ILight.LightState.ON) {
			lightState = ILight.LightState.OFF;
			panel.turnOff();
		}
		
		if (oldLightState != lightState)
			notifyLightStateChanged(oldLightState, lightState);
		
		return true;
	}

	@Override
	public void addSwitchStateListener(ISwitchStateListener switchStateListener) {
		if (!switchStateListeners.contains(switchStateListener))
			switchStateListeners.add(switchStateListener);
	}

	@Override
	public boolean removeSwitchStateListener(ISwitchStateListener switchStateListener) {
		return switchStateListeners.remove(switchStateListener);
	}

	@Override
	public void addLightStateChangeListener(ILightStateListener lightStateListener) {
		if (!lightStateListeners.contains(lightStateListener))
			lightStateListeners.add(lightStateListener);
	}

	@Override
	public boolean removeLightStateListener(ILightStateListener lightStateListener) {
		return lightStateListeners.remove(lightStateListener);
	}
	
	@Override
	public void switchStateChanged(SwitchState oldState, SwitchState newState) {
		notifySwitchStateChanged(oldState, newState);
	}
	
	private void notifySwitchStateChanged(SwitchState oldState, SwitchState newState) {
		for (ISwitchStateListener listener : switchStateListeners) {
			listener.switchStateChanged(oldState, newState);
		}
	}
	
	private void notifyLightStateChanged(LightState oldState, LightState newState) {
		for (ILightStateListener listener : lightStateListeners) {
			listener.lightStateChanged(oldState, newState);
		}
	}
	
	public static Light createInstance(LoraCommunicator communicator) {
		return new Light(communicator);
	}
}
