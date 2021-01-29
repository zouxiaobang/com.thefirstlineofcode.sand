package com.firstlinecode.sand.emulators.lora.gateway;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FontUIResource;

import com.firstlinecode.basalt.protocol.core.stanza.error.StanzaError;
import com.firstlinecode.chalk.core.AuthFailureException;
import com.firstlinecode.chalk.core.IChatClient;
import com.firstlinecode.chalk.core.StandardChatClient;
import com.firstlinecode.chalk.core.stream.StandardStreamConfig;
import com.firstlinecode.chalk.core.stream.UsernamePasswordToken;
import com.firstlinecode.chalk.network.ConnectionException;
import com.firstlinecode.chalk.network.IConnectionListener;
import com.firstlinecode.sand.client.actuator.ActuatorPlugin;
import com.firstlinecode.sand.client.concentrator.ConcentratorPlugin;
import com.firstlinecode.sand.client.dmr.IModelRegistrar;
import com.firstlinecode.sand.client.ibdr.IRegistration;
import com.firstlinecode.sand.client.ibdr.IbdrPlugin;
import com.firstlinecode.sand.client.ibdr.RegistrationException;
import com.firstlinecode.sand.client.lora.DynamicAddressConfigurator;
import com.firstlinecode.sand.client.lora.IDualLoraChipsCommunicator;
import com.firstlinecode.sand.client.things.IDeviceListener;
import com.firstlinecode.sand.client.things.ThingsUtils;
import com.firstlinecode.sand.client.things.autuator.IActuator;
import com.firstlinecode.sand.client.things.commuication.ParamsMap;
import com.firstlinecode.sand.client.things.concentrator.IConcentrator;
import com.firstlinecode.sand.client.things.concentrator.IConcentrator.LanError;
import com.firstlinecode.sand.client.things.concentrator.Node;
import com.firstlinecode.sand.emulators.lora.gateway.log.LogConsolesDialog;
import com.firstlinecode.sand.emulators.lora.gateway.things.DeviceIdentityInfo;
import com.firstlinecode.sand.emulators.lora.gateway.things.ThingInfo;
import com.firstlinecode.sand.emulators.lora.gateway.things.ThingInternalFrame;
import com.firstlinecode.sand.emulators.lora.network.ILoraNetwork;
import com.firstlinecode.sand.emulators.lora.network.LoraCommunicator;
import com.firstlinecode.sand.emulators.lora.things.AbstractLoraThingEmulator;
import com.firstlinecode.sand.emulators.lora.things.AbstractLoraThingEmulatorFactory;
import com.firstlinecode.sand.emulators.models.Ge01ModelDescriptor;
import com.firstlinecode.sand.emulators.models.Le01ModelDescriptor;
import com.firstlinecode.sand.emulators.things.Constants;
import com.firstlinecode.sand.emulators.things.IGateway;
import com.firstlinecode.sand.emulators.things.UiUtils;
import com.firstlinecode.sand.emulators.things.emulators.IThingEmulator;
import com.firstlinecode.sand.emulators.things.emulators.IThingEmulatorFactory;
import com.firstlinecode.sand.emulators.things.emulators.StreamConfigInfo;
import com.firstlinecode.sand.emulators.things.ui.AboutDialog;
import com.firstlinecode.sand.emulators.things.ui.AbstractThingEmulatorPanel;
import com.firstlinecode.sand.emulators.things.ui.StatusBar;
import com.firstlinecode.sand.emulators.things.ui.StreamConfigDialog;
import com.firstlinecode.sand.protocols.concentrator.NodeAddress;
import com.firstlinecode.sand.protocols.core.CommunicationNet;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.protocols.core.ModelDescriptor;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class Gateway<C, P extends ParamsMap> extends JFrame implements ActionListener, InternalFrameListener,
		ComponentListener, WindowListener, IGateway, IConnectionListener, DynamicAddressConfigurator.Listener,
		IConcentrator.Listener {
	private static final long serialVersionUID = -7894418812878036627L;
	
	private static final String THING_NAME = "Lora Gateway Emulator";
	
	private static final String DEFAULT_GATEWAY_LAN_ID = "00";
	private static final int ALWAYS_FULL_POWER = 100;
	private static final String THING_MODEL = "GE01";
	
	// File Menu
	private static final String MENU_TEXT_FILE = "File";
	private static final String MENU_NAME_FILE = "file";
	private static final String MENU_ITEM_TEXT_NEW = "New";
	private static final String MENU_ITEM_NAME_NEW = "new";
	private static final String MENU_ITEM_TEXT_OPEN_FILE = "Open file...";
	private static final String MENU_ITEM_NAME_OPEN_FILE = "open_file";
	private static final String MENU_ITEM_TEXT_SAVE = "Save";
	private static final String MENU_ITEM_NAME_SAVE = "save";
	private static final String MENU_ITEM_TEXT_SAVE_AS = "Save As...";
	private static final String MENU_ITEM_NAME_SAVE_AS = "save_as";
	private static final String MENU_ITEM_NAME_QUIT = "quit";
	private static final String MENU_ITEM_TEXT_QUIT = "Quit";
	
	// Edit Menu
	private static final String MENU_TEXT_EDIT = "Edit";
	private static final String MENU_NAME_EDIT = "edit";
	private static final String MENU_ITEM_TEXT_POWER_ON = "Power On";
	private static final String MENU_ITEM_NAME_POWER_ON = "power_on";
	private static final String MENU_ITEM_TEXT_POWER_OFF = "Power Off";
	private static final String MENU_ITEM_NAME_POWER_OFF = "power_off";
	private static final String MENU_ITEM_TEXT_RESET = "Reset";
	private static final String MENU_ITEM_NAME_RESET = "reset";
	private static final String MENU_ITEM_TEXT_DELETE = "Delete";
	private static final String MENU_ITEM_NAME_DELETE = "delete";
	
	// Tools Menu
	private static final String MENU_TEXT_TOOLS = "Tools";
	private static final String MENU_NAME_TOOLS = "tools";
	private static final String MENU_ITEM_TEXT_REGISTER = "Register";
	private static final String MENU_ITEM_NAME_REGISTER = "register";
	private static final String MENU_ITEM_TEXT_CONNECT = "Connect";
	private static final String MENU_ITEM_NAME_CONNECT = "connect";
	private static final String MENU_ITEM_TEXT_DISCONNECT = "Disconnect";
	private static final String MENU_ITEM_NAME_DISCONNECT = "disconnect";
	
	private static final String MENU_ITEM_NAME_ADDRESS_CONFIGURATION_MODE = "address_configuration_mode";
	private static final String MENU_ITEM_TEXT_ADDRESS_CONFIGURATION_MODE = "Address Configuration Mode";
	private static final String MENU_ITEM_NAME_WORKING_MODE = "working_mode";
	private static final String MENU_ITEM_TEXT_WORKING_MODE = "Working Mode";
	private static final String MENU_ITEM_NAME_RECONFIGURE_ADDRESS = "reconfigure_address";
	private static final String MENU_ITEM_TEXT_RECONFIGURE_ADDRESS = "Reconfigure Address";
	
	private static final String MENU_ITEM_TEXT_SHOW_LOG_CONSOLE = "Show Log Console";
	private static final String MENU_ITEM_NAME_SHOW_LOG_CONSOLE = "show_log_console";
	
	// Help Menu
	private static final String MENU_TEXT_HELP = "Help";
	private static final String MENU_NAME_HELP = "help";
	private static final String MENU_ITEM_TEXT_ABOUT = "About";
	private static final String MENU_ITEM_NAME_ABOUT = "about";
	
	private static final String RESOURCE_NAME_GATEWAY = "00";
	
	private String deviceId;
	private DeviceIdentity deviceIdentity;
	private StandardStreamConfig streamConfig;
	
	private List<AbstractLoraThingEmulatorFactory<?>> thingFactories;
	private Map<String, List<AbstractLoraThingEmulator>> allThings;
	private Map<String, Node> nodes;
	private boolean dirty;
	
	private ILoraNetwork network;
	private IDualLoraChipsCommunicator gatewayCommunicator;
	
	private JDesktopPane desktop;
	private JMenuBar menuBar;
	private StatusBar statusBar;
	private LogConsolesDialog logConsolesDialog;
	
	private File configFile;
	
	private IChatClient chatClient;
	private boolean autoReconnect;
	
	private DynamicAddressConfigurator addressConfigurator;
	private IConcentrator concentrator;
	private Map<String, ModelDescriptor> registeredModels;
	
	public Gateway(ILoraNetwork network, IDualLoraChipsCommunicator gatewayCommunicator) {
		super(THING_NAME);
		
		this.network = network;
		this.gatewayCommunicator = gatewayCommunicator;
		
		deviceId = generateDeviceId();
		
		thingFactories = new ArrayList<>();
		allThings = new HashMap<>();
		nodes = new HashMap<>();
		dirty = false;
		autoReconnect = false;
		registerModels();
		
		new Thread(new AutoReconnectThread(), "Gateway Auto Reconnect Thread").start();
		
		setupUi();
	}

	protected String generateDeviceId() {
		return getModel() + ThingsUtils.generateRandomId(8);
	}
	
	private class AutoReconnectThread implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (autoReconnect && (chatClient == null || !chatClient.isConnected())) {
					connect(false);
				}
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private IConnectionListener getLogConsoleInternetConnectionListener() {
		IConnectionListener listener = null;
		
		if (logConsolesDialog == null)
			return null;
		
		listener = logConsolesDialog.getInternetConnectionListener();
		if (listener != null)
			return (IConnectionListener)listener;
		
		return null;
	}
	
	private void setupUi() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		setDefaultUiFont(new javax.swing.plaf.FontUIResource("Serif", Font.PLAIN, 20));
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width - 1024) / 2, (screenSize.height - 768) / 2, 1024, 768);
		
		desktop = new JDesktopPane();
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		add(desktop, BorderLayout.CENTER);
		
		menuBar = createMenuBar();
		setJMenuBar(menuBar);
		
		statusBar = new StatusBar(this);
		add(statusBar, BorderLayout.SOUTH);
		
		updateStatus();
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
	}

	private void updateStatus() {
		statusBar.setText(getStatus());
	}
	
	private void setDefaultUiFont(FontUIResource fur) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get (key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put (key, fur);
		}
	}
	
	public void setDeviceId(String deviceId) {
		if (deviceId == null)
			throw new IllegalArgumentException("Null device id.");
		
		this.deviceId = deviceId;
	}
	
	private String getStatus() {
		StringBuilder sb = new StringBuilder();
		if (deviceIdentity == null) {
			sb.append("Unregistered").append(", ");
		} else {
			sb.append("Registered: ").append(deviceIdentity.getDeviceName()).append(", ");
			if (chatClient != null && chatClient.isConnected()) {
				sb.append("Connected, ");
			} else {
				sb.append("Disconnected, ");
			}
		}
		
		sb.append("Device ID: ").append(deviceId);
		
		return sb.toString();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		if (MENU_ITEM_NAME_NEW.equals(actionCommand)) {
			createNewThing();
		} else if (MENU_ITEM_NAME_OPEN_FILE.equals(actionCommand)) {
			openFile();
		} else if (MENU_ITEM_NAME_SAVE.equals(actionCommand)) {
			save();
		} else if (MENU_ITEM_NAME_SAVE_AS.equals(actionCommand)) {
			saveAs();
		} else if (MENU_ITEM_NAME_QUIT.equals(actionCommand)) {
			quit();
		} else if (MENU_ITEM_NAME_POWER_ON.equals(actionCommand)) {
			powerOn();
		} else if (MENU_ITEM_NAME_POWER_OFF.equals(actionCommand)) {
			powerOff();
		} else if (MENU_ITEM_NAME_RESET.equals(actionCommand)) {
			reset();
		} else if (MENU_ITEM_NAME_DELETE.equals(actionCommand)) {
			delete();
		} else if (MENU_ITEM_NAME_REGISTER.equals(actionCommand)) {
			register();
		} else if (MENU_ITEM_NAME_CONNECT.equals(actionCommand)) {
			connect();
		} else if (MENU_ITEM_NAME_DISCONNECT.equals(actionCommand)) {
			disconnect();
		} else if (MENU_ITEM_NAME_ADDRESS_CONFIGURATION_MODE.equals(actionCommand)) {
			setToAddressConfigurationMode();
		} else if (MENU_ITEM_NAME_WORKING_MODE.equals(actionCommand)) {
			setToWorkingMode();
		} else if (MENU_ITEM_NAME_RECONFIGURE_ADDRESS.equals(actionCommand)) {
			reconfigureAddress();
		} else if (MENU_ITEM_NAME_SHOW_LOG_CONSOLE.equals(actionCommand)) {
			showLogConsoleDialog();
		} else if (MENU_ITEM_NAME_ABOUT.equals(actionCommand)) {
			showAboutDialog();
		} else {
			throw new IllegalArgumentException("Illegal action command: " + actionCommand);
		}
	}
	
	private void reconfigureAddress() {
		getSelectedFrame().getThing().powerOff();
		setToWorkingMode();
		setToAddressConfigurationMode();
		getSelectedFrame().getThing().powerOn();
	}

	private synchronized void setToWorkingMode() {
		addressConfigurator.stop();
		startWorking(chatClient);
		
		UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_WORKING_MODE).setEnabled(false);
		UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_ADDRESS_CONFIGURATION_MODE).setEnabled(true);	
		UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_RECONFIGURE_ADDRESS).setEnabled(false);	
	}

	private synchronized void setToAddressConfigurationMode() {
		addressConfigurator.start();
		stopWorking(chatClient);
		
		UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_ADDRESS_CONFIGURATION_MODE).setEnabled(false);
		UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_WORKING_MODE).setEnabled(true);
		
		ThingInternalFrame thingInternalFrame = getSelectedFrame();
		if (thingInternalFrame != null && !thingInternalFrame.getThing().isAddressConfigured()) {
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_RECONFIGURE_ADDRESS).setEnabled(true);
		}
	}
	
	private synchronized void disconnect() {
		if (chatClient == null || chatClient.isClosed())
			throw new IllegalStateException("Gateway has already disconnected.");
		
		doDisconnect();
		setDirty(true);
		refreshConnectionStateRelativatedMenus();
		updateStatus();
		UiUtils.showNotification(this, "Message", "Gateway has disconnected.");
	}

	private void doDisconnect() {
		if (chatClient != null) {
			autoReconnect = false;
			chatClient.close();
		}
		
		if (addressConfigurator != null && DynamicAddressConfigurator.State.WORKING != addressConfigurator.getState()) {
			addressConfigurator.stop();
		}
		addressConfigurator = null;
	}
	
	private void connect() {
		connect(true);
		
		// Don't start actuator before the time that chat client has connected to server.
		startActuator(chatClient);
	}
	
	private void connect(boolean dirty) {
		if (chatClient != null && chatClient.isConnected())
			throw new IllegalStateException("Gateway has already connected.");
		
		try {
			doConnect();
			setDirty(dirty);
			UiUtils.showNotification(this, "Message", "Gateway has connected.");
		} catch (ConnectionException e) {
			if (chatClient != null) {
				chatClient.close();
				chatClient = null;
			}
			
			JOptionPane.showMessageDialog(this, "Connection error. Error type: " + e.getType(), "Connect Error", JOptionPane.ERROR_MESSAGE);
		} catch (AuthFailureException e) {
			JOptionPane.showMessageDialog(this, "Authentication failed.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void doConnect() throws ConnectionException, AuthFailureException {
		if (chatClient == null)
			chatClient = createChatClient();
		
		IConnectionListener logConsoleListener = getLogConsoleInternetConnectionListener();
		if (logConsoleListener != null && !chatClient.getConnectionListeners().contains(logConsoleListener))
			chatClient.addConnectionListener(getLogConsoleInternetConnectionListener());
		
		if (!chatClient.getConnectionListeners().contains(this)) {
			chatClient.addConnectionListener(this);
		}
		
		chatClient.connect(new UsernamePasswordToken(deviceIdentity.getDeviceName().toString(), deviceIdentity.getCredentials()));
		
		autoReconnect = true;
		concentrator = createConcentrator();
		addressConfigurator = new DynamicAddressConfigurator(gatewayCommunicator, concentrator);
		addressConfigurator.addListener(this);
		
		refreshConnectionStateRelativatedMenus();
		updateStatus();
	}

	private IConcentrator createConcentrator() {
		IConcentrator concentrator = chatClient.createApi(IConcentrator.class);
		concentrator.init(deviceId, nodes, Collections.singletonMap(CommunicationNet.LORA, gatewayCommunicator));
		concentrator.addListener(this);
		
		return concentrator;
	}

	private IChatClient createChatClient() {
		if (deviceIdentity == null)
			throw new IllegalStateException("Device identity is null. Please register your gateway.");
		
		StandardStreamConfig streamConfigWithResource = createStreamConfigWithResource();
		IChatClient chatClient = new StandardChatClient(streamConfigWithResource);
		
		registerPlugins(chatClient);
		registerModels(chatClient);
		
		return chatClient;
	}
	
	private void startWorking(IChatClient chatClient) {
		startActuator(chatClient);
		startSensor(chatClient);
	}

	private void startSensor(IChatClient chatClient2) {
		// TODO Auto-generated method stub
		
	}

	private void startActuator(IChatClient chatClient) {
		if (addressConfigurator.getState() != DynamicAddressConfigurator.State.WORKING)
			return;
		
		IActuator actuator = chatClient.createApi(IActuator.class);
		actuator.start();
	}
	
	private void stopWorking(IChatClient chatClient) {
		stopSensor(chatClient);
		stopActuator(chatClient);
	}
	
	private void stopSensor(IChatClient chatClient) {
		// TODO Auto-generated method stub
		
	}

	private void stopActuator(IChatClient chatClient) {
		IActuator actuator = chatClient.createApi(IActuator.class);
		actuator.stop();
	}

	private void registerModels(IChatClient chatClient) {
		IModelRegistrar modelRegistrar = chatClient.createApi(IModelRegistrar.class);
		modelRegistrar.registerModeDescriptor(new Ge01ModelDescriptor());
		modelRegistrar.registerModeDescriptor(new Le01ModelDescriptor());
	}

	private void registerModels() {
		registeredModels = new HashMap<>();
		Ge01ModelDescriptor ge01 = new Ge01ModelDescriptor();
		registeredModels.put(ge01.getName(), ge01);
		Le01ModelDescriptor le01 = new Le01ModelDescriptor();
		registeredModels.put(le01.getName(), le01);
	}

	private void registerPlugins(IChatClient chatClient) {
		chatClient.register(ConcentratorPlugin.class);
		chatClient.register(ActuatorPlugin.class);
	}

	private StandardStreamConfig createStreamConfigWithResource() {
		StandardStreamConfig cloned = new StandardStreamConfig(streamConfig.getHost(), streamConfig.getPort());
		cloned.setTlsPreferred(streamConfig.isTlsPreferred());
		cloned.setResource(RESOURCE_NAME_GATEWAY);
		
		return cloned;
	}

	private void showLogConsoleDialog() {
		logConsolesDialog = new LogConsolesDialog(this, chatClient, registeredModels, network, gatewayCommunicator, allThings);
		logConsolesDialog.addWindowListener(this);
		
		logConsolesDialog.setVisible(true);
		
		UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_SHOW_LOG_CONSOLE).setEnabled(false);
	}

	private void register() {
		if (deviceIdentity != null)
			throw new IllegalStateException("Gateway has already registered.");
		
		if (streamConfig == null) {
			StreamConfigDialog streamConfigDialog = new StreamConfigDialog(this);
			UiUtils.showDialog(this, streamConfigDialog);
			
			streamConfig = streamConfigDialog.getStreamConfig();
		}
		
		if (streamConfig != null) {
			doRegister();	
		}
		
		if (deviceIdentity != null)
			UiUtils.showNotification(this, "Message", "Gateway has registered.");
	}
	
	private void doRegister() {
		if (streamConfig == null)
			throw new IllegalArgumentException("Null stream config.");
		
		IChatClient chatClient = new StandardChatClient(streamConfig);
		chatClient.register(IbdrPlugin.class);
		IRegistration registration = chatClient.createApi(IRegistration.class);
		adddInternetLogListener(registration);
		
		try {
			deviceIdentity = registration.register(deviceId);
		} catch (RegistrationException e) {
			JOptionPane.showMessageDialog(this, "Can't register device. Error: " + e.getError(), "Registration Error", JOptionPane.ERROR_MESSAGE);
		} finally {			
			registration.removeConnectionListener(getLogConsoleInternetConnectionListener());
			chatClient.close();
		}
		
		setDirty(true);
		refreshGatewayInstanceRelativatedMenus();
		updateStatus();
	}

	private void adddInternetLogListener(IRegistration registration) {
		IConnectionListener logListener = getLogConsoleInternetConnectionListener();
		if (logListener != null) {
			registration.addConnectionListener(logListener);
		}
	}

	private void delete() {
		// TODO Auto-generated method stub
		
	}

	private void reset() {
		ThingInternalFrame selectedFrame = getSelectedFrame();
		if (selectedFrame == null) {
			refreshThingSelectionRelativedMenuItems();
			return;
		}
		
		powerOff();
		selectedFrame.getThing().reset();
	}
	
	@Override
	public void powerOff() {
		getSelectedFrame().getThing().powerOff();
		refreshPowerRelativedMenuItems();
	}

	private ThingInternalFrame getSelectedFrame() {
		ThingInternalFrame thingFrame = (ThingInternalFrame)desktop.getSelectedFrame();
		return thingFrame;
	}
	
	@Override
	public void powerOn() {
		getSelectedFrame().getThing().powerOn();
		refreshPowerRelativedMenuItems();
	}

	private void saveAs() {
		JFileChooser fileChooser = createFileChooser();
		fileChooser.setDialogTitle("Choose a file to save your LoRa gateway info");
		File defaultDirectory = FileSystemView.getFileSystemView().getDefaultDirectory();
		fileChooser.setSelectedFile(new File(defaultDirectory, deviceId + ".lgi"));
		
		int result = fileChooser.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			saveToFile(fileChooser.getSelectedFile());
		}
	}

	private JFileChooser createFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new FileFilter() {
			
			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".lgi");
			}

			@Override
			public String getDescription() {
				return "LoRa gateway info file (.lgi)";
			}
		});
		
		return fileChooser;
	}

	private void save() {
		if (configFile == null)
			saveAs();
		else
			saveToFile(configFile);
	}

	private void saveToFile(File file) {
		JInternalFrame[] frames = desktop.getAllFrames();
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e3) {
				throw new RuntimeException("Can't create gateway info file " + file.getPath());
			}
		}
		
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream(file));
			output.writeObject(deviceId);
			if (streamConfig != null) {
				output.writeObject(new StreamConfigInfo(streamConfig.getHost(), streamConfig.getPort(), streamConfig.isTlsPreferred()));
			} else {
				output.writeObject(null);
			}
			
			if (deviceIdentity != null) {
				output.writeObject(new DeviceIdentityInfo(deviceIdentity.getDeviceName(), deviceIdentity.getCredentials()));
			} else {
				output.writeObject(null);
			}
			
			output.writeBoolean(autoReconnect);
			
			output.writeInt(frames.length);
			if (frames.length != 0) {				
				for (JInternalFrame frame : frames) {
					ThingInternalFrame thingFrame = (ThingInternalFrame)frame;
					output.writeObject(new ThingInfo(thingFrame.getLayer(), thingFrame.getX(), thingFrame.getY(),
							thingFrame.isSelected(), thingFrame.getThing(), thingFrame.getTitle()));
				}
			}
			
			output.writeInt(nodes.size());
			if (nodes.isEmpty()) {
				return;
			}
			
			for (Node node : nodes.values()) {
				output.writeObject(node);
			}
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format("Gateway info file %s doesn't exist.", file.getPath()));
		} catch (IOException e) {
			throw new RuntimeException("Can't save gateway info file.", e);
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		setDirty(false);
		if (!file.equals(configFile)) {
			setConfigFile(file);
		}
	}
	
	private void showAboutDialog() {
		UiUtils.showDialog(this, new AboutDialog(this, THING_NAME, Constants.SOFTWARE_VERSION));
	}

	private void quit() {
		doDisconnect();
		
		if (!dirty)
			System.exit(0);
		
		int result = JOptionPane.showConfirmDialog(this, "Gateway info has changed. Do you want to save the change?");
		if (result == JOptionPane.CANCEL_OPTION) {
			return;
		} else if (result == JOptionPane.NO_OPTION) {
			System.exit(0);
		} else {
			save();
			System.exit(0);
		}
	}

	private void openFile() {
		if (dirty) {
			int result = JOptionPane.showConfirmDialog(this, "Gateway info has changed. Do you want to save the change?");
			if (result == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (result == JOptionPane.YES_OPTION) {
				save();				
			} else {
				// NO-OP
			}
		}
		
		JFileChooser fileChooser = createFileChooser();
		fileChooser.setDialogTitle("Choose a gateway info file you want to open");
		File defaultDirectory = FileSystemView.getFileSystemView().getDefaultDirectory();
		fileChooser.setCurrentDirectory(defaultDirectory);
		
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			removeThings();
			loadFromFile(fileChooser.getSelectedFile());
			changeGatewayStatusAndRefreshUiThread(false);
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {					
					JInternalFrame[] frames = Gateway.this.desktop.getAllFrames();
					for (JInternalFrame frame : frames) {
						frame.addComponentListener(Gateway.this);
						frame.addInternalFrameListener(Gateway.this);
					}
					
					refreshGatewayInstanceRelativatedMenus();
					updateStatus();
				}
				
			});
		}
	}

	private void removeThings() {
		desktop.removeAll();
		
		// Refresh desktop
		desktop.setVisible(false);
		desktop.setVisible(true);
	}

	private void loadFromFile(File file) {
		GatewayInfo gatewayInfo = readGatewayInfo(file);
		
		deviceId = gatewayInfo.deviceId;
		streamConfig = gatewayInfo.streamConfig;
		deviceIdentity = gatewayInfo.deviceIdentity;
		autoReconnect = gatewayInfo.autoReconnect;
		
		if (gatewayInfo.thingInfos != null) {
			for (ThingInfo thingInfo : gatewayInfo.thingInfos) {
				showThing(thingInfo.getThing(), thingInfo.getTitle(), thingInfo.getLayer(), thingInfo.getX(), thingInfo.getY(), thingInfo.isSelected());
			}
		}
		refreshGatewayInstanceRelativatedMenus();
		
		setConfigFile(file);
	}
	
	private void refreshConnectionStateRelativatedMenus() {
		if (chatClient != null && chatClient.isConnected()) {
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_CONNECT).setEnabled(false);			
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_DISCONNECT).setEnabled(true);			
			
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_ADDRESS_CONFIGURATION_MODE).setEnabled(true);
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_WORKING_MODE).setEnabled(false);
			
			refreshGatewayInstanceRelativatedMenus();
		} else {
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_DISCONNECT).setEnabled(false);			
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_CONNECT).setEnabled(true);
			
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_ADDRESS_CONFIGURATION_MODE).setEnabled(false);
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_WORKING_MODE).setEnabled(false);
		}
	}

	private void refreshGatewayInstanceRelativatedMenus() {
		if (deviceIdentity != null) {
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_REGISTER).setEnabled(false);
			
			if (!isConnected()) {
				UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_CONNECT).setEnabled(true);
				UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_DISCONNECT).setEnabled(false);
			} else {
				UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_DISCONNECT).setEnabled(true);
				UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_CONNECT).setEnabled(false);
			}
		}
	}
	
	private void setConfigFile(File file) {
		if (configFile == null && file != null) {
			UiUtils.getMenuItem(menuBar, MENU_NAME_FILE, MENU_ITEM_NAME_SAVE_AS).setEnabled(true);
		}
		
		configFile = file;
	}

	private GatewayInfo readGatewayInfo(File file) {
		GatewayInfo gatewayInfo = new GatewayInfo();
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new FileInputStream(file));
			
			gatewayInfo.deviceId = (String)input.readObject();
			StreamConfigInfo streamConfigInfo = (StreamConfigInfo)input.readObject();
			gatewayInfo.streamConfig = streamConfigInfo == null ? null : createStreamConfig(streamConfigInfo);
			
			DeviceIdentityInfo deviceIdentityInfo = (DeviceIdentityInfo)input.readObject();
			gatewayInfo.deviceIdentity = deviceIdentityInfo == null ? null : createDeviceIdentity(deviceIdentityInfo);
			
			gatewayInfo.autoReconnect = input.readBoolean();
			
			int size = input.readInt();
			if (size != 0) {
				gatewayInfo.thingInfos = new ArrayList<>(size);
				for (int i = 0; i < size; i++) {
					ThingInfo thingInfo = (ThingInfo)input.readObject();
					gatewayInfo.thingInfos.add(thingInfo);
				}
			}
			
			size = input.readInt();
			if (size != 0) {
				gatewayInfo.nodes = new HashMap<>();
				for (int i = 0; i < size; i++) {
					Node node = (Node)input.readObject();
					gatewayInfo.nodes.put(node.getLanId(), node);
				}
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format("Gateway info file %s doesn't exist.", file.getPath()));
		} catch (IOException e) {
			throw new RuntimeException("Can't load gateway info file.", e);
		} catch (ClassNotFoundException e) {
			// ???
			e.printStackTrace();
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		return gatewayInfo;
	}
	
	private DeviceIdentity createDeviceIdentity(DeviceIdentityInfo deviceIdentityInfo) {
		return new DeviceIdentity(deviceIdentityInfo.deviceName, deviceIdentityInfo.credentials);
	}

	private StandardStreamConfig createStreamConfig(StreamConfigInfo streamConfigInfo) {
		StandardStreamConfig streamConfig = new StandardStreamConfig(streamConfigInfo.host, streamConfigInfo.port);
		streamConfig.setTlsPreferred(streamConfigInfo.tlsPreferred);
		
		return streamConfig;
	}

	private class GatewayInfo {
		private String deviceId;
		private StandardStreamConfig streamConfig;
		private DeviceIdentity deviceIdentity;
		private boolean autoReconnect;
		private List<ThingInfo> thingInfos;
		private Map<String, Node> nodes;
	}

	private void createNewThing() {
		String thingName = (String)JOptionPane.showInputDialog(this, "Choose thing you want to create",
				"Choose thing", JOptionPane.QUESTION_MESSAGE, null, getThingNames(), null);
		if (thingName == null)
			return;
		
		createThing(thingName);
	}
	
	private AbstractLoraThingEmulator createThing(String thingName) {
		AbstractLoraThingEmulatorFactory<?> thingFactory = getThingFactory(thingName);
		IThingEmulator thing = thingFactory.create(new LoraCommunicator(network.createChip(LoraAddress.randomLoraAddress(
				LoraAddress.DEFAULT_THING_COMMUNICATION_FREQUENCE_BAND))));
		
		if (!(thing instanceof AbstractLoraThingEmulator)) {
			throw new RuntimeException("Not a lora thing emulator.");
		}
		
		AbstractLoraThingEmulator loraThing = (AbstractLoraThingEmulator)thing;
		List<AbstractLoraThingEmulator> things = getThings(thingFactory);
		
		int instanceIndex = things.size();
		things.add(loraThing);
		
		showThing(loraThing, getThingInstanceName(thingFactory, instanceIndex), -1, 30 * instanceIndex, 30 * instanceIndex);
		changeGatewayStatusAndRefreshUiThread(true);				
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JInternalFrame selectedFrame = Gateway.this.desktop.getSelectedFrame();				
				selectedFrame.addComponentListener(Gateway.this);
				selectedFrame.addInternalFrameListener(Gateway.this);			
			}
		});
		
		if (logConsolesDialog != null) {
			logConsolesDialog.createThingLogConsole(loraThing);
		}
		
		thing.powerOn();
		
		return loraThing;
	}

	private void showThing(AbstractLoraThingEmulator thing, String title, int layer, int x, int y) {
		showThing(thing, title, layer, x, y, true);
	}

	private void showThing(AbstractLoraThingEmulator thing, String title, int layer, int x, int y, boolean selected) {
		// TODO: Trace the thing's status(etc. Battery Power) and refresh dirty variable.
		AbstractThingEmulatorPanel<?> thingPanel = thing.getPanel();
		ThingInternalFrame internalFrame = new ThingInternalFrame(thing, title);
		internalFrame.addComponentListener(this);
		internalFrame.setBounds(x, y, thingPanel.getPreferredSize().width, thingPanel.getPreferredSize().height);
		internalFrame.setVisible(true);
		
		desktop.add(internalFrame);
		
		try {
			if (layer != -1 ) {
				internalFrame.setLayer(layer);
			}
			internalFrame.setSelected(selected);
			// Only deactivated event is fired when calling internalFrame.setSelected(true).
			// So we need to call internalFrameActivated() method manually.
			internalFrameActivated(null);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		
		if (thing instanceof AbstractLoraThingEmulator)
			thingPanel.updateStatus(((AbstractLoraThingEmulator)thing).getThingStatus());
	}
	
	private void changeGatewayStatusAndRefreshUiThread(boolean dirty) {
		try {
			SwingUtilities.invokeLater(new GatewayStatusChanger(dirty));
		} catch (Exception e) {
			throw new RuntimeException("Can't add component listener to thing internal frame.");
		}
	}
	
	private class GatewayStatusChanger implements Runnable {
		private boolean dirty;
		
		public GatewayStatusChanger(boolean dirty) {
			this.dirty = dirty;
		}
		
		@Override
		public void run() {
			setDirty(dirty);
			refreshPowerRelativedMenuItems();
		}
	}

	private String getThingInstanceName(IThingEmulatorFactory<?> factory, int thingsIndex) {
		return factory.getThingName() + " #" + thingsIndex;
	}

	private List<AbstractLoraThingEmulator> getThings(IThingEmulatorFactory<?> factory) {
		List<AbstractLoraThingEmulator> things = allThings.get(factory.getThingName());
		if (things == null) {
			things = new ArrayList<>();
			allThings.put(factory.getThingName(), things);
		}
		
		return things;
	}

	private AbstractLoraThingEmulatorFactory<?> getThingFactory(String thingName) {
		for (AbstractLoraThingEmulatorFactory<?> thingFactory : thingFactories) {
			if (thingFactory.getThingName().equals(thingName))
				return thingFactory;
		}
		
		throw new IllegalArgumentException(String.format("Illegal thing name: %s.", thingName));
	}

	private Object[] getThingNames() {
		if (thingFactories.isEmpty())
			throw new IllegalStateException("No thing factory registered.");
		
		Object[] thingNames = new Object[thingFactories.size()];
		
		for (int i = 0; i < thingFactories.size(); i++) {
			thingNames[i] = thingFactories.get(i).getThingName();
		}
		
		return thingNames;
	}

	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
		menuBar.add(createToolsMenu());
		menuBar.add(createHelpMenu());
		
		return menuBar;
    }

	private JMenu createEditMenu() {
		JMenu editMenu = new JMenu(MENU_TEXT_EDIT);
		editMenu.setName(MENU_NAME_EDIT);
		editMenu.setMnemonic(KeyEvent.VK_E);
		
		editMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_POWER_ON, MENU_ITEM_TEXT_POWER_ON, -1, null, this, false));
		editMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_POWER_OFF, MENU_ITEM_TEXT_POWER_OFF, -1, null, this, false));
		editMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_RESET, MENU_ITEM_TEXT_RESET, -1, null, this, false));

		editMenu.addSeparator();

		editMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_DELETE, MENU_ITEM_TEXT_DELETE, -1, null, this, false));
		
		return editMenu;
	}
	
	private JMenu createToolsMenu() {
		JMenu toolsMenu = new JMenu(MENU_TEXT_TOOLS);
		toolsMenu.setName(MENU_NAME_TOOLS);
		toolsMenu.setMnemonic(KeyEvent.VK_T);
		
		toolsMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_REGISTER, MENU_ITEM_TEXT_REGISTER, -1, null, this));
		
		toolsMenu.addSeparator();
		
		toolsMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_CONNECT, MENU_ITEM_TEXT_CONNECT, -1, null, this, false));
		toolsMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_DISCONNECT, MENU_ITEM_TEXT_DISCONNECT, -1, null, this, false));
		
		toolsMenu.addSeparator();
		
		toolsMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_WORKING_MODE,
				MENU_ITEM_TEXT_WORKING_MODE, -1, null, this, false));
		toolsMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_ADDRESS_CONFIGURATION_MODE,
				MENU_ITEM_TEXT_ADDRESS_CONFIGURATION_MODE, -1, null, this, false));
		toolsMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_RECONFIGURE_ADDRESS,
				MENU_ITEM_TEXT_RECONFIGURE_ADDRESS, -1, null, this, false));
		
		toolsMenu.addSeparator();
		
		toolsMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_SHOW_LOG_CONSOLE, MENU_ITEM_TEXT_SHOW_LOG_CONSOLE, -1, null, this));
		
		return toolsMenu;
	}

	private JMenu createHelpMenu() {
		JMenu helpMenu = new JMenu(MENU_TEXT_HELP);
		helpMenu.setName(MENU_NAME_HELP);
		helpMenu.setMnemonic(KeyEvent.VK_H);
		
		helpMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_ABOUT, MENU_ITEM_TEXT_ABOUT, -1, null, this));
		
		return helpMenu;
	}

	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu(MENU_TEXT_FILE);
		fileMenu.setName(MENU_NAME_FILE);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		fileMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_NEW, MENU_ITEM_TEXT_NEW, -1,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK), this));
		fileMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_OPEN_FILE, MENU_ITEM_TEXT_OPEN_FILE, -1, null, this));
		
		fileMenu.addSeparator();
		
		fileMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_SAVE, MENU_ITEM_TEXT_SAVE, -1,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), this, false));
		fileMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_SAVE_AS, MENU_ITEM_TEXT_SAVE_AS, -1, null, this, false));
		
		fileMenu.addSeparator();
		
		fileMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_QUIT, MENU_ITEM_TEXT_QUIT, -1, null, this));
		
		return fileMenu;
	}

	@Override
	public void registerThingEmulatorFactory(IThingEmulatorFactory<?> thingFactory) {
		if (!(thingFactory instanceof AbstractLoraThingEmulatorFactory)) {
			throw new IllegalArgumentException("Not a lora thing emulator factory.");
		}
		
		for (IThingEmulatorFactory<?> existedThingFactory : thingFactories) {
			if (existedThingFactory.getClass().getName().equals(thingFactory.getClass().getName())) {
				throw new IllegalArgumentException(String.format("Thing factory %s has registered.", thingFactory.getClass().getName()));				
			}
		}
		
		thingFactories.add((AbstractLoraThingEmulatorFactory<?>)thingFactory);
	}

	@Override
	public void componentResized(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {
		setDirty(true);
	}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		if (e.getSource() instanceof JFrame) {
			quit();
		} else if (e.getSource() instanceof LogConsolesDialog) {
			logConsolesDialog.setVisible(false);
			logConsolesDialog.dispose();
			logConsolesDialog = null;
			
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_SHOW_LOG_CONSOLE).setEnabled(true);
		} else {
			// NO-OP
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {}

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		setDirty(true);
		refreshPowerRelativedMenuItems();
		refreshThingSelectionRelativedMenuItems();
	}
	
	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {}
	
	private void refreshThingSelectionRelativedMenuItems() {
		ThingInternalFrame thingFrame = getSelectedFrame();
		if (thingFrame == null) {
			UiUtils.getMenuItem(menuBar, MENU_NAME_EDIT, MENU_ITEM_NAME_RESET).setEnabled(false);
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_RECONFIGURE_ADDRESS).setEnabled(false);
			
			return;
		}
		
		UiUtils.getMenuItem(menuBar, MENU_NAME_EDIT, MENU_ITEM_NAME_RESET).setEnabled(true);
		
		if (!UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_ADDRESS_CONFIGURATION_MODE).isEnabled())
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_RECONFIGURE_ADDRESS).setEnabled(true);
	}

	private void refreshPowerRelativedMenuItems() {
		ThingInternalFrame thingFrame = getSelectedFrame();
		if (thingFrame == null)
			return;
		
		if (thingFrame.getThing().isPowered()) {			
			UiUtils.getMenuItem(menuBar, MENU_NAME_EDIT, MENU_ITEM_NAME_POWER_ON).setEnabled(false);
			UiUtils.getMenuItem(menuBar, MENU_NAME_EDIT, MENU_ITEM_NAME_POWER_OFF).setEnabled(true);			
		} else {
			UiUtils.getMenuItem(menuBar, MENU_NAME_EDIT, MENU_ITEM_NAME_POWER_OFF).setEnabled(false);			
			UiUtils.getMenuItem(menuBar, MENU_NAME_EDIT, MENU_ITEM_NAME_POWER_ON).setEnabled(true);
		}
	}
	
	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		refreshDirtyRelativedMenuItems(dirty);
	}

	private void refreshDirtyRelativedMenuItems(boolean dirty) {
		JMenuItem saveMenuItem = UiUtils.getMenuItem(menuBar, MENU_NAME_FILE, MENU_ITEM_NAME_SAVE);
		if (dirty) {
			saveMenuItem.setEnabled(true);
		} else {
			saveMenuItem.setEnabled(false);
		}
	}

	@Override
	public boolean isRegistered() {
		return deviceIdentity != null;
	}

	@Override
	public boolean isConnected() {
		return chatClient != null && chatClient.isConnected();
	}
	
	@Override
	public final String getLanId() {
		return DEFAULT_GATEWAY_LAN_ID;
	}

	@Override
	public String getDeviceId() {
		return deviceId;
	}
	
	@Override
	public String getModel() {
		return THING_MODEL;
	}

	@Override
	public int getBatteryPower() {
		return ALWAYS_FULL_POWER;
	}

	@Override
	public boolean isPowered() {
		return true;
	}

	@Override
	public String getSoftwareVersion() {
		return Constants.SOFTWARE_VERSION;
	}

	@Override
	public synchronized void occurred(ConnectionException exception) {
		if (exception.getType() == ConnectionException.Type.CONNECTION_CLOSED && chatClient.isClosed()) {
			if (addressConfigurator != null && addressConfigurator.getState() != DynamicAddressConfigurator.State.WORKING) {				
				addressConfigurator.stop();
			}
			
			refreshConnectionStateRelativatedMenus();
			updateStatus();
			UiUtils.showNotification(this, "Message", "Gateway has disconnected.");
		}
	}

	@Override
	public void received(String message) {
		// NO-OP
	}

	@Override
	public void sent(String message) {
		// NO-OP
	}

	@Override
	public void occurred(StanzaError error, Node source) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void occurred(LanError error, Node source) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void nodeCreated(String requestedId, String allocatedLanId, Node node) {
		if (!requestedId.equals(allocatedLanId))
			nodes.remove(requestedId);
		
		nodes.put(allocatedLanId, node);
		
		boolean found = false;
		for (Collection<AbstractLoraThingEmulator> things : allThings.values()) {
			for (AbstractLoraThingEmulator thing : things) {
				if (thing.getDeviceId().equals(node.getDeviceId())) {
					found = true;
					thing.nodeAdded(allocatedLanId);
					break;
				}
			}
			
			if (found)
				break;
		}
		
		setDirty(dirty);
	}

	@Override
	public void nodeRemoved(String lanId, Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addressConfigured(String deviceId, LoraAddress address) {
		Node node = new Node();
		node.setDeviceId(deviceId);
		node.setLanId(concentrator.getBestSuitedNewLanId());
		node.setCommunicationNet(CommunicationNet.LORA);
		node.setAddress(address.toString());
		
		nodes.put(node.getLanId(), node);
		
		concentrator.createNode(deviceId, node.getLanId(), new NodeAddress<LoraAddress>(CommunicationNet.LORA, address.toString()));
	}

	@Override
	public void addDeviceListener(IDeviceListener listener) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public boolean removeDeviceListener(IDeviceListener listener) {
		throw new UnsupportedOperationException("Not supported.");
	}
}
