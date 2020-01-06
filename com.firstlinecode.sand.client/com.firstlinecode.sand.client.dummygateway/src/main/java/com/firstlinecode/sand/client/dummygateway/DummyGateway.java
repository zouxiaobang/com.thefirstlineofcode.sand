package com.firstlinecode.sand.client.dummygateway;

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
import javax.swing.MenuElement;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FontUIResource;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.sand.client.dummything.AbstractDummyThing;
import com.firstlinecode.sand.client.dummything.AbstractDummyThingPanel;
import com.firstlinecode.sand.client.dummything.IDummyThing;
import com.firstlinecode.sand.client.dummything.IDummyThingFactory;
import com.firstlinecode.sand.client.dummything.StatusBar;
import com.firstlinecode.sand.client.dummything.ThingsUtils;

public class DummyGateway extends JFrame implements ActionListener, InternalFrameListener, ComponentListener, WindowListener, IDummyGateway {
	private static final long serialVersionUID = -7894418812878036627L;

	private static final String MENU_TEXT_FILE = "File";
	private static final String MENU_ITEM_TEXT_NEW = "New";
	private static final String MENU_ITEM_TEXT_OPEN_FILE = "Open file...";
	private static final String MENU_ITEM_TEXT_SAVE = "Save";
	private static final String MENU_ITEM_TEXT_SAVE_AS = "Save As...";
	private static final String MENU_ITEM_TEXT_QUIT = "Quit";

	private static final String MENU_TEXT_EDIT = "Edit";
	private static final String MENU_ITEM_TEXT_POWER_ON = "Power On";
	private static final String MENU_ITEM_TEXT_POWER_OFF = "Power Off";
	private static final String MENU_ITEM_TEXT_DELETE = "Delete";

	private static final String MENU_TEXT_HELP = "Help";
	private static final String MENU_ITEM_TEXT_ABOUT = "About";
	
	private static final String MENU_NAME_FILE = "file";
	private static final String MENU_NAME_EDIT = "edit";
	private static final String MENU_NAME_HELP = "help";
	
	private static final String MENU_ITEM_NAME_NEW = "new";
	private static final String MENU_ITEM_NAME_OPEN_FILE = "open_file";
	private static final String MENU_ITEM_NAME_SAVE = "save";
	private static final String MENU_ITEM_NAME_SAVE_AS = "save_as";
	private static final String MENU_ITEM_NAME_QUIT = "quit";
	private static final String MENU_ITEM_NAME_POWER_ON = "power_on";
	private static final String MENU_ITEM_NAME_POWER_OFF = "power_off";
	private static final String MENU_ITEM_NAME_DELETE = "delete";
	private static final String MENU_ITEM_NAME_ABOUT = "about";
	
	private String deviceId;
	private JabberId jid;
	
	private List<IDummyThingFactory<? extends IDummyThing>> factories;
	private Map<String, List<IDummyThing>> allThings;
	private boolean dirty;
	
	private JDesktopPane desktop;
	private JMenuBar menuBar;
	private StatusBar statusBar;
	
	private File configFile;
	
	public DummyGateway() {
		super("Unregistered Gateway");
		
		deviceId = ThingsUtils.generateRandomDeviceId();
		
		factories = new ArrayList<>();
		allThings = new HashMap<String, List<IDummyThing>>();
		dirty = false;
		
		setupUi();
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
		
		statusBar = createStatusBar();
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
		if (jid == null) {
			sb.append("Unregistered").append(", ");
		} else {
			sb.append("Registered: ").append(jid.getName()).append(", ");
		}
		
		sb.append("Device ID: ").append(deviceId).append(", ");
		
		return sb.toString().substring(0, sb.length() - 2);
	}

	private StatusBar createStatusBar() {
		return new StatusBar();
	}

	public void createAndShowGUI() {
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		if (MENU_ITEM_NAME_NEW.equals(actionCommand)) {
			createNewThing();
		} else if (MENU_ITEM_NAME_OPEN_FILE.equals(actionCommand)) {
			openFile();
		} else if (MENU_ITEM_NAME_QUIT.equals(actionCommand)) {
			quit();
		} else if (MENU_ITEM_NAME_ABOUT.equals(actionCommand)) {
			showAboutDialog();
		} else if (MENU_ITEM_NAME_SAVE.equals(actionCommand)) {
			save();
		} else if (MENU_ITEM_NAME_SAVE_AS.equals(actionCommand)) {
			saveAs();
		} else {
			throw new IllegalArgumentException("Illegal action command: " + actionCommand);
		}
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
			output.writeInt(frames.length);
			for (JInternalFrame frame : frames) {
				ThingInternalFrame thingFrame = (ThingInternalFrame)frame;
				output.writeObject(new DummyThingInfo(thingFrame.getLayer(), thingFrame.getX(), thingFrame.getY(), thingFrame.isSelected(), thingFrame.getThing()));
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format("Gateway info file %s doesn't exist.", file.getPath()));
		} catch (IOException e) {
			throw new RuntimeException("Can't save gateway info file.");
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
		AboutDialog about = new AboutDialog(this, "0.1.0.RELEASE");
		about.setVisible(true);
	}

	private void quit() {
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
			setDirtyInUiThread(false);
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {					
					JInternalFrame[] frames = DummyGateway.this.desktop.getAllFrames();
					for (JInternalFrame frame : frames) {
						frame.addComponentListener(DummyGateway.this);
						frame.addInternalFrameListener(DummyGateway.this);
					}
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
		List<DummyThingInfo> thingInfos = readThingInfos(file);
		
		for (DummyThingInfo thingInfo : thingInfos) {
			showThing(thingInfo.getThing(), thingInfo.getLayer(), thingInfo.getX(), thingInfo.getY(), thingInfo.isSelected());
		}
		
		setConfigFile(file);
	}

	private void setConfigFile(File file) {
		if (configFile == null && file != null) {
			getMenuItem(MENU_NAME_FILE, MENU_ITEM_NAME_SAVE_AS).setEnabled(true);
		}
		
		configFile = file;
	}

	private List<DummyThingInfo> readThingInfos(File file) {
		List<DummyThingInfo> thingInfos = new ArrayList<>();
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new FileInputStream(file));
			int size = input.readInt();
			for (int i = 0; i < size; i++) {
				DummyThingInfo thingInfo = (DummyThingInfo)input.readObject();
				thingInfos.add(thingInfo);
			}
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format("Gateway info file %s doesn't exist.", file.getPath()));
		} catch (IOException e) {
			throw new RuntimeException("Can't load gateway info file.");
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
		
		return thingInfos;
	}

	private void createNewThing() {
		String thingName = (String)JOptionPane.showInputDialog(this, "Choose thing you want to create",
				"Choose thing", JOptionPane.QUESTION_MESSAGE, null, getThingNames(), null);
		createThing(thingName);
		
	}
	
	private IDummyThing createThing(String thingName) {
		IDummyThingFactory<? extends IDummyThing> factory = getFactory(thingName);
		IDummyThing thing = factory.create();
		
		List<IDummyThing> things = getThings(factory);
		
		int instanceIndex = things.size();
		thing.setName(getThingInstanceName(factory, instanceIndex));
		thing.powerOn();
		
		things.add(thing);
		
		showThing(thing, -1, 30 * instanceIndex, 30 * instanceIndex);
		setDirtyInUiThread(true);				
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JInternalFrame selectedFrame = DummyGateway.this.desktop.getSelectedFrame();				
				selectedFrame.addComponentListener(DummyGateway.this);
				selectedFrame.addInternalFrameListener(DummyGateway.this);			
			}
		});
		
		return thing;
	}
	
	private void showThing(IDummyThing thing, int layer, int x, int y) {
		this.showThing(thing, layer, x, y, true);
	}

	private void showThing(IDummyThing thing, int layer, int x, int y, boolean selected) {
		AbstractDummyThingPanel thingPanel = thing.getPanel();
		ThingInternalFrame internalFrame = new ThingInternalFrame(thing);		
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
		
		
		if (thing instanceof AbstractDummyThing)
			thingPanel.updateStatus(((AbstractDummyThing)thing).getThingStatus());
	}
	
	private void setDirtyInUiThread(boolean dirty) {
		try {
			SwingUtilities.invokeLater(new DirtySetter(dirty));
		} catch (Exception e) {
			throw new RuntimeException("Can't add component listener to thing internal frame.");
		}
	}
	
	private class DirtySetter implements Runnable {
		private boolean dirty;
		
		public DirtySetter(boolean dirty) {
			this.dirty = dirty;
		}
		
		@Override
		public void run() {
			DummyGateway.this.setDirty(dirty);
		}
	}

	private String getThingInstanceName(IDummyThingFactory<? extends IDummyThing> factory, int thingsSize) {
		return factory.getThingName() + " #" + thingsSize;
	}

	private List<IDummyThing> getThings(IDummyThingFactory<? extends IDummyThing> factory) {
		List<IDummyThing> things = allThings.get(factory.getThingName());
		if (things == null) {
			things = new ArrayList<IDummyThing>();
			allThings.put(factory.getThingName(), things);
		}
		return things;
	}

	private IDummyThingFactory<? extends IDummyThing> getFactory(String thingName) {
		for (IDummyThingFactory<? extends IDummyThing> factory : factories) {
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
		menuBar.add(createHelpMenu());
		
		return menuBar;
    }

	private JMenu createEditMenu() {
		JMenu editMenu = new JMenu(MENU_TEXT_EDIT);
		editMenu.setName(MENU_NAME_EDIT);
		editMenu.setMnemonic(KeyEvent.VK_E);
		
		editMenu.add(createMenuItem(MENU_ITEM_NAME_POWER_ON, MENU_ITEM_TEXT_POWER_ON, -1,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK), false));
		editMenu.add(createMenuItem(MENU_ITEM_NAME_POWER_OFF, MENU_ITEM_TEXT_POWER_OFF, -1,
				KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.ALT_MASK), false));
		
		editMenu.addSeparator();
		
		editMenu.add(createMenuItem(MENU_ITEM_NAME_DELETE, MENU_ITEM_TEXT_DELETE, -1,
				KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK), false));
		
		return editMenu;
	}

	private JMenu createHelpMenu() {
		JMenu helpMenu = new JMenu(MENU_TEXT_HELP);
		helpMenu.setName(MENU_NAME_HELP);
		helpMenu.setMnemonic(KeyEvent.VK_H);
		
		helpMenu.add(createMenuItem(MENU_ITEM_NAME_ABOUT, MENU_ITEM_TEXT_ABOUT, -1,
				KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK)));
		
		return helpMenu;
	}

	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu(MENU_TEXT_FILE);
		fileMenu.setName(MENU_NAME_FILE);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		fileMenu.add(createMenuItem(MENU_ITEM_NAME_NEW, MENU_ITEM_TEXT_NEW, -1,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK)));		
		fileMenu.add(createMenuItem(MENU_ITEM_NAME_OPEN_FILE, MENU_ITEM_TEXT_OPEN_FILE, -1,
				KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK)));
				
		fileMenu.addSeparator();
		
		fileMenu.add(createMenuItem(MENU_ITEM_NAME_SAVE, MENU_ITEM_TEXT_SAVE, -1,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), false));
		fileMenu.add(createMenuItem(MENU_ITEM_NAME_SAVE_AS, MENU_ITEM_TEXT_SAVE_AS, -1,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), false));
		
		fileMenu.addSeparator();
		
		fileMenu.add(createMenuItem(MENU_ITEM_NAME_QUIT, MENU_ITEM_TEXT_QUIT, KeyEvent.VK_Q,
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK)));
		
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
	public void registerThingFactory(IDummyThingFactory<?> factory) {
		for (IDummyThingFactory<?> existedFactory : factories) {
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
		quit();
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
		System.out.println("aaaaaaaaaaaaaaaaaaaaaa");
	}
	
	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		setDirty(true);
	}
	
	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		
		JMenuItem saveMenuItem = getMenuItem(MENU_NAME_FILE, MENU_ITEM_NAME_SAVE);
		if (dirty) {
			if (!saveMenuItem.isEnabled())
				saveMenuItem.setEnabled(true);
		} else {
			if (saveMenuItem.isEnabled())
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

	
}
