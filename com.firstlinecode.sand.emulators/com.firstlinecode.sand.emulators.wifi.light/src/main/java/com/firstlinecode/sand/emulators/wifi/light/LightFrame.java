package com.firstlinecode.sand.emulators.wifi.light;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
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
import com.firstlinecode.sand.client.actuator.ActuatorPlugin;
import com.firstlinecode.sand.client.ibdr.IRegistration;
import com.firstlinecode.sand.client.ibdr.IbdrPlugin;
import com.firstlinecode.sand.client.ibdr.RegistrationException;
import com.firstlinecode.sand.client.things.BatteryPowerEvent;
import com.firstlinecode.sand.client.things.IDeviceListener;
import com.firstlinecode.sand.client.things.obm.IObmFactory;
import com.firstlinecode.sand.client.things.obm.ObmFactory;
import com.firstlinecode.sand.emulators.things.ILight.SwitchState;
import com.firstlinecode.sand.emulators.things.UiUtils;
import com.firstlinecode.sand.emulators.things.emulators.ISwitchStateListener;
import com.firstlinecode.sand.emulators.things.emulators.StreamConfigInfo;
import com.firstlinecode.sand.emulators.things.ui.AboutDialog;
import com.firstlinecode.sand.emulators.things.ui.LightEmulatorPanel;
import com.firstlinecode.sand.emulators.things.ui.StreamConfigDialog;

public class LightFrame extends JFrame implements ActionListener, WindowListener,
			IDeviceListener, ISwitchStateListener {
	private static final long serialVersionUID = 6734911253434942398L;
	
	// File Menu
	private static final String MENU_TEXT_FILE = "File";
	private static final String MENU_NAME_FILE = "file";
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
	
	private JMenuBar menuBar;
	private LogConsolesDialog logConsolesDialog;
	
	private LightEmulatorPanel panel;
	
	private Light light;
	private boolean dirty;
	
	protected IObmFactory obmFactory = ObmFactory.createInstance();
	
	private IChatClient chatClient;
	private StandardStreamConfig streamConfig;
	
	private File configFile;
	
	public LightFrame() {
		this(null);
	}
	
	public LightFrame(Light light) {
		super(Light.THING_NAME);
		
		if (light != null) {
			this.light = light;
			dirty = false;
		} else {
			this.light = new Light(Light.THING_MODEL);
			this.light.powerOn();
			dirty = true;
		}
		this.light.addDeviceListener(this);
		
		setupUi();
		
		refreshLightInstanceRelativatedMenus();
		refreshPowerRelativedMenus();
		refreshDirtyRelativedMenuItems();
	}

	private void setupUi() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		setDefaultUiFont(new javax.swing.plaf.FontUIResource("Serif", Font.PLAIN, 20));
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width - 1024) / 2, (screenSize.height - 768) / 2, 1024, 768);
		
		menuBar = createMenuBar();
		setJMenuBar(menuBar);
		
		panel = (LightEmulatorPanel)light.getPanel();
		add(panel, BorderLayout.CENTER);
		((LightEmulatorPanel)panel).setSwitchStateListener(this);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		
		refreshLightInstanceRelativatedMenus();
	}

	private JMenuBar createMenuBar() {
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
		
		fileMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_OPEN_FILE, MENU_ITEM_TEXT_OPEN_FILE, -1, null, this));
		
		fileMenu.addSeparator();
		
		fileMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_SAVE, MENU_ITEM_TEXT_SAVE, -1,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), this, false));
		fileMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_SAVE_AS, MENU_ITEM_TEXT_SAVE_AS, -1, null, this, false));
		
		fileMenu.addSeparator();
		
		fileMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_QUIT, MENU_ITEM_TEXT_QUIT, -1, null, this));
		
		return fileMenu;
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String actionCommand = e.getActionCommand();
		
		if (MENU_ITEM_NAME_OPEN_FILE.equals(actionCommand)) {
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
	
	private void powerOn() {
		if (light.isPowered())
			return;
		
		light.powerOn();
		dirty = true;
	}
	
	private void powerOff() {
		if (light.isPowered())
			return;
		
		light.powerOff();
		dirty = true;
		
		refreshPowerRelativedMenus();
	}
	
	private void refreshPowerRelativedMenus() {
		if (light.isPowered()) {
			UiUtils.getMenuItem(menuBar, MENU_NAME_EDIT, MENU_ITEM_NAME_POWER_ON).setEnabled(false);
			UiUtils.getMenuItem(menuBar, MENU_NAME_EDIT, MENU_ITEM_NAME_POWER_OFF).setEnabled(true);
		} else {
			UiUtils.getMenuItem(menuBar, MENU_NAME_EDIT, MENU_ITEM_NAME_POWER_OFF).setEnabled(false);
			UiUtils.getMenuItem(menuBar, MENU_NAME_EDIT, MENU_ITEM_NAME_POWER_ON).setEnabled(true);
		}
	}

	private void showAboutDialog() {
		UiUtils.showDialog(this, new AboutDialog(this, Light.THING_NAME, Light.SOFTWARE_VERSION));
	}

	private void showLogConsoleDialog() {
		logConsolesDialog = new LogConsolesDialog(this, chatClient);
		logConsolesDialog.addWindowListener(this);
		
		logConsolesDialog.setVisible(true);
		
		UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_SHOW_LOG_CONSOLE).setEnabled(false);
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
	
	private void register() {
		if (light.getDeviceIdentity() != null)
			throw new IllegalStateException("Light has already registered.");
		
		if (streamConfig == null) {
			StreamConfigDialog streamConfigDialog = new StreamConfigDialog(this);
			UiUtils.showDialog(this, streamConfigDialog);
			
			streamConfig = streamConfigDialog.getStreamConfig();
		}
		
		if (streamConfig != null) {
			doRegister();	
		}
		
		if (light.getDeviceIdentity() != null)
			UiUtils.showNotification(this, "Message", "Light has registered.");
	}
	
	private void doRegister() {
		if (streamConfig == null)
			throw new IllegalArgumentException("Null stream config.");
		
		IChatClient chatClient = new StandardChatClient(streamConfig);
		chatClient.register(IbdrPlugin.class);
		IRegistration registration = chatClient.createApi(IRegistration.class);
		adddInternetLogListener(registration);
		
		try {
			light.setDeviceIdentity(registration.register(light.getDeviceId()));
		} catch (RegistrationException e) {
			JOptionPane.showMessageDialog(this, "Can't register device. Error: " + e.getError(), "Registration Error", JOptionPane.ERROR_MESSAGE);
		} finally {			
			registration.removeConnectionListener(getLogConsoleInternetConnectionListener());
			chatClient.close();
		}
		
		dirty = true;
		refreshLightInstanceRelativatedMenus();
		panel.updateStatus();
	}
	
	private void refreshLightInstanceRelativatedMenus() {
		if (light.getDeviceIdentity() != null) {
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
	
	private boolean isConnected() {
		return chatClient != null && chatClient.isConnected();
	}
	
	private void adddInternetLogListener(IRegistration registration) {
		IConnectionListener logListener = getLogConsoleInternetConnectionListener();
		if (logListener != null) {
			registration.addConnectionListener(logListener);
		}
	}

	private void disconnect() {
		if (chatClient == null || chatClient.isClosed())
			throw new IllegalStateException("Light has already disconnected.");
		
		doDisconnect();
		refreshConnectionStateRelativatedMenus();
		panel.updateStatus();
		UiUtils.showNotification(this, "Message", "Light has disconnected.");
	}

	private void connect() {
		// TODO Auto-generated method stub
		if (chatClient != null && chatClient.isConnected())
			throw new IllegalStateException("Gateway has already connected.");
		
		try {
			doConnect();
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
		
		chatClient.connect(new UsernamePasswordToken(light.getDeviceIdentity().getDeviceName().toString(),
				light.getDeviceIdentity().getCredentials()));
				
		refreshConnectionStateRelativatedMenus();
		panel.updateStatus();
	}
	
	private void refreshConnectionStateRelativatedMenus() {
		if (chatClient != null && chatClient.isConnected()) {
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_CONNECT).setEnabled(false);			
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_DISCONNECT).setEnabled(true);			
			
			refreshLightInstanceRelativatedMenus();
		} else {
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_DISCONNECT).setEnabled(false);			
			UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_CONNECT).setEnabled(true);
		}
	}
	
	private IChatClient createChatClient() {
		if (light.getDeviceIdentity() == null)
			throw new IllegalStateException("Device identity is null. Please register your light.");
		
		StandardStreamConfig streamConfigWithResource = createStreamConfigWithResource();
		IChatClient chatClient = new StandardChatClient(streamConfigWithResource);
		
		registerPlugins(chatClient);
		
		return chatClient;
	}
	
	private void registerPlugins(IChatClient chatClient) {
		chatClient.register(ActuatorPlugin.class);
	}
	
	private StandardStreamConfig createStreamConfigWithResource() {
		StandardStreamConfig cloned = new StandardStreamConfig(streamConfig.getHost(), streamConfig.getPort());
		cloned.setTlsPreferred(streamConfig.isTlsPreferred());
		cloned.setResource(light.getThingName());
		
		return cloned;
	}

	private void saveAs() {
		JFileChooser fileChooser = createFileChooser();
		fileChooser.setDialogTitle("Choose a file to save your WIFI light info");
		File defaultDirectory = FileSystemView.getFileSystemView().getDefaultDirectory();
		fileChooser.setSelectedFile(new File(defaultDirectory, light.getDeviceId() + ".wli"));
		
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
				return f.getName().endsWith(".wli");
			}

			@Override
			public String getDescription() {
				return "WIFI light info file (.wli)";
			}
		});
		
		return fileChooser;
	}

	private void openFile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

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

	private void quit() {
		doDisconnect();
		
		if (!dirty)
			System.exit(0);
		
		int result = JOptionPane.showConfirmDialog(this, "Light info has changed. Do you want to save the change?");
		if (result == JOptionPane.CANCEL_OPTION) {
			return;
		} else if (result == JOptionPane.NO_OPTION) {
			System.exit(0);
		} else {
			save();
			System.exit(0);
		}
	}

	private void doDisconnect() {
		if (chatClient != null) {
			chatClient.close();
		}
	}

	private void save() {
		if (configFile == null)
			saveAs();
		else
			saveToFile(configFile);
	}
	
	private void saveToFile(File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e3) {
				throw new RuntimeException("Can't create light info file " + file.getPath());
			}
		}
		
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream(file));
			output.writeObject(light);
			
			if (streamConfig != null) {
				output.writeObject(new StreamConfigInfo(streamConfig.getHost(), streamConfig.getPort(), streamConfig.isTlsPreferred()));
			} else {
				output.writeObject(null);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format("Light info file %s doesn't exist.", file.getPath()));
		} catch (IOException e) {
			throw new RuntimeException("Can't save light info file.", e);
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
		refreshDirtyRelativedMenuItems();
	}
	
	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		refreshDirtyRelativedMenuItems();
	}
	
	private void refreshDirtyRelativedMenuItems() {
		JMenuItem saveMenuItem = UiUtils.getMenuItem(menuBar, MENU_NAME_FILE, MENU_ITEM_NAME_SAVE);
		if (dirty) {
			saveMenuItem.setEnabled(true);
		} else {
			saveMenuItem.setEnabled(false);
		}
	}
	
	private void setConfigFile(File file) {
		if (configFile == null && file != null) {
			UiUtils.getMenuItem(menuBar, MENU_NAME_FILE, MENU_ITEM_NAME_SAVE_AS).setEnabled(true);
		}
			
		configFile = file;
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
	public void batteryPowerChanged(BatteryPowerEvent event) {
		setDirty(dirty);
	}

	@Override
	public void switchStateChanged(SwitchState oldState, SwitchState newState) {
		setDirty(dirty);
	}
}
