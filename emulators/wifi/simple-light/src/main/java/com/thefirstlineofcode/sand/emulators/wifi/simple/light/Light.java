package com.thefirstlineofcode.sand.emulators.wifi.simple.light;

import java.util.Map;

import com.thefirstlineofcode.chalk.core.AuthFailureException;
import com.thefirstlineofcode.chalk.core.stream.StandardStreamConfig;
import com.thefirstlineofcode.chalk.network.ConnectionException;
import com.thefirstlineofcode.sand.client.actuator.ActuatorPlugin;
import com.thefirstlineofcode.sand.client.core.ThingsUtils;
import com.thefirstlineofcode.sand.client.core.actuator.IActuator;
import com.thefirstlineofcode.sand.client.core.actuator.IExecutor;
import com.thefirstlineofcode.sand.client.core.actuator.IExecutorFactory;
import com.thefirstlineofcode.sand.client.edge.AbstractEdgeThing;
import com.thefirstlineofcode.sand.client.ibdr.RegistrationException;
import com.thefirstlineofcode.sand.client.things.simple.light.FlashExecutor;
import com.thefirstlineofcode.sand.client.things.simple.light.ILight;
import com.thefirstlineofcode.sand.emulators.commons.Constants;
import com.thefirstlineofcode.sand.emulators.commons.ui.LightEmulatorPanel;
import com.thefirstlineofcode.sand.emulators.models.SlWe01ModelDescriptor;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;
import com.thefirstlineofcode.sand.protocols.things.simple.light.Flash;

public class Light extends AbstractEdgeThing implements ILight {
	public static final String THING_TYPE = SlWe01ModelDescriptor.THING_TYPE;
	public static final String THING_MODEL = SlWe01ModelDescriptor.MODEL_NAME;
	
	private static final SwitchState DEFAULT_SWITCH_STATE = SwitchState.OFF;
	private static final LightState DEFAULT_LIGHT_STATE = LightState.OFF;
	
	private SwitchState switchState;
	private LightState lightState;
	
	private LightEmulatorPanel panel;
	
	private IActuator actuator;
	
	public Light(StandardStreamConfig streamConfig) {
		super(THING_TYPE, THING_MODEL);
		
		switchState = DEFAULT_SWITCH_STATE;
		lightState = DEFAULT_LIGHT_STATE;
	}
	
	public void setLightEmulatorPanel(LightEmulatorPanel panel) {
		this.panel = panel;
	}

	@Override
	public String getSoftwareVersion() {
		return Constants.SOFTWARE_VERSION;
	}

	@Override
	public SwitchState getSwitchState() {
		return switchState;
	}

	@Override
	public LightState getLightState() {
		return lightState;
	}
	
	public void changeSwitchState(SwitchState switchState) {
		if (this.switchState == switchState)
			return;
		
		this.switchState = switchState;		
		if (switchState == ILight.SwitchState.ON && lightState == ILight.LightState.OFF) {
			lightState = ILight.LightState.ON;
			try {
				turnOn();
			} catch (ExecutionException e) {
				throw new RuntimeException("Can't turn on light.", e);
			}
		} else if (switchState != ILight.SwitchState.ON && lightState == ILight.LightState.ON) {
			lightState = ILight.LightState.OFF;
			try {
				turnOff();
			} catch (ExecutionException e) {
				throw new RuntimeException("Can't turn off light.", e);
			}
		}
	}

	@Override
	public void turnOn() throws ExecutionException {
		panel.turnOn();
		lightState = ILight.LightState.ON;
	}

	@Override
	public void turnOff() throws ExecutionException {
		panel.turnOff();
		lightState = ILight.LightState.OFF;
	}

	@Override
	public void flash(int repeat) throws ExecutionException {
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
	protected void registrationExceptionOccurred(RegistrationException e) {}

	@Override
	protected void saveAttributes(Map<String, String> attributes) {}

	@Override
	protected void registerChalkPlugins() {
		chatClient.register(ActuatorPlugin.class);
	}

	@Override
	protected void FailedToConnect(ConnectionException e) {}

	@Override
	protected void failedToAuth(AuthFailureException e) {}

	@Override
	protected void startIotComponents() {
		startActuator();
	}
	
	protected void startActuator() {
		if (actuator == null) {
			actuator = createActuator();
		}
		
		actuator.start();
	}

	protected IActuator createActuator() {
		IActuator actuator = chatClient.createApi(IActuator.class);
		actuator.registerExecutorFactory(Flash.class, new IExecutorFactory<Flash>() {
			private IExecutor<Flash> executor = new FlashExecutor(Light.this);
			
			@Override
			public IExecutor<Flash> create() {
				return executor;
			}
			
		});
		
		return actuator;
	}

	@Override
	protected void stopIotComponents() {
		stopActuator();
	}
	
	private void stopActuator() {
		if (actuator != null) {
			actuator.stop();
			actuator = null;
		}
	}

	@Override
	protected void disconnected() {}

	@Override
	protected Map<String, String> loadDeviceAttributes() {
		return null;
	}

	@Override
	protected String generateDeviceId() {
		return getDeviceModel() + "-" + ThingsUtils.generateRandomId(8);
	}
	
	@Override
	protected StandardStreamConfig getStreamConfig(Map<String, String> attributes) {
		return null;
	}

	@Override
	protected DeviceIdentity getIdentity(Map<String, String> attributes) {
		return null;
	}
}
