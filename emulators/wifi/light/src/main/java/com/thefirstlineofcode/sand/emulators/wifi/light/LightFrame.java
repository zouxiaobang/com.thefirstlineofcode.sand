package com.thefirstlineofcode.sand.emulators.wifi.light;

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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import com.thefirstlineofcode.chalk.core.stream.StandardStreamConfig;
import com.thefirstlineofcode.sand.client.things.BatteryPowerEvent;
import com.thefirstlineofcode.sand.client.things.IDeviceListener;
import com.thefirstlineofcode.sand.client.things.obm.IObmFactory;
import com.thefirstlineofcode.sand.client.things.obm.ObmFactory;
import com.thefirstlineofcode.sand.emulators.models.Le02ModelDescriptor;
import com.thefirstlineofcode.sand.emulators.things.UiUtils;
import com.thefirstlineofcode.sand.emulators.things.ILight.SwitchState;
import com.thefirstlineofcode.sand.emulators.things.emulators.ISwitchStateListener;
import com.thefirstlineofcode.sand.emulators.things.ui.AboutDialog;
import com.thefirstlineofcode.sand.emulators.things.ui.LightEmulatorPanel;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;

public class LightFrame extends JFrame implements ActionListener, WindowListener,
			IDeviceListener, ISwitchStateListener {
	private static final long serialVersionUID = 6734911253434942398L;
	
	// File Menu
	private static final String MENU_TEXT_FILE = "File";
	private static final String MENU_NAME_FILE = "file";
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
	
	protected IObmFactory obmFactory = ObmFactory.getInstance();
	
	private StandardStreamConfig streamConfig;
	
	private Map<String, ModelDescriptor> registeredModels;
	
	public LightFrame() {
		this(null);
	}
	
	public LightFrame(Light light) {
		super(Light.THING_TYPE);
		
		if (light != null) {
			this.light = light;
		} else {
			this.light = new Light();
		}
		this.light.addDeviceListener(this);
		
		registerModels();
		
		setupUi();
		
		refreshPowerRelativedMenus();
	}
	
	private void registerModels() {
		registeredModels = new HashMap<>();
		Le02ModelDescriptor le02 = new Le02ModelDescriptor();
		registeredModels.put(le02.getName(), le02);
	}

	private void setupUi() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		setDefaultUiFont(new javax.swing.plaf.FontUIResource("Serif", Font.PLAIN, 20));
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width - 640) / 2, (screenSize.height - 480) / 2, 640, 480);
		
		menuBar = createMenuBar();
		setJMenuBar(menuBar);
		
		panel = (LightEmulatorPanel)light.getPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		((LightEmulatorPanel)panel).setSwitchStateListener(this);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);		
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
		
		if (MENU_ITEM_NAME_QUIT.equals(actionCommand)) {
			quit();
		} else if (MENU_ITEM_NAME_POWER_ON.equals(actionCommand)) {
			powerOn();
		} else if (MENU_ITEM_NAME_POWER_OFF.equals(actionCommand)) {
			powerOff();
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
	}
	
	private void powerOff() {
		if (light.isPowered())
			return;
		
		light.powerOff();
		
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
		UiUtils.showDialog(this, new AboutDialog(this, Light.THING_TYPE, Light.SOFTWARE_VERSION));
	}

	private void showLogConsoleDialog() {
		logConsolesDialog = new LogConsolesDialog(this);
		logConsolesDialog.addWindowListener(this);
		
		logConsolesDialog.setVisible(true);
		
		UiUtils.getMenuItem(menuBar, MENU_NAME_TOOLS, MENU_ITEM_NAME_SHOW_LOG_CONSOLE).setEnabled(false);
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
		System.exit(0);
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
	}

	@Override
	public void switchStateChanged(SwitchState oldState, SwitchState newState) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
		
	}
}
