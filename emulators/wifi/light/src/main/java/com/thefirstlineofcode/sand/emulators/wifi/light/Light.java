package com.thefirstlineofcode.sand.emulators.wifi.light;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

import javax.naming.OperationNotSupportedException;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.chalk.core.stream.StandardStreamConfig;
import com.thefirstlineofcode.chalk.core.stream.StreamConfig;
import com.thefirstlineofcode.chalk.network.ConnectionException;
import com.thefirstlineofcode.chalk.network.IConnectionListener;
import com.thefirstlineofcode.sand.client.ibdr.RegistrationException;
import com.thefirstlineofcode.sand.emulators.things.Constants;
import com.thefirstlineofcode.sand.emulators.things.ILight;
import com.thefirstlineofcode.sand.emulators.things.emulators.AbstractThingEmulator;
import com.thefirstlineofcode.sand.emulators.things.emulators.ILightEmulator;
import com.thefirstlineofcode.sand.emulators.things.ui.AbstractThingEmulatorPanel;
import com.thefirstlineofcode.sand.emulators.things.ui.LightEmulatorPanel;
import com.thefirstlineofcode.sand.protocols.actuator.LanActionException;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public class Light extends AbstractThingEmulator implements ILightEmulator, IBgProcessListener {
	private static final long serialVersionUID = 272824587125544136L;
	
	private static final Logger logger = LoggerFactory.getLogger(Light.class);
	
	public static final String THING_TYPE = "Light WiFi Emulator";
	public static final String THING_MODEL = "LE02";
	
	private static final SwitchState DEFAULT_SWITCH_STATE = SwitchState.OFF;
	private static final LightState DEFAULT_LIGHT_STATE = LightState.OFF;
	
	private SwitchState switchState;
	private LightState lightState;
	
	private LightFrame lightFrame;
	
	private StandardStreamConfig streamConfig;
	private DeviceIdentity deviceIdentity;
	
	private LightEmulatorPanel panel;
	
	private IBgProcess bgProcess;
	
	private IConnectionListener internetLogListener;
	
	public Light(LightFrame lightFrame) {
		super(THING_TYPE, THING_MODEL);
		
		this.lightFrame = lightFrame;
		
		deviceId = generateDeviceId();
		switchState = DEFAULT_SWITCH_STATE;
		lightState = DEFAULT_LIGHT_STATE;
	}
	
	public StreamConfig getStreamConfig() {
		return streamConfig;
	}
	
	public void setStreamConfig(StandardStreamConfig streamConfig) {
		this.streamConfig = streamConfig;
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
	
	public String getThingStatus() {
		StringBuilder sb = new StringBuilder();
		if (powered) {
			sb.append("Power On, ");
		} else {
			sb.append("Power Off, ");
		}
		
		if (bgProcess != null && bgProcess.isConnected()) {
			sb.append("Connected, ");
		} else if (deviceIdentity != null) {
			sb.append("Registered, ");			
		} else {
			sb.append("Unregistered, ");			
		}
		
		
		sb.append("Battery: ").append(batteryPower).append("%, ");
		
		sb.append("Device ID: ").append(deviceId);
		
		return sb.toString();

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
		
		if (streamConfig == null)
			throw new IllegalStateException("Null stream config. Please set stream config first.");
		
		if (bgProcess == null) {
			bgProcess = new BgProcess(this, streamConfig);	
		}
		
		bgProcess.addBgProcessListener(this);
		
		if (internetLogListener != null)
			bgProcess.addConnectionListener(internetLogListener);
		
		bgProcess.start();
	}

	@Override
	protected void doPowerOff() {
		if (switchState == SwitchState.OFF || lightState == LightState.OFF) {			
			lightState = LightState.OFF;
		}
		panel.turnOff();
		
		if (bgProcess != null) {			
			bgProcess.stop();
			bgProcess = null;
		}
	}
	
	@Override
	protected void doReset() {
		throw new RuntimeException(new OperationNotSupportedException("Can't reset WIFI light."));
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
	
	public void setInternetLogListener(IConnectionListener internetLogListener) {
		this.internetLogListener = internetLogListener;
		if (bgProcess != null)
			bgProcess.addConnectionListener(internetLogListener);
	}
	
	public IConnectionListener getInternetLogListener() {
		return internetLogListener;
	}
	
	@Override
	public void registered(DeviceIdentity identity) {
		this.deviceIdentity = identity;
		panel.updateStatus();
	}
	
	@Override
	public void registerExceptionOccurred(RegistrationException e) {
		if (logger.isErrorEnabled())
			logger.error("Can't connect to host. Failed to auth.", e);
		
		JOptionPane.showMessageDialog(lightFrame, "Can't register device. Error: " +
				e.getError(), "Registration Error", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void failedToAuth() {
		if (logger.isErrorEnabled())
			logger.error("Can't connect to host. Failed to auth.");
		
		JOptionPane.showMessageDialog(lightFrame, "Can't connect to host. Failed to auth.",
				"Auth Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void FailedToConnect(ConnectionException e) {
		if (logger.isErrorEnabled())
			logger.error("Can't connect to host. Connection exception occurred.", e);
		
		JOptionPane.showMessageDialog(lightFrame, "Can't connect to host. Connection exception occurred.",
				"Connection Exception", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void connected(IChatClient chatClient) {
		if (logger.isInfoEnabled())
			logger.info("Connected to host.");
		
		panel.updateStatus();
	}

	@Override
	public void disconnected() {
		if (logger.isInfoEnabled())
			logger.info("disconnected from host.");
		
		panel.updateStatus();
	}

	@Override
	public void connectionExceptionOccurred(ConnectionException e) {
		if (logger.isErrorEnabled())
			logger.error("Connection exception occurred.", e);
		
		JOptionPane.showMessageDialog(lightFrame, "Connection exception occurred.",
				"Connection Exception", JOptionPane.ERROR_MESSAGE);
	}
}
