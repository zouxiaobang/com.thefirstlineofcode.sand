package com.firstlinecode.sand.emulators.lora.gateway.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.firstlinecode.chalk.IChatClient;
import com.firstlinecode.chalk.network.IConnectionListener;
import com.firstlinecode.sand.client.lora.IDualLoraChipsCommunicator;
import com.firstlinecode.sand.client.things.commuication.ICommunicationNetwork;
import com.firstlinecode.sand.client.things.commuication.ICommunicationNetworkListener;
import com.firstlinecode.sand.emulators.lora.thing.AbstractLoraThingEmulator;
import com.firstlinecode.sand.protocols.core.ModeDescriptor;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class LogConsolesDialog extends JDialog {
	private static final long serialVersionUID = 5197344780011371803L;
	
	public static final String NAME_INTERNET = "Internet";
	public static final String NAME_COMMUNICATION_NETWORK = "Communication Network";
	public static final String NAME_GATEWAY = "Gateway";
	
	private JTabbedPane tabbedPane;
	private Map<String, AbstractLogConsolePanel> logConsoles;
	private Map<String, ModeDescriptor> modes;
	
	public LogConsolesDialog(JFrame parent, Map<String, ModeDescriptor> modes, IChatClient chatClient,
							 ICommunicationNetwork<LoraAddress, byte[], ?> network,
							 IDualLoraChipsCommunicator gatewayCommunicator,
							 Map<String, List<AbstractLoraThingEmulator>> allThings) {
		super(parent, "Log Console");

		this.modes = modes;
		logConsoles = new HashMap<>();
		
		setUi();

		createLogConsoles(chatClient, network, gatewayCommunicator, allThings);
	}
	
	private void setUi() {
		tabbedPane = new JTabbedPane();
		getContentPane().add(tabbedPane);
		
		setBounds(50, 50, 800, 480);
	}

	private void createLogConsoles(IChatClient chatClient, ICommunicationNetwork<LoraAddress, byte[], ?> network,
			IDualLoraChipsCommunicator gatewayCommunicator, Map<String, List<AbstractLoraThingEmulator>> allThings) {
		createInternetLogConsole(chatClient);
		createCommunicationNetworkLogConsole(network);
		createGatewayConsole(gatewayCommunicator);
		createThingLogConsoles(allThings);
	}

	private void createThingLogConsoles(Map<String, List<AbstractLoraThingEmulator>> allThings) {
		for (List<AbstractLoraThingEmulator> things : allThings.values()) {
			for (AbstractLoraThingEmulator thing : things) {
				createThingLogConsole(thing);
			}
		}
	}

	public void createThingLogConsole(AbstractLoraThingEmulator thing) {
		createLogConsole(thing.getDeviceId(), new ThingLogConsolePanel(thing, modes.get(thing.getMode())));
	}

	private void createGatewayConsole(IDualLoraChipsCommunicator gatewayCommunicator) {
		createLogConsole(NAME_GATEWAY, new GatewayLogConsolePanel(gatewayCommunicator, modes));
	}

	@SuppressWarnings("unchecked")
	private void createCommunicationNetworkLogConsole(ICommunicationNetwork<LoraAddress, byte[], ?> network) {
		createLogConsole(NAME_COMMUNICATION_NETWORK, new CommunicationNetworkLogConsolePanel(network, modes));
		network.addListener((ICommunicationNetworkListener<LoraAddress, byte[]>)logConsoles.get(NAME_COMMUNICATION_NETWORK));
	}
	
	private void createInternetLogConsole(IChatClient chatClient) {
		createLogConsole(NAME_INTERNET, new InternetLogConsolePanel(chatClient));
	}
	
	private void createLogConsole(String name, AbstractLogConsolePanel logConsole) {
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
	
	public IConnectionListener getConnectionListener() {
		return (IConnectionListener)logConsoles.get(NAME_INTERNET);
	}
	
	public void removeThingLogConsole(AbstractLoraThingEmulator thing) {
		ThingLogConsolePanel logConsole = (ThingLogConsolePanel)logConsoles.remove(thing.getDeviceId());
		tabbedPane.remove(logConsole);
	}
	
	public void thingRemoved(AbstractLoraThingEmulator thing) {
		ThingLogConsolePanel logConsole = (ThingLogConsolePanel)logConsoles.remove(thing.getDeviceId());
		logConsole.thingRemoved(thing);
	}
}
