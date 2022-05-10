package com.thefirstlineofcode.sand.emulators.wifi.light;

import java.awt.Window;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.naming.OperationNotSupportedException;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.chalk.core.AuthFailureException;
import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.chalk.core.stream.StandardStreamConfig;
import com.thefirstlineofcode.chalk.core.stream.StreamConfig;
import com.thefirstlineofcode.chalk.network.ConnectionException;
import com.thefirstlineofcode.chalk.network.IConnectionListener;
import com.thefirstlineofcode.sand.client.edge.IEdgeThingListener;
import com.thefirstlineofcode.sand.client.ibdr.RegistrationException;
import com.thefirstlineofcode.sand.emulators.commons.AbstractThingEmulator;
import com.thefirstlineofcode.sand.emulators.commons.Constants;
import com.thefirstlineofcode.sand.emulators.commons.ILightEmulator;
import com.thefirstlineofcode.sand.emulators.commons.ui.AbstractThingEmulatorPanel;
import com.thefirstlineofcode.sand.emulators.commons.ui.LightEmulatorPanel;
import com.thefirstlineofcode.sand.emulators.commons.ui.UiUtils;
import com.thefirstlineofcode.sand.emulators.things.ILight;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public class LightEmulator extends AbstractThingEmulator implements ILightEmulator, IEdgeThingListener {
	private static final long serialVersionUID = 272824587125544136L;
	
	public static final String THING_TYPE = "Light WiFi Emulator";
	public static final String THING_MODEL = "LE02";
	
	private static final Logger logger = LoggerFactory.getLogger(LightEmulator.class);
	
	private Window mainWindow;
	private LightEmulatorPanel panel;
	private Light light;
	
	private IConnectionListener internetLogListener;
	
	public LightEmulator(Window mainWindow, StandardStreamConfig streamConfig) {
		super(THING_TYPE, THING_MODEL);
		
		this.mainWindow = mainWindow;
		light = new Light(streamConfig);
		light.setLightEmulatorPanel((LightEmulatorPanel)getPanel());
		
		deviceId = light.getDeviceId();
	}
	
	public StreamConfig getStreamConfig() {
		return light.getStreamConfig();
	}
	
	@Override
	public String getDeviceId() {
		return light.getDeviceId();
	}
	
	@Override
	public SwitchState getSwitchState() {
		return light.getSwitchState();
	}
	
	@Override
	public LightState getLightState() {
		return light.getLightState();
	}
	
	@Override
	public void turnOn() throws ExecutionException {
		if (!isPowered())
			return;
		
		light.turnOn();
	}
	
	@Override
	public void turnOff() throws ExecutionException {
		if (!isPowered())
			return;
		
		light.turnOn();
	}
	
	public void flash(int repeat) throws ExecutionException {
		if (!isPowered())
			return;
		
		light.flash(repeat);
	}
	
	@Override
	public String getSoftwareVersion() {
		return Constants.SOFTWARE_VERSION;
	}
	
	@Override
	public AbstractThingEmulatorPanel<?> getPanel() {
		if (panel == null) {
			panel = new LightEmulatorPanel(this);
			addDeviceListener(panel);
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
		
		if (light.isConnected()) {
			sb.append("Connected, ");
		} else if (light.isRegistered()) {
			sb.append("Registered, ");			
		} else {
			sb.append("Unregistered, ");			
		}
		
		sb.append("Battery: ").append(getBatteryPower()).append("%, ");
		
		sb.append("Device ID: ").append(getDeviceId());
		
		return sb.toString();

	}
	
	@Override
	protected void doWriteExternal(ObjectOutput out) throws IOException {}
	
	@Override
	protected void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException {}
	
	@Override
	protected void doPowerOn() {
		light.addEdgeThingListener(this);
		
		if (light.getSwitchState() == SwitchState.ON || light.getLightState() == LightState.ON) {
			try {
				light.turnOn();
			} catch (ExecutionException e) {
				throw new RuntimeException("Can't turn on light.", e);
			}
		}
		
		if (internetLogListener != null)
			light.addConnectionListener(internetLogListener);
		
		light.start();
	}

	@Override
	protected void doPowerOff() {
		if (light.getLightState() == ILight.LightState.ON) {
			try {
				light.turnOff();
			} catch (ExecutionException e) {
				throw new RuntimeException("Can't turn off light.", e);
			}
		}
		
		light.stop();
	}
	
	@Override
	protected void doReset() {
		throw new RuntimeException(new OperationNotSupportedException("Can't reset WIFI light."));
	}
	
	@Override
	public void changeSwitchState(SwitchState switchState) {
		light.changeSwitchState(switchState);
	}
	
	public void setInternetLogListener(IConnectionListener internetLogListener) {
		this.internetLogListener = internetLogListener;
		if (light != null)
			light.addConnectionListener(internetLogListener);
	}
	
	public IConnectionListener getInternetLogListener() {
		return internetLogListener;
	}
	
	@Override
	public void registered(DeviceIdentity identity) {
		if (logger.isInfoEnabled())
			logger.info("Registered to host.");
		
		UiUtils.showNotification(mainWindow, "Message", "Light has registered on the host.");
		
		panel.updateStatus();
	}
	
	@Override
	public void registerExceptionOccurred(RegistrationException e) {
		if (logger.isErrorEnabled())
			logger.error("Can't connect to host. Failed to auth.", e);
		
		JOptionPane.showMessageDialog(mainWindow, "Can't register device. Error: " +
				e.getError(), "Registration Error", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void failedToAuth(AuthFailureException e) {
		if (logger.isErrorEnabled())
			logger.error("Can't connect to host. Failed to auth.");
		
		JOptionPane.showMessageDialog(mainWindow, "Can't connect to host. Failed to auth",
				"Auth Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void FailedToConnect(ConnectionException e) {
		if (logger.isErrorEnabled())
			logger.error("Can't connect to host. Connection exception occurred.", e);
		
		JOptionPane.showMessageDialog(mainWindow, "Can't connect to host. Connection exception occurred.",
				"Connection Exception", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void connected(IChatClient chatClient) {
		if (logger.isInfoEnabled())
			logger.info("Connected to host.");
		
		UiUtils.showNotification(mainWindow, "Message", "Light has connected.");
		
		panel.updateStatus();
	}

	@Override
	public void disconnected() {
		if (logger.isInfoEnabled())
			logger.info("disconnected from host.");
		
		UiUtils.showNotification(mainWindow, "Message", "Light has disconnected.");
		
		panel.updateStatus();
	}

	@Override
	public void connectionExceptionOccurred(ConnectionException e) {
		if (logger.isErrorEnabled())
			logger.error("Connection exception occurred.", e);
		
		JOptionPane.showMessageDialog(mainWindow, "Connection exception occurred.",
				"Connection Exception", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	protected void loadDeviceAttributes() {}
	
	@Override
	protected void saveDeviceId(String deviceId) {}
}
