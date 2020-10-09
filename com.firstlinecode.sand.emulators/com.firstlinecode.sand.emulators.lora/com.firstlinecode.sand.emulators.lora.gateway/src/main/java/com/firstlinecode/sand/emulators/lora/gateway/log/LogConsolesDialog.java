package com.firstlinecode.sand.emulators.lora.gateway.log;

import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import com.firstlinecode.chalk.IChatClient;
import com.firstlinecode.sand.client.lora.IDualLoraChipsCommunicator;
import com.firstlinecode.sand.client.things.commuication.ICommunicationNetwork;
import com.firstlinecode.sand.client.things.commuication.ICommunicationNetworkListener;
import com.firstlinecode.sand.emulators.lora.things.AbstractLoraThingEmulator;
import com.firstlinecode.sand.emulators.things.ui.AbstractLogConsolesDialog;
import com.firstlinecode.sand.protocols.core.ModelDescriptor;
import com.firstlinecode.sand.protocols.lora.LoraAddress;

public class LogConsolesDialog extends AbstractLogConsolesDialog {
	private static final long serialVersionUID = 5197344780011371803L;
	
	public static final String NAME_COMMUNICATION_NETWORK = "Communication Network";
	public static final String NAME_GATEWAY = "Gateway";
	
	private Map<String, ModelDescriptor> models;
	
	private ICommunicationNetwork<LoraAddress, byte[], ?> network;
	private IDualLoraChipsCommunicator gatewayCommunicator;
	private Map<String, List<AbstractLoraThingEmulator>> allThings;
	
	public LogConsolesDialog(JFrame parent, IChatClient chatClient, Map<String, ModelDescriptor> models,
			ICommunicationNetwork<LoraAddress, byte[], ?> network,
			IDualLoraChipsCommunicator gatewayCommunicator,
			Map<String, List<AbstractLoraThingEmulator>> allThings) {
		super(parent, chatClient);
		
		this.models = models;
		this.network = network;
		this.gatewayCommunicator = gatewayCommunicator;
		this.allThings = allThings;
		
		createPreinstlledLogConsoles();
	}

	protected void createPreinstlledLogConsoles() {
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
		createLogConsole(thing.getDeviceId(), new ThingLogConsolePanel(thing, models.get(thing.getModel())));
	}

	private void createGatewayConsole(IDualLoraChipsCommunicator gatewayCommunicator) {
		createLogConsole(NAME_GATEWAY, new GatewayLogConsolePanel(gatewayCommunicator, models));
	}

	@SuppressWarnings("unchecked")
	private void createCommunicationNetworkLogConsole(ICommunicationNetwork<LoraAddress, byte[], ?> network) {
		createLogConsole(NAME_COMMUNICATION_NETWORK, new CommunicationNetworkLogConsolePanel(network, models));
		network.addListener((ICommunicationNetworkListener<LoraAddress, byte[]>)logConsoles.get(NAME_COMMUNICATION_NETWORK));
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
