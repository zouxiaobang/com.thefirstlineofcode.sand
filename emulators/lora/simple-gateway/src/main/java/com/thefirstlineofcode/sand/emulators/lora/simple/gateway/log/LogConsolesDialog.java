package com.thefirstlineofcode.sand.emulators.lora.simple.gateway.log;

import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import com.thefirstlineofcode.sand.client.core.commuication.ICommunicationNetwork;
import com.thefirstlineofcode.sand.client.core.commuication.ICommunicationNetworkListener;
import com.thefirstlineofcode.sand.client.core.obx.IObxFactory;
import com.thefirstlineofcode.sand.client.lora.IDualLoraChipsCommunicator;
import com.thefirstlineofcode.sand.emulators.commons.ui.AbstractLogConsolesDialog;
import com.thefirstlineofcode.sand.emulators.lora.things.AbstractLoraThingEmulator;
import com.thefirstlineofcode.sand.protocols.lora.LoraAddress;

public class LogConsolesDialog extends AbstractLogConsolesDialog {
	private static final long serialVersionUID = 5197344780011371803L;
	
	public static final String NAME_COMMUNICATION_NETWORK = "Communication Network";
	public static final String NAME_GATEWAY = "Gateway";
	
	private ICommunicationNetwork<LoraAddress, byte[], ?> network;
	private IDualLoraChipsCommunicator gatewayCommunicator;
	private Map<String, List<AbstractLoraThingEmulator>> allThings;
	
	public LogConsolesDialog(JFrame parent,  ICommunicationNetwork<LoraAddress, byte[], ?> network,
			IDualLoraChipsCommunicator gatewayCommunicator, Map<String, List<AbstractLoraThingEmulator>> allThings,
			IObxFactory obxFactory) {
		super(parent, obxFactory);
		
		this.network = network;
		this.gatewayCommunicator = gatewayCommunicator;
		this.allThings = allThings;
		
		createPreinstlledLogConsoles();
	}

	protected void createPreinstlledLogConsoles() {
		createInternetLogConsole();
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
		createLogConsole(thing.getDeviceId(), new ThingLogConsolePanel(thing, obxFactory));
	}

	private void createGatewayConsole(IDualLoraChipsCommunicator gatewayCommunicator) {
		createLogConsole(NAME_GATEWAY, new GatewayLogConsolePanel(gatewayCommunicator, obxFactory));
	}

	@SuppressWarnings("unchecked")
	private void createCommunicationNetworkLogConsole(ICommunicationNetwork<LoraAddress, byte[], ?> network) {
		createLogConsole(NAME_COMMUNICATION_NETWORK, new CommunicationNetworkLogConsolePanel(network, obxFactory));
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
