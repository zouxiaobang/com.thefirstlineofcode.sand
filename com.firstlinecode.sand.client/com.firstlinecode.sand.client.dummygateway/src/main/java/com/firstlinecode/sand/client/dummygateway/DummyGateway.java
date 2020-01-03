package com.firstlinecode.sand.client.dummygateway;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.firstlinecode.sand.client.dummything.IDummyThing;
import com.firstlinecode.sand.client.dummything.IDummyThingFactory;

public class DummyGateway extends JFrame implements ActionListener, IDummyGateway {
	private static final long serialVersionUID = -7894418812878036627L;
	
	private static final String ACTION_COMMAND_QUIT = "quit";
	private static final String ACTION_COMMAND_OPEN_FILE = "open_file";
	private static final String ACTION_COMMAND_NEW = "new";
	private static final String ACTION_COMMAND_ABOUT = "about";
	
	private List<IDummyThingFactory<? extends IDummyThing>> factories;
	private Map<String, List<IDummyThing>> allThings;
	
	private JDesktopPane desktop;	
	
	public DummyGateway() {
		super("Unregistered Gateway");
		
		factories = new ArrayList<>();
		allThings = new HashMap<String, List<IDummyThing>>();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width - 1024) / 2, (screenSize.height - 768) / 2, 1024, 768);
		
		desktop = new JDesktopPane();
		
		setContentPane(desktop);
		setJMenuBar(createMenuBar());
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
	}
	
	public void createAndShowGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		if (ACTION_COMMAND_NEW.equals(actionCommand)) {
			createNewThing();
		} else if (ACTION_COMMAND_OPEN_FILE.equals(actionCommand)) {
			openGatewayFile();
		} else if (ACTION_COMMAND_QUIT.equals(actionCommand)) {
			quit();
		} else if (ACTION_COMMAND_ABOUT.equals(actionCommand)) {
			showAboutDialog();
		} else {
			throw new IllegalArgumentException("Illegal action command: " + actionCommand);
		}
	}
	
	private void showAboutDialog() {
		AboutDialog about = new AboutDialog(this, "0.1.0.RELEASE");
		about.setVisible(true);
	}

	private void quit() {
		System.exit(0);
	}

	private void openGatewayFile() {
		// TODO Auto-generated method stub
		
	}

	private void createNewThing() {
		String thingName = (String)JOptionPane.showInputDialog(this, "Choose thing you want to create.",
				"Choose thing", JOptionPane.QUESTION_MESSAGE, null, getThingNames(), null);
		createThing(thingName);
		
	}

	private IDummyThing createThing(String thingName) {
		IDummyThingFactory<? extends IDummyThing> factory = getFactory(thingName);
		IDummyThing thing = factory.create();
		
		List<IDummyThing> things = getThings(factory);
		
		int instanceIndex = things.size();
		thing.setInstanceName(getThingInstanceName(factory, instanceIndex));
		
		things.add(thing);
		
		ThingInternalFrame internalFrame = new ThingInternalFrame(thing, instanceIndex);
		internalFrame.setVisible(true);
		desktop.add(internalFrame);
		try {
			internalFrame.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		
		return thing;
	}
	
	private class ThingInternalFrame extends JInternalFrame {
		private static final long serialVersionUID = 4975138886817512398L;

		public ThingInternalFrame(IDummyThing thing, int instanceIndex) {
			super(thing.getInstanceName(), false, false, false, false);
			
			JPanel thingPanel = thing.getPanel();
			setContentPane(thingPanel);
			setBounds(30 * instanceIndex, 30 * instanceIndex, thingPanel.getPreferredSize().width, thingPanel.getPreferredSize().height);
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
		menuBar.add(createHelpMenu());
		
		return menuBar;
    }

	private JMenu createHelpMenu() {
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		
		JMenuItem aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_A, ActionEvent.ALT_MASK));
		aboutMenuItem.setActionCommand(ACTION_COMMAND_ABOUT);
		aboutMenuItem.addActionListener(this);
		helpMenu.add(aboutMenuItem);
		
		return helpMenu;
	}

	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem newMenuItem = new JMenuItem("New");
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, ActionEvent.ALT_MASK));
		newMenuItem.setActionCommand(ACTION_COMMAND_NEW);
		newMenuItem.addActionListener(this);
		fileMenu.add(newMenuItem);
		
		JMenuItem openFileMenuItem = new JMenuItem("Open file...");
		openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, ActionEvent.ALT_MASK));
		openFileMenuItem.setActionCommand(ACTION_COMMAND_OPEN_FILE);
		openFileMenuItem.addActionListener(this);
		fileMenu.add(openFileMenuItem);
		
		openFileMenuItem = new JMenuItem("Quit");
		openFileMenuItem.setMnemonic(KeyEvent.VK_Q);
		openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		openFileMenuItem.setActionCommand(ACTION_COMMAND_QUIT);
		openFileMenuItem.addActionListener(this);
		fileMenu.add(openFileMenuItem);
		return fileMenu;
	}

	@Override
	public void registerThingFactory(IDummyThingFactory<? extends IDummyThing> factory) {
		for (IDummyThingFactory<?> existedFactory : factories) {
			if (existedFactory.getClass().getName().equals(factory.getClass().getName())) {
				throw new IllegalArgumentException(String.format("Thing factory %s has registered.", factory.getClass().getName()));				
			}
		}
		
		factories.add(factory);
	}
}
