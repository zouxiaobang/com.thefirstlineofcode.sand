package com.firstlinecode.sand.emulators.things.ui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.firstlinecode.chalk.core.IChatClient;
import com.firstlinecode.chalk.network.IConnectionListener;

public abstract class AbstractLogConsolesDialog extends JDialog {
	private static final long serialVersionUID = 5197344780011371803L;
	
	public static final String NAME_INTERNET = "Internet";
	
	protected JTabbedPane tabbedPane;
	protected Map<String, AbstractLogConsolePanel> logConsoles;
	
	protected IChatClient chatClient;
	
	public AbstractLogConsolesDialog(JFrame parent, IChatClient chatClient) {
		super(parent, "Log Console");
		
		this.chatClient = chatClient;
		logConsoles = new HashMap<>();
		
		setUi();
	}
	
	private void setUi() {
		tabbedPane = new JTabbedPane();
		getContentPane().add(tabbedPane);
		
		setBounds(50, 50, 800, 480);
	}

	protected abstract void createPreinstlledLogConsoles();
	
	protected void createInternetLogConsole(IChatClient chatClient) {
		createLogConsole(NAME_INTERNET, new InternetLogConsolePanel(chatClient));
	}
	
	public void createLogConsole(String name, AbstractLogConsolePanel logConsole) {
		if (logConsoles.containsKey(name)) {
			throw new IllegalArgumentException(String.format("Logger '%s' has existed.", name));
		}
		
		tabbedPane.addTab(name, logConsole);
		logConsoles.put(name, logConsole);
		addWindowListener(logConsole);
	}
	
	public void removeLogConsole(String name) {
		AbstractLogConsolePanel logConsole = logConsoles.get(name);
		if (logConsole != null) {
			tabbedPane.remove(logConsole);
		}
	}
	
	public IConnectionListener getInternetConnectionListener() {
		return (IConnectionListener)logConsoles.get(NAME_INTERNET);
	}
}
