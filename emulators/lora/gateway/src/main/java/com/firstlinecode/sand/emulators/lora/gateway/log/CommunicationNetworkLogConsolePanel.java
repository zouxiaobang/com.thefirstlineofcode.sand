package com.firstlinecode.sand.emulators.lora.gateway.log;

import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstlinecode.sand.client.things.commuication.ICommunicationNetwork;
import com.firstlinecode.sand.client.things.obm.ObmData;
import com.firstlinecode.sand.emulators.lora.network.ILoraNetworkListener;
import com.firstlinecode.sand.emulators.things.ui.AbstractLogConsolePanel;
import com.firstlinecode.sand.protocols.core.ModelDescriptor;
import com.firstlinecode.sand.protocols.lora.LoraAddress;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

public class CommunicationNetworkLogConsolePanel extends AbstractLogConsolePanel implements ILoraNetworkListener {
	private static final long serialVersionUID = 4598974878913796627L;
	
	private static final Logger logger = LoggerFactory.getLogger(CommunicationNetworkLogConsolePanel.class);
	
	private ICommunicationNetwork<LoraAddress, byte[], ?> network;
	
	public CommunicationNetworkLogConsolePanel(ICommunicationNetwork<LoraAddress, byte[], ?> network, Map<String, ModelDescriptor> models) {
		this.network = network;
		addProtocolToTypes(models);
	}

	public void addProtocolToTypes(Map<String, ModelDescriptor> models) {
		Collection<ModelDescriptor> modelDescriptors = models.values();
		for (ModelDescriptor modelDescriptor : modelDescriptors) {
			Map<Protocol, Class<?>> supportedActions = modelDescriptor.getSupportedActions();
			for (Map.Entry<Protocol, Class<?>> entry : supportedActions.entrySet()) {
				Protocol protocol = entry.getKey();
				if (!protocolToTypes.containsKey(protocol)) {
					protocolToTypes.put(protocol, entry.getValue());
				}
			}

			Map<Protocol, Class<?>> supportedEvents = modelDescriptor.getSupportedEvents();
			for (Map.Entry<Protocol, Class<?>> entry : supportedEvents.entrySet()) {
				Protocol protocol = entry.getKey();
				if (!protocolToTypes.containsKey(protocol)) {
					protocolToTypes.put(protocol, entry.getValue());
				}
			}
		}
	}
	
	@Override
	protected void doWindowClosing(WindowEvent e) {
		network.removeListener(this);
	}

	@Override
	public void sent(LoraAddress from, LoraAddress to, byte[] data) {
		String logMessage = createSentLogMessage(from, to, new ObmData(parseProtocol(data), data));
		
		if (logger.isDebugEnabled()) {
			logger.debug(logMessage);
		}
		
		log(logMessage);
	}

	private String createSentLogMessage(LoraAddress from, LoraAddress to, ObmData obmData) {
		return String.format("D(%s)-->N-->D(%s):" + LINE_SEPARATOR +
						"    O: %s" + LINE_SEPARATOR +
						"    B(%d bytes): %s",
				from, to, obmData.getProtocolObjectInfoString(), obmData.getBinary().length,
					obmData.getHexString());
	}

	@Override
	public void received(LoraAddress from, LoraAddress to, byte[] data) {
		String logMessage = createReceivedLogMessage(from, to, new ObmData(parseProtocol(data), data));
		
		if (logger.isDebugEnabled()) {
			logger.debug(logMessage);
		}
		
		log(logMessage);
		
	}

	private String createReceivedLogMessage(LoraAddress from, LoraAddress to, ObmData obmData) {
		return String.format("D(%s)<--N<--D(%s)" + LINE_SEPARATOR +
						"    O: %s" + LINE_SEPARATOR +
						"    B(%d bytes): %s",
				to, from, obmData.getProtocolObjectInfoString(), obmData.getBinary().length,
				obmData.getHexString());
	}

	@Override
	public void addressChanged(LoraAddress newAddress, LoraAddress oldAddress) {
		String logMessage = createAddressChangedLogMessage(oldAddress, newAddress);
		
		if (logger.isInfoEnabled()) {
			logger.info(logMessage);
		}
		
		log(logMessage);
	}

	private String createAddressChangedLogMessage(LoraAddress oldAddress, LoraAddress newAddress) {
		return String.format("D(%s)<=N, D(%s)=>N", oldAddress, newAddress);
	}

	@Override
	public void collided(LoraAddress from, LoraAddress to, byte[] data) {
		String logMessage = createCollidedLogMessage(from, to, new ObmData(parseProtocol(data), data));
		
		if (logger.isWarnEnabled()) {
			logger.warn(logMessage);
		}
		
		log(logMessage);
	}

	private String createCollidedLogMessage(LoraAddress from, LoraAddress to, ObmData obmData) {
		return String.format("?* D(%s)-->N-->D(%s)" + LINE_SEPARATOR +
						"    O: %s" + LINE_SEPARATOR +
						"    B(%d bytes): %s",
						from, to, obmData.getProtocolObjectInfoString(), obmData.getBinary().length,
						obmData.getHexString());
	}

	@Override
	public void lost(LoraAddress from, LoraAddress to, byte[] data) {
		String logMessage = createLostLogMessage(from, to, new ObmData(parseProtocol(data), data));
		
		if (logger.isWarnEnabled()) {
			logger.warn(logMessage);
		}
		
		log(logMessage);
	}

	private String createLostLogMessage(LoraAddress from, LoraAddress to, ObmData obmData) {
		return String.format("?& D(%s)->N-->D(%s)" + LINE_SEPARATOR +
				"    O: %s" + LINE_SEPARATOR +
				"    B(%d bytes): %s",
				from, to, obmData.getProtocolObjectInfoString(), obmData.getBinary().length,
				obmData.getHexString());
	}

}
