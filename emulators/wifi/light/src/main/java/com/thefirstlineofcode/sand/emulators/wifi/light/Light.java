package com.thefirstlineofcode.sand.emulators.wifi.light;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import com.thefirstlineofcode.sand.emulators.things.Constants;
import com.thefirstlineofcode.sand.emulators.things.ILight;
import com.thefirstlineofcode.sand.emulators.things.emulators.AbstractThingEmulator;
import com.thefirstlineofcode.sand.emulators.things.emulators.ILightEmulator;
import com.thefirstlineofcode.sand.emulators.things.emulators.ILightStateListener;
import com.thefirstlineofcode.sand.emulators.things.emulators.ISwitchStateListener;
import com.thefirstlineofcode.sand.emulators.things.ui.AbstractThingEmulatorPanel;
import com.thefirstlineofcode.sand.emulators.things.ui.LightEmulatorPanel;
import com.thefirstlineofcode.sand.protocols.actuator.LanActionException;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public class Light extends AbstractThingEmulator implements ILightEmulator {
	private static final long serialVersionUID = 272824587125544136L;
	
	public static final String THING_TYPE = "Light WiFi Emulator";
	public static final String THING_MODEL = "LE02";
	
	private static final SwitchState DEFAULT_SWITCH_STATE = SwitchState.OFF;
	private static final LightState DEFAULT_LIGHT_STATE = LightState.OFF;
	
	private SwitchState switchState;
	private LightState lightState;
	
	private DeviceIdentity deviceIdentity;
	
	private LightEmulatorPanel panel;
	
	private List<ISwitchStateListener> switchStateListeners;
	private List<ILightStateListener> lightStateListeners;
	
	private IBgProcess bgProcess;
	
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
	public void turnOn() throws LanActionException {
		panel.turnOn();
	}
	
	@Override
	public void turnOff() throws LanActionException {
		panel.turnOff();
	}
	
	public void flash(int repeat) throws LanActionException {
		if (!isPowered())
			return;
		
		if (repeat < 0)
			throw new IllegalArgumentException("Repeat must be an positive integer.");
		
		if (repeat == 0)
			repeat = 1;
		
		doFlash(repeat);
	}
	
	private void doFlash(int repeat) {
		new Thread(new FlashRunnable(repeat)).start();
	}

	private class FlashRunnable implements Runnable {
		private int repeat;
		
		public FlashRunnable(int repeat) {
			this.repeat = repeat;
		}

		@Override
		public void run() {
			panel.setFlashButtionEnabled(false);
			
			ILight.LightState oldLightState = lightState;
			if (lightState == ILight.LightState.ON) {
				panel.turnOff();
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (repeat == 1) {
				panel.flash();
			} else {
				for (int i = 0; i < repeat; i++) {
					panel.flash();
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// Ignore
					}
				}
			}
			
			lightState = oldLightState;
			if (lightState == ILight.LightState.ON) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				panel.turnOn();
			}
			
			panel.setFlashButtionEnabled(true);
			
			synchronized (Light.this) {				
				Light.this.notify();
			}
		}			
	}
	
	@Override
	public String getSoftwareVersion() {
		return Constants.SOFTWARE_VERSION;
	}
	
	@Override
	public AbstractThingEmulatorPanel<?> getPanel() {
		if (panel == null) {
			panel = new LightEmulatorPanel(this);
		}
		
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
		if (switchState == SwitchState.ON || lightState == LightState.ON) {
			lightState = LightState.ON;					
			panel.turnOn();
		}
	}

	@Override
	protected void doPowerOff() {
		if (switchState == SwitchState.OFF || lightState == LightState.OFF) {			
			lightState = LightState.OFF;
		}
		panel.turnOff();
	}
	
	@Override
	protected void doReset() {
		throw new RuntimeException(new OperationNotSupportedException("Can't reset WIFI light."));
	}

	@Override
	public void changeSwitchState(SwitchState switchState) {
		if (this.switchState == switchState)
			return;
		
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
		
		SwitchState oldSwitchState = this.switchState;
		this.switchState = switchState;
		notifySwitchStateChanged(oldSwitchState, switchState);
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
