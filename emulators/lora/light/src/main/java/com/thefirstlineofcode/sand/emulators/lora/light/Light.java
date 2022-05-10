package com.thefirstlineofcode.sand.emulators.lora.light;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.sand.emulators.commons.ILightEmulator;
import com.thefirstlineofcode.sand.emulators.commons.ui.AbstractThingEmulatorPanel;
import com.thefirstlineofcode.sand.emulators.commons.ui.LightEmulatorPanel;
import com.thefirstlineofcode.sand.emulators.lora.network.LoraCommunicator;
import com.thefirstlineofcode.sand.emulators.lora.things.AbstractLoraThingEmulator;
import com.thefirstlineofcode.sand.emulators.models.Le01ModelDescriptor;
import com.thefirstlineofcode.sand.emulators.things.ILight;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;
import com.thefirstlineofcode.sand.protocols.devices.light.Flash;

public class Light extends AbstractLoraThingEmulator implements ILightEmulator {
	public static final String THING_TYPE = "Light Emulator";
	public static final String THING_MODEL = "LE01";
	public static final String SOFTWARE_VERSION = "0.1.0.RELEASE";
	
	private static final SwitchState DEFAULT_SWITCH_STATE = SwitchState.OFF;
	private static final LightState DEFAULT_LIGHT_STATE = LightState.OFF;
	
	private static final int DATA_RECEIVING_INTERVAL = 1000;
	
	private SwitchState switchState = DEFAULT_SWITCH_STATE;
	private LightState lightState = DEFAULT_LIGHT_STATE;
	
	private LightEmulatorPanel panel;
	
	private Timer dataReceivingTimer;
	
	public Light() {}
	
	public Light(LoraCommunicator communicator) {
		this(communicator, DEFAULT_SWITCH_STATE);
	}
	
	public Light(LoraCommunicator communicator, SwitchState switchState) {
		this(communicator, switchState, (switchState == SwitchState.ON) ? LightState.ON : LightState.OFF);
	}
	
	public Light(LoraCommunicator communicator, SwitchState switchState, LightState lightState) {
		super(THING_TYPE, THING_MODEL, communicator);
		
		if (switchState == null)
			throw new IllegalArgumentException("Null switch state.");
		
		if (lightState == null)
			throw new IllegalArgumentException("Null light state.");
		
		if (switchState == SwitchState.ON && lightState == LightState.OFF ||
				switchState == SwitchState.OFF && lightState == LightState.ON) {
			throw new IllegalStateException(String.format("Invalid light state. Switch state: %s. Light state: %s.", switchState, lightState));
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
	}

	@Override
	protected void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.doReadExternal(in);
		
		lightState = (LightState)in.readObject();
		switchState = (SwitchState)in.readObject();
	}
	
	public SwitchState getSwitchState() {
		return switchState;
	}

	@Override
	public LightState getLightState() {
		return lightState;
	}

	@Override
	public void turnOn() {
		if (!isPowered() || batteryPower == 0)
			return;
		
		panel.turnOn();

	}

	@Override
	public void turnOff() {
		if (!isPowered() || batteryPower == 0)
			return;
		
		panel.turnOff();
	}

	@Override
	public AbstractThingEmulatorPanel<?> getPanel() {
		if (panel == null) {
			panel = new LightEmulatorPanel(this);
			addDeviceListener(panel);
		}
		
		return panel;
	}

	@Override
	protected void doReset() {
		turnOff();
		
		super.doReset();
	}

	@Override
	protected void doPowerOn() {
		super.doPowerOn();
		
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
		
		super.doPowerOff();
	}

	@Override
	protected void doStartToReceiveData() {
		dataReceivingTimer = new Timer(String.format("%s '%s' Data Receiving Timer", getDeviceName(), deviceId));
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
			if (switchState != ILight.SwitchState.CONTROL)
				throw new ExecutionException(ILight.ERROR_CODE_NOT_REMOTE_CONTROL_STATE);
			
			flash(((Flash)action).getRepeat());
		} else {
			throw new RuntimeException(String.format("Unsupported action type: %s.", action.getClass().getName()));
		}
	}
	
	public void flash(int repeat) throws ExecutionException {
		if (!isPowered() || batteryPower == 0)
			return;
		
		if (repeat < 0)
			throw new IllegalArgumentException("Repeat must be an positive integer.");
		
		if (repeat == 0)
			repeat = 1;
		
		doFlash(repeat);
	}
	
	private void doFlash(int repeat) {
		new Thread(new FlashRunnable(repeat)).start();
		
		try {
			synchronized(this) {					
				wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	protected Map<Protocol, Class<?>> createSupportedActions() {
		return new Le01ModelDescriptor().getSupportedActions();
	}
	
	@Override
	public void changeSwitchState(SwitchState switchState) {
		if (this.switchState == switchState)
			return;
		
		this.switchState = switchState;
		if (switchState == ILight.SwitchState.ON && lightState == ILight.LightState.OFF) {
			lightState = ILight.LightState.ON;			
			panel.turnOn();
		} else if (switchState != ILight.SwitchState.ON && lightState == ILight.LightState.ON) {
			lightState = ILight.LightState.OFF;
			panel.turnOff();
		}		
	}

	@Override
	protected void loadDeviceAttributes() {}

	@Override
	protected void saveDeviceId(String deviceId) {}
}
