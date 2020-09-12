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
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import com.firstlinecode.chalk.IChatClient;
import com.firstlinecode.sand.client.things.ThingsUtils;
import com.firstlinecode.sand.emulators.things.Constants;
import com.firstlinecode.sand.emulators.things.ILight;
import com.firstlinecode.sand.emulators.things.NotRemoteControlStateException;
import com.firstlinecode.sand.emulators.things.NotTurnOffStateException;
import com.firstlinecode.sand.emulators.things.UiUtils;
import com.firstlinecode.sand.emulators.things.ui.AboutDialog;
import com.firstlinecode.sand.emulators.things.ui.StatusBar;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public class Light extends JFrame implements ILight, ActionListener, WindowListener {
	private static final long serialVersionUID = 6734911253434942398L;
	
	private static final String THING_NAME = "WIFI Light Emulator";
	private static final String THING_MODE = "LE02";
	
	private static final SwitchState DEFAULT_SWITCH_STATE = SwitchState.OFF;
	private static final LightState DEFAULT_LIGHT_STATE = LightState.OFF;
	
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
	private static final String MENU_ITEM_TEXT_RESET = "Reset";
	private static final String MENU_ITEM_NAME_RESET = "reset";
	
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
	private StatusBar statusBar;
	private LogConsolesDialog logConsolesDialog;
	
	private String deviceId;
	private DeviceIdentity deviceIdentity;
	private boolean dirty;
	
	private SwitchState switchState = DEFAULT_SWITCH_STATE;
	private LightState lightState = DEFAULT_LIGHT_STATE;
	
	private IChatClient chatClient;
	
	public Light() {
		super(THING_NAME);
		
		deviceId = generateDeviceId();
		dirty = false;
		
		setupUi();
	}
	
	private String generateDeviceId() {
		return getMode() + ThingsUtils.generateRandomId(8);
	}

	private void setupUi() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		setDefaultUiFont(new javax.swing.plaf.FontUIResource("Serif", Font.PLAIN, 20));
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width - 1024) / 2, (screenSize.height - 768) / 2, 1024, 768);
		
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
		editMenu.add(UiUtils.createMenuItem(MENU_ITEM_NAME_RESET, MENU_ITEM_TEXT_RESET, -1, null, this, false));

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
	public SwitchState getSwitchState() {
		return switchState;
	}

	@Override
	public LightState getLightState() {
		// TODO Auto-generated method stub
		return null;
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
	public void flash() throws NotRemoteControlStateException, NotTurnOffStateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDeviceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMode() {
		return THING_MODE;
	}

	@Override
	public String getSoftwareVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBatteryPower() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void powerOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void powerOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPowered() {
		// TODO Auto-generated method stub
		return false;
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
		} else if (MENU_ITEM_NAME_RESET.equals(actionCommand)) {
			reset();
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

	private void showAboutDialog() {
		UiUtils.showDialog(this, new AboutDialog(this, THING_NAME, Constants.SOFTWARE_VERSION));
	}

	private void showLogConsoleDialog() {
		// TODO Auto-generated method stub
		
	}

	private void disconnect() {
		// TODO Auto-generated method stub
		
	}

	private void connect() {
		// TODO Auto-generated method stub
		
	}

	private void register() {
		// TODO Auto-generated method stub
		
	}

	private void reset() {
		// TODO Auto-generated method stub
		
	}

	private void saveAs() {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
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
}
