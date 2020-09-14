package com.firstlinecode.sand.emulators.wifi.light;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

import com.firstlinecode.sand.emulators.things.NotRemoteControlStateException;
import com.firstlinecode.sand.emulators.things.NotTurnedOffStateException;
import com.firstlinecode.sand.emulators.things.PowerEvent;
import com.firstlinecode.sand.emulators.things.emulators.AbstractThingEmulator;
import com.firstlinecode.sand.emulators.things.emulators.ILightEmulator;
import com.firstlinecode.sand.emulators.things.ui.AbstractThingEmulatorPanel;
import com.firstlinecode.sand.emulators.things.ui.ISwitchStateListener;
import com.firstlinecode.sand.emulators.things.ui.LightEmulatorPanel;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public class Light extends AbstractThingEmulator implements ILightEmulator, ISwitchStateListener {
	public static final String THING_NAME = "WIFI Light Emulator";
	public static final String THING_MODE = "LE02";
	public static final String SOFTWARE_VERSION = "0.1.0.RELEASE";
	
	private static final SwitchState DEFAULT_SWITCH_STATE = SwitchState.OFF;
	private static final LightState DEFAULT_LIGHT_STATE = LightState.OFF;
	
	private SwitchState switchState;
	private LightState lightState;
	
	private DeviceIdentity deviceIdentity;
	
	private LightEmulatorPanel panel;
	
	public Light(String mode) {
		super(mode);
		
		deviceId = generateDeviceId();
		switchState = DEFAULT_SWITCH_STATE;
		lightState = DEFAULT_LIGHT_STATE;
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
	public void turnOn() throws NotRemoteControlStateException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void turnOff() throws NotRemoteControlStateException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void flash() throws NotRemoteControlStateException, NotTurnedOffStateException {
		// TODO Auto-generated method stub
		
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
			panel.setSwitchStateListener(this);
		}
		
		return panel;
	}
	@Override
	public String getThingName() {
		return THING_NAME;
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
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void doPowerOn() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void doPowerOff() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void doReset() {
		// TODO Auto-generated method stub
		 
	}

	@Override
	public void switchStateChanged(SwitchState oldState, SwitchState newState) {
		switchState = newState;
	}
}
