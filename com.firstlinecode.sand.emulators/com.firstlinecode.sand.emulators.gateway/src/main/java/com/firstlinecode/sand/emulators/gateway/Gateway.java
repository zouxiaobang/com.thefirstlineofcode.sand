package com.firstlinecode.sand.emulators.gateway;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FontUIResource;

import com.firstlinecode.chalk.AuthFailureException;
import com.firstlinecode.chalk.IChatClient;
import com.firstlinecode.chalk.StandardChatClient;
import com.firstlinecode.chalk.core.stream.StandardStreamConfig;
import com.firstlinecode.chalk.core.stream.UsernamePasswordToken;
import com.firstlinecode.chalk.network.ConnectionException;
import com.firstlinecode.chalk.network.IConnectionListener;
import com.firstlinecode.sand.client.ibdr.IRegistration;
import com.firstlinecode.sand.client.ibdr.IbdrPlugin;
import com.firstlinecode.sand.client.ibdr.RegistrationException;
import com.firstlinecode.sand.client.things.IEventListener;
import com.firstlinecode.sand.client.things.IThing;
import com.firstlinecode.sand.client.things.actuator.IAction;
import com.firstlinecode.sand.client.things.actuator.IActionListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicationChip;
import com.firstlinecode.sand.client.things.commuication.ICommunicationNetwork;
import com.firstlinecode.sand.client.things.concentrator.IAddressConfigurator;
import com.firstlinecode.sand.client.things.concentrator.Node;
import com.firstlinecode.sand.client.things.concentrator.NodeCreationException;
import com.firstlinecode.sand.client.things.concentrator.NodeNotFoundException;
import com.firstlinecode.sand.emulators.lora.ILoraChip;
import com.firstlinecode.sand.emulators.lora.LoraAddress;
import com.firstlinecode.sand.emulators.thing.AbstractThingEmulator;
import com.firstlinecode.sand.emulators.thing.AbstractThingEmulatorPanel;
import com.firstlinecode.sand.emulators.thing.Constants;
import com.firstlinecode.sand.emulators.thing.IThingEmulator;
import com.firstlinecode.sand.emulators.thing.IThingEmulatorFactory;
import com.firstlinecode.sand.emulators.thing.ThingsUtils;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public class Gateway<A, D> extends JFrame implements ActionListener, InternalFrameListener,
		ComponentListener, WindowListener, IGateway<A>, IConnectionListener {
	private static final int ALWAYS_FULL_POWER = 100;
	private static final String DEVICE_TYPE = "Gateway";
	private static final String DEVICE_MODE = "Emulator01";
	
	private static final int DEFAULT_NOTIFICATION_DELAY_TIME = 1000 * 2;

	private static final long serialVersionUID = -7894418812878036627L;
	
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
	private static final String MENU_ITEM_TEXT_SHOW_LOG_CONSOLE = "Show Log Console";
	private static final String MENU_ITEM_NAME_SHOW_LOG_CONSOLE = "show_log_console";
	
	// Help Menu
	private static final String MENU_TEXT_HELP = "Help";
	private static final String MENU_NAME_HELP = "help";
	private static final String MENU_ITEM_TEXT_ABOUT = "About";
	private static final String MENU_ITEM_NAME_ABOUT = "about";

	private static final String RESOURCE_NAME_GATEWAY = "gateway";
	private static final int DEFAULT_LORA_WORKING_FREQUENCY_BAND = 0;
	private static final int DEFAULT_LORA_DEPLOYING_FREQUENCY_BAND = 63;
	
	private String deviceId;
	private DeviceIdentity deviceIdentity;
	private StandardStreamConfig streamConfig;
	private String lanId;
	
	private List<IThingEmulatorFactory> factories;
	private Map<String, List<IThingEmulator>> allThings;
	private Map<String, Node<A>> nodes;
	private boolean dirty;
	
	private ICommunicationNetwork<A, D> network;
	
	private JDesktopPane desktop;
	private JMenuBar menuBar;
	private GatewayStatusBar statusBar;
	private LogConsoleDialog logConsoleDalog;
	
	private File configFile;
	
	private IChatClient chatClient;
	private boolean autoReconnect;
	
	private IAddressConfigurator addressConfigurator;
	
	public Gateway(ICommunicationNetwork<A, D> network) {
		super("Unregistered Gateway");
		
		deviceId = ThingsUtils.generateRandomDeviceId();
		
		factories = new ArrayList<>();
		allThings = new HashMap<>();
		nodes = new HashMap<>();
		dirty = false;
		
		this.network = network;
		
		autoReconnect = false;
		new Thread(new AutoReconnectThread()).start();
		
		setupUi();
	}
	
	private class AutoReconnectThread implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (autoReconnect && (chatClient == null || !chatClient.isConnected())) {
					log("Trying to reconnect...");
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
		
		statusBar = new GatewayStatusBar();
		add(statusBar, BorderLayout.SOUTH);
		
		updateStatus();
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
	}

	private void updateStatus() {
		statusBar.setText(getGatewayStatus());
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
	
	private String getGatewayStatus() {
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
		
		sb.append("Device ID: ").append(deviceId).append(", ");
		
		return sb.toString().substring(0, sb.length() - 2);
	}
	
	private class GatewayStatusBar extends JPanel {		
		private static final long serialVersionUID = -4540556323673700464L;
		
		private JLabel text;
		private JButton copy;
		
		public GatewayStatusBar() {
			super(new BorderLayout());
			
			JPanel statusBarPanel = new JPanel();
			text = new JLabel();
			text.setHorizontalAlignment(SwingConstants.RIGHT);
			statusBarPanel.add(text);
			
			copy = new JButton("Copy Device ID");
			copy.setToolTipText("Copy gateway device ID to clipboard.");
			copy.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(new StringSelection(deviceId), null);
					Gateway.this.showNotification(Gateway.this, "Message", "Device ID has copied to clipboard.", DEFAULT_NOTIFICATION_DELAY_TIME);
				}
			});
			statusBarPanel.add(copy);
			
			add(statusBarPanel, BorderLayout.EAST);	
			setPreferredSize(new Dimension(640, 48));
		}
		
		public void setText(String status) {
			text.setText(status);
		}
	}
	
	private void showNotification(Window parent, String title, String message, int millis) {
		final JDialog dialog = new JDialog(this, title, ModalityType.MODELESS);
		dialog.setBounds(getParentCenterBounds(400, 160));
		dialog.add(new JLabel(message));
		dialog.setVisible(true);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				dialog.setVisible(false);
				dialog.dispose();
			}
		}, millis);
	}
	
	private Rectangle getParentCenterBounds(int width, int height) {
		int parentX = Gateway.this.getX();
		int parentY = Gateway.this.getY();
		int parentWidth = Gateway.this.getWidth();
		int parentHeight = Gateway.this.getHeight();
		
		if (width > parentWidth || height > parentHeight)
			return new Rectangle(parentX, parentY, width, height);
		
		return new Rectangle((parentX + (parentWidth - width) / 2), (parentY + (parentHeight - height) / 2), width, height);
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
		} else if (MENU_ITEM_NAME_SHOW_LOG_CONSOLE.equals(actionCommand)) {
			showLogConsoleDialog();
		} else if (MENU_ITEM_NAME_ABOUT.equals(actionCommand)) {
			showAboutDialog();
		} else {
			throw new IllegalArgumentException("Illegal action command: " + actionCommand);
		}
	}
	
	private void disconnect() {
		if (chatClient == null || chatClient.isClosed())
			throw new IllegalStateException("Gateway has already disconnected.");
		
		doDisconnect();
		setDirty(true);
		refreshConnectionStateRelativatedMenus();
		updateStatus();
		showNotification(this, "Message", "Gateway has disconnected.", DEFAULT_NOTIFICATION_DELAY_TIME);
	}

	private void doDisconnect() {
		if (chatClient != null) {
			autoReconnect = false;
			chatClient.close();
		}
	}
	
	private void connect() {
		connect(true);
	}
	
	private void connect(boolean dirty) {
		if (chatClient != null && chatClient.isConnected())
			throw new IllegalStateException("Gateway has already connected.");
		
		try {
			doConnect();
			setDirty(dirty);
			showNotification(this, "Message", "Gateway has connected.", DEFAULT_NOTIFICATION_DELAY_TIME);
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
		
		log("Gateway is trying to connect to server...");
		chatClient.connect(new UsernamePasswordToken(deviceIdentity.getDeviceName().toString(), deviceIdentity.getCredentials()));
		log("Gateway has connected to server.");
		autoReconnect = true;
		
		refreshConnectionStateRelativatedMenus();
		updateStatus();
	}

	private IChatClient createChatClient() {
		if (deviceIdentity == null)
			throw new IllegalStateException("Device identity is null. Please register your gateway.");
		
		chatClient = new StandardChatClient(streamConfig);
		chatClient.addConnectionListener(this);

		return chatClient;
	}

	private void showLogConsoleDialog() {
		logConsoleDalog = new LogConsoleDialog(this);
		logConsoleDalog.setVisible(true);
		
		getMenuItem(MENU_NAME_TOOLS, MENU_ITEM_NAME_SHOW_LOG_CONSOLE).setEnabled(false);
	}

	private void register() {
		if (deviceIdentity != null)
			throw new IllegalStateException("Gateway has already registered.");
		
		if (streamConfig == null) {
			StreamConfigDialog streamConfigDialog = new StreamConfigDialog(this);
			showDialog(streamConfigDialog);
			
			streamConfig = streamConfigDialog.getStreamConfig();
		}
		
		if (streamConfig != null) {
			streamConfig.setResource(RESOURCE_NAME_GATEWAY);
			doRegister();	
		}
		
		if (deviceIdentity != null)
			showNotification(this, "Message", "Gateway has registered.", DEFAULT_NOTIFICATION_DELAY_TIME);
	}
	
	private void doRegister() {
		if (streamConfig == null)
			throw new IllegalArgumentException("Null stream config.");
		
		IChatClient chatClient = new StandardChatClient(streamConfig);
		chatClient.register(IbdrPlugin.class);
		IRegistration registration = chatClient.createApi(IRegistration.class);
		registration.addConnectionListener(this);
		try {
			deviceIdentity = registration.register(deviceId);
			
			setDirty(true);
			refreshGatewayInstanceRelativatedMenus();
			updateTitle();
			updateStatus();			
		} catch (RegistrationException e) {
			log(e);
			JOptionPane.showMessageDialog(this, "Can't register device. Error: " + e.getError(), "Registration Error", JOptionPane.ERROR_MESSAGE);
		} finally {			
			chatClient.close();
		}
	}

	private void log(Exception e) {
		if (logConsoleDalog != null) {
			logConsoleDalog.log(e);
		} else {
			e.printStackTrace();
		}
	}
	
	private void log(String message) {
		if (logConsoleDalog != null) {
			logConsoleDalog.log(message);
		} else {
			System.out.println(message);
		}
	}

	private void showDialog(JDialog dialog) {
		Dimension size = dialog.getPreferredSize();
		Rectangle bounds = getBounds();
		int x = (int)(bounds.x + (bounds.getWidth() - size.width) / 2);
		int y = (int)(bounds.y + (bounds.getHeight() - size.height) / 2);
		
		dialog.setBounds(x, y, size.width, size.height);
		dialog.setVisible(true);
	}

	private void delete() {
		// TODO Auto-generated method stub
		
	}

	private void reset() {
		JOptionPane.showMessageDialog(this, "Not implemented yet.");
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
		fileChooser.setDialogTitle("Choose a file to save your gateway info");
		File defaultDirectory = FileSystemView.getFileSystemView().getDefaultDirectory();
		fileChooser.setSelectedFile(new File(defaultDirectory, deviceId + ".gwi"));
		
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
				return f.getName().endsWith(".gwi");
			}

			@Override
			public String getDescription() {
				return "Gateway info file (.gwi)";
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
		showDialog(new AboutDialog(this, Constants.SOFTWARE_VERSION));
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
				// Noop
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
					updateTitle();
					updateStatus();
				}
				
			});
		}
	}

	protected void updateTitle() {
		if (deviceIdentity == null) {
			setTitle("Unregistered Gateway");
		} else {
			setTitle("Registered Gateway");
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
			getMenuItem(MENU_NAME_TOOLS, MENU_ITEM_NAME_CONNECT).setEnabled(false);			
			getMenuItem(MENU_NAME_TOOLS, MENU_ITEM_NAME_DISCONNECT).setEnabled(true);			
		} else {
			getMenuItem(MENU_NAME_TOOLS, MENU_ITEM_NAME_DISCONNECT).setEnabled(false);			
			getMenuItem(MENU_NAME_TOOLS, MENU_ITEM_NAME_CONNECT).setEnabled(true);			
		}
	}

	private void refreshGatewayInstanceRelativatedMenus() {
		if (deviceIdentity != null) {
			getMenuItem(MENU_NAME_TOOLS, MENU_ITEM_NAME_REGISTER).setEnabled(false);
			
			if (!isConnected()) {
				getMenuItem(MENU_NAME_TOOLS, MENU_ITEM_NAME_CONNECT).setEnabled(true);
				getMenuItem(MENU_NAME_TOOLS, MENU_ITEM_NAME_DISCONNECT).setEnabled(false);
			} else {
				getMenuItem(MENU_NAME_TOOLS, MENU_ITEM_NAME_DISCONNECT).setEnabled(true);
				getMenuItem(MENU_NAME_TOOLS, MENU_ITEM_NAME_CONNECT).setEnabled(false);
			}
		}
	}
	
	private void setConfigFile(File file) {
		if (configFile == null && file != null) {
			getMenuItem(MENU_NAME_FILE, MENU_ITEM_NAME_SAVE_AS).setEnabled(true);
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
		streamConfig.setResource(RESOURCE_NAME_GATEWAY);
		
		return streamConfig;
	}

	private class GatewayInfo {
		private String deviceId;
		private StandardStreamConfig streamConfig;
		private DeviceIdentity deviceIdentity;
		private boolean autoReconnect;
		private List<ThingInfo> thingInfos;
	}

	private void createNewThing() {
		String thingName = (String)JOptionPane.showInputDialog(this, "Choose thing you want to create",
				"Choose thing", JOptionPane.QUESTION_MESSAGE, null, getThingNames(), null);
		createThing(thingName);
	}
	
	private IThingEmulator createThing(String thingName) {
		IThingEmulatorFactory factory = getFactory(thingName);
		ICommunicationChip<?> chip = network.createChip((A)LoraAddress.randomLoraAddress(DEFAULT_LORA_WORKING_FREQUENCY_BAND),
				Collections.singletonMap("type", (Object)ILoraChip.Type.NORMAL));
		IThingEmulator thing = (IThingEmulator)factory.create(chip);
		
		List<IThingEmulator> things = getThings(factory);
		
		int instanceIndex = things.size();
		thing.powerOn();
		
		things.add(thing);
		
		showThing(thing, getThingInstanceName(factory, instanceIndex), -1, 30 * instanceIndex, 30 * instanceIndex);
		changeGatewayStatusAndRefreshUiThread(true);				
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JInternalFrame selectedFrame = Gateway.this.desktop.getSelectedFrame();				
				selectedFrame.addComponentListener(Gateway.this);
				selectedFrame.addInternalFrameListener(Gateway.this);			
			}
		});
		
		return thing;
	}

	private void showThing(IThingEmulator thing, String title, int layer, int x, int y) {
		this.showThing(thing, title, layer, x, y, true);
	}

	private void showThing(IThingEmulator thing, String title, int layer, int x, int y, boolean selected) {
		AbstractThingEmulatorPanel thingPanel = thing.getPanel();
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
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		
		
		if (thing instanceof AbstractThingEmulator)
			thingPanel.updateStatus(((AbstractThingEmulator)thing).getThingStatus());
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
			refreshThingSelectionRelativedMenuItems();
			refreshPowerRelativedMenuItems();
		}
	}

	private String getThingInstanceName(IThingEmulatorFactory factory, int thingsIndex) {
		return factory.getThingName() + " #" + thingsIndex;
	}

	private List<IThingEmulator> getThings(IThingEmulatorFactory factory) {
		List<IThingEmulator> things = allThings.get(factory.getThingName());
		if (things == null) {
			things = new ArrayList<>();
			allThings.put(factory.getThingName(), things);
		}
		return things;
	}

	private IThingEmulatorFactory getFactory(String thingName) {
		for (IThingEmulatorFactory factory : factories) {
			if (factory.getThingName().equals(thingName))
				return factory;
		}
		
		throw new IllegalArgumentException(String.format("Illegal thing name: %s", thingName));
	}

	private Object[] getThingNames() {
		if (factories.isEmpty())
			throw new IllegalStateException("No thing factory registered.");
		
		Object[] thingNames = new Object[factories.size()];
		
		for (int i = 0; i < factories.size(); i++) {
			thingNames[i] = factories.get(i).getThingName();
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
		
		editMenu.add(createMenuItem(MENU_ITEM_NAME_POWER_ON, MENU_ITEM_TEXT_POWER_ON, -1, null, false));
		editMenu.add(createMenuItem(MENU_ITEM_NAME_POWER_OFF, MENU_ITEM_TEXT_POWER_OFF, -1, null, false));
		editMenu.add(createMenuItem(MENU_ITEM_NAME_RESET, MENU_ITEM_TEXT_RESET, -1, null, false));

		editMenu.addSeparator();

		editMenu.add(createMenuItem(MENU_ITEM_NAME_DELETE, MENU_ITEM_TEXT_DELETE, -1, null, false));
		
		return editMenu;
	}
	
	private JMenu createToolsMenu() {
		JMenu toolsMenu = new JMenu(MENU_TEXT_TOOLS);
		toolsMenu.setName(MENU_NAME_TOOLS);
		toolsMenu.setMnemonic(KeyEvent.VK_T);
		
		toolsMenu.add(createMenuItem(MENU_ITEM_NAME_REGISTER, MENU_ITEM_TEXT_REGISTER, -1, null));
		
		toolsMenu.addSeparator();
		
		toolsMenu.add(createMenuItem(MENU_ITEM_NAME_CONNECT, MENU_ITEM_TEXT_CONNECT, -1, null, false));
		toolsMenu.add(createMenuItem(MENU_ITEM_NAME_DISCONNECT, MENU_ITEM_TEXT_DISCONNECT, -1, null, false));
		
		toolsMenu.addSeparator();
		
		toolsMenu.add(createMenuItem(MENU_ITEM_NAME_SHOW_LOG_CONSOLE, MENU_ITEM_TEXT_SHOW_LOG_CONSOLE, -1, null));
		
		return toolsMenu;
	}

	private JMenu createHelpMenu() {
		JMenu helpMenu = new JMenu(MENU_TEXT_HELP);
		helpMenu.setName(MENU_NAME_HELP);
		helpMenu.setMnemonic(KeyEvent.VK_H);
		
		helpMenu.add(createMenuItem(MENU_ITEM_NAME_ABOUT, MENU_ITEM_TEXT_ABOUT, -1, null));
		
		return helpMenu;
	}

	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu(MENU_TEXT_FILE);
		fileMenu.setName(MENU_NAME_FILE);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		fileMenu.add(createMenuItem(MENU_ITEM_NAME_NEW, MENU_ITEM_TEXT_NEW, -1,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK)));
		fileMenu.add(createMenuItem(MENU_ITEM_NAME_OPEN_FILE, MENU_ITEM_TEXT_OPEN_FILE, -1, null));
		
		fileMenu.addSeparator();
		
		fileMenu.add(createMenuItem(MENU_ITEM_NAME_SAVE, MENU_ITEM_TEXT_SAVE, -1,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), false));
		fileMenu.add(createMenuItem(MENU_ITEM_NAME_SAVE_AS, MENU_ITEM_TEXT_SAVE_AS, -1, null, false));
		
		fileMenu.addSeparator();
		
		fileMenu.add(createMenuItem(MENU_ITEM_NAME_QUIT, MENU_ITEM_TEXT_QUIT, -1, null));
		
		return fileMenu;
	}
	
	private JMenuItem createMenuItem(String name, String text, int mnemonic, KeyStroke accelerator) {
		return this.createMenuItem(name, text, mnemonic, accelerator, true);
	}
	
	private JMenuItem createMenuItem(String name, String text, int mnemonic, KeyStroke accelerator, boolean enabled) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.setName(name);
		
		if (mnemonic != -1)
			menuItem.setMnemonic(mnemonic);
		
		if (accelerator != null) {			
			menuItem.setAccelerator(accelerator);
		}
		
		menuItem.setActionCommand(name);
		menuItem.addActionListener(this);
		
		menuItem.setEnabled(enabled);
		
		return menuItem;
	}

	@Override
	public void registerThingEmulatorFactory(IThingEmulatorFactory factory) {
		for (IThingEmulatorFactory existedFactory : factories) {
			if (existedFactory.getClass().getName().equals(factory.getClass().getName())) {
				throw new IllegalArgumentException(String.format("Thing factory %s has registered.", factory.getClass().getName()));				
			}
		}
		
		factories.add(factory);
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
		} else if (e.getSource() instanceof LogConsoleDialog) {
			logConsoleDalog.setVisible(false);
			logConsoleDalog.dispose();
			logConsoleDalog = null;
			
			getMenuItem(MENU_NAME_TOOLS, MENU_ITEM_NAME_SHOW_LOG_CONSOLE).setEnabled(true);
		} else {
			// no-op.
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
	}
	
	private void refreshThingSelectionRelativedMenuItems() {
		ThingInternalFrame thingFrame = getSelectedFrame();
		if (thingFrame == null)
			return;
		
		getMenuItem(MENU_NAME_EDIT, MENU_ITEM_NAME_RESET).setEnabled(true);
	}

	private void refreshPowerRelativedMenuItems() {
		ThingInternalFrame thingFrame = getSelectedFrame();
		if (thingFrame == null)
			return;
		
		if (thingFrame.getThing().isPowered()) {			
			getMenuItem(MENU_NAME_EDIT, MENU_ITEM_NAME_POWER_ON).setEnabled(false);
			getMenuItem(MENU_NAME_EDIT, MENU_ITEM_NAME_POWER_OFF).setEnabled(true);			
		} else {
			getMenuItem(MENU_NAME_EDIT, MENU_ITEM_NAME_POWER_OFF).setEnabled(false);			
			getMenuItem(MENU_NAME_EDIT, MENU_ITEM_NAME_POWER_ON).setEnabled(true);
		}
	}
	
	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		setDirty(true);
	}
	
	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		refreshDirtyRelativedMenuItems(dirty);
	}

	private void refreshDirtyRelativedMenuItems(boolean dirty) {
		JMenuItem saveMenuItem = getMenuItem(MENU_NAME_FILE, MENU_ITEM_NAME_SAVE);
		if (dirty) {
			saveMenuItem.setEnabled(true);
		} else {
			saveMenuItem.setEnabled(false);
		}
	}

	private JMenuItem getMenuItem(String menuName, String menuItemName) {
		JMenu menu = getMenu(menuName);
		
		for (MenuElement child : menu.getSubElements()[0].getSubElements()) {
			JMenuItem menuItem = (JMenuItem)child;
			if (menuItem.getName().equals(menuItemName))
				return menuItem;
		}
		
		throw new IllegalArgumentException(String.format("Menu item '%s->%s' not existed.", menuName, menuItemName));
	}

	private JMenu getMenu(String menuName) {
		for (MenuElement child : menuBar.getSubElements()) {
			JMenu menu = (JMenu)child;
			if (menuName.equals(menu.getName())) {
				return menu;
			}
		}
		
		throw new IllegalArgumentException(String.format("Menu '%s' not existed.", menuName));
	}

	@Override
	public void occurred(ConnectionException exception) {
		log(exception);
	}

	@Override
	public void received(String message) {
		log("G<--S " + message);
	}

	@Override
	public void sent(String message) {
		log("G-->S " + message);
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
	public String getDeviceId() {
		return deviceId;
	}

	@Override
	public String getDeviceType() {
		return DEVICE_TYPE;
	}

	@Override
	public String getDeviceMode() {
		return DEVICE_MODE;
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
	public String createNode(Node<A> node) throws NodeCreationException {
		return createNode(node.getThing(), node.getAddress());
	}
	
	@Override
	public String createNode(IThing thing, A address) throws NodeCreationException {
		if (thing == null || address == null) {
			throw new IllegalArgumentException("Null thing or null address.");
		}
		
		if (nodes.size() > 99) {
			throw new NodeCreationException(NodeCreationException.Reason.OVERFLOW_SIZE,
					"Can't create node. Overflow size of nodes.");
		}
		
		if (thingHasAdded(thing)) {
			throw new NodeCreationException(NodeCreationException.Reason.REDUPLICATED_THING,
					String.format("Can't create node. The node which's thing is %s has already added.", thing));
		}
		
		if (addressHasUsed(address)) {
			throw new NodeCreationException(NodeCreationException.Reason.REDUPLICATED_THING,
					String.format("Can't create node. The address %s has already used.", address));
		}
		
		Node<A> node = new Node<>(thing, address);
		String lanId = String.format("%02d", nodes.size());
		nodes.put(lanId, node);
		
		return lanId;
	}

	private boolean addressHasUsed(A address) {
		for (Node<A> node : nodes.values()) {
			if (node.getAddress().equals(address))
				return true;
		}
		
		return false;
	}

	private boolean thingHasAdded(IThing thing) {
		for (Node<A> node : nodes.values()) {
			if (node.getThing().equals(thing))
				return true;
		}
		
		return false;
	}

	@Override
	public Node<A> removeNode(String nodeLanId) {
		return nodes.remove(nodeLanId);
	}

	@Override
	public String[] getChildren() {
		return this.nodes.keySet().toArray(new String[nodes.size()]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Node<A>[] getNodes() {
		return this.nodes.values().toArray(new Node[nodes.size()]);
	}

	@Override
	public Node<A> getNode(String nodeLanId) throws NodeNotFoundException {
		Node<A> node = nodes.get(nodeLanId);
		if (node != null)
			return node;
		
		throw new NodeNotFoundException(String.format("Node which's lan id is % not be found.", nodeLanId));
	}

	@Override
	public void addEventListener(IEventListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(IAction operation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addActionListener(IActionListener<?> listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IAddressConfigurator getAddressConfigurator() {
		return addressConfigurator;
	}
}
