package com.thefirstlineofcode.sand.emulators.wifi.light;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import com.thefirstlineofcode.sand.emulators.things.ILight;
import com.thefirstlineofcode.sand.emulators.things.NotRemoteControlStateException;
import com.thefirstlineofcode.sand.emulators.things.PowerEvent;
import com.thefirstlineofcode.sand.emulators.things.emulators.AbstractThingEmulator;
import com.thefirstlineofcode.sand.emulators.things.emulators.ILightEmulator;
import com.thefirstlineofcode.sand.emulators.things.emulators.ILightStateListener;
import com.thefirstlineofcode.sand.emulators.things.emulators.ISwitchStateListener;
import com.thefirstlineofcode.sand.emulators.things.ui.AbstractThingEmulatorPanel;
import com.thefirstlineofcode.sand.emulators.things.ui.LightEmulatorPanel;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public class Light extends AbstractThingEmulator implements ILightEmulator, ISwitchStateListener {
	private static final long serialVersionUID = 272824587125544136L;
	
	public static final String THING_TYPE = "Light Emulator";
	public static final String THING_MODEL = "LE02";
	public static final String SOFTWARE_VERSION = "0.1.0.RELEASE";
	
	private static final SwitchState DEFAULT_SWITCH_STATE = SwitchState.OFF;
	private static final LightState DEFAULT_LIGHT_STATE = LightState.OFF;
	
	private SwitchState switchState;
	private LightState lightState;
	
	private DeviceIdentity deviceIdentity;
	
	private LightEmulatorPanel panel;
	
	private List<ISwitchStateListener> switchStateListeners;
	private List<ILightStateListener> lightStateListeners;
	
	public Light() {
		super(THING_TYPE, THING_MODEL);
		
		deviceId = generateDeviceId();
		switchState = DEFAULT_SWITCH_STATE;
		lightState = DEFAULT_LIGHT_STATE;
		
		switchStateListeners = new ArrayList<>();
		lightStateListeners = new ArrayList<>();
	}
	
	public DeviceIdentity getDeviceIdentity() {
		return deviceIdentity;
	}

	public void setDeviceIdentity(DeviceIdentity deviceIdentity) {
		this.deviceIdentity = deviceIdentity;
	}
	
	@Override
	public SwitchState getSwitchState() {
		return switchState;
	}
	
	@Override
	public LightState getLightState() {
		return lightState;
	}
	
	@Override
	public void turnOn() {
		if (switchState != SwitchState.CONTROL) {
			new RuntimeException(new NotRemoteControlStateException(switchState)).printStackTrace();
			return;
		}
		
		panel.turnOn();
	}
	
	@Override
	public void turnOff() {
		if (switchState != SwitchState.CONTROL) {
			new NotRemoteControlStateException(switchState).printStackTrace();
			return;
		}
		
		panel.turnOff();
	}
	
	public void flash() {
		if (switchState != SwitchState.CONTROL)
			new NotRemoteControlStateException(switchState).printStackTrace();;
			
		if (lightState != LightState.OFF)
			throw new RuntimeException("Try to flash light when light state is " + lightState + ".");
		
		panel.flash();
	}
	
	@Override
	public String getSoftwareVersion() {
		return SOFTWARE_VERSION;
	}
	
	@Override
	public void powerChanged(PowerEvent event) {
		// TODO Auto-generated method stub
		
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
	public void configure(String key, Object value) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Map<String, Object> getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void doWriteExternal(ObjectOutput out) throws IOException {
		if (deviceIdentity != null) {
			out.writeObject(deviceIdentity);
		} else {
			out.writeObject(null);
		}
		
		out.writeObject(switchState);
		out.writeObject(lightState);
	}
	
	@Override
	protected void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		deviceIdentity = (DeviceIdentity)in.readObject();
		
		switchState = (SwitchState)in.readObject();
		lightState = (LightState)in.readObject();
	}
	
	@Override
	protected void doPowerOn() {
		if (switchState == SwitchState.ON || lightState == LightState.ON)
			panel.turnOn();
	}
	
	@Override
	protected void doPowerOff() {
		if (lightState == LightState.OFF)
			panel.turnOff();
	}
	
	@Override
	protected void doReset() {
		throw new RuntimeException(new OperationNotSupportedException("Can't reset WIFI light."));
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

	@Override
	public void switchStateChanged(SwitchState oldState, SwitchState newState) {
		notifySwitchStateChanged(oldState, newState);
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
}
