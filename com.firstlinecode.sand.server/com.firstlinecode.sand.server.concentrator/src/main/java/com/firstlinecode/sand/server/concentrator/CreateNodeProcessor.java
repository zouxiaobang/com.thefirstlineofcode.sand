package com.firstlinecode.sand.server.concentrator;

import java.util.Calendar;
import java.util.Date;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.Conflict;
import com.firstlinecode.basalt.protocol.core.stanza.error.ItemNotFound;
import com.firstlinecode.basalt.protocol.core.stanza.error.NotAcceptable;
import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.granite.framework.core.config.IConfiguration;
import com.firstlinecode.granite.framework.core.config.IConfigurationAware;
import com.firstlinecode.granite.framework.processing.IProcessingContext;
import com.firstlinecode.granite.framework.processing.IXepProcessor;
import com.firstlinecode.sand.protocols.concentrator.CreateNode;
import com.firstlinecode.sand.protocols.core.CommunicationNet;
import com.firstlinecode.sand.server.device.Device;
import com.firstlinecode.sand.server.device.IDeviceManager;

public class CreateNodeProcessor implements IXepProcessor<Iq, CreateNode>, IConfigurationAware {
	private static final String CONFIGURATION_KEY_NODE_CONFIRMATION_VALIDITY_TIME = "node.confirmation.validity.time";
	private static final int DEFAULT_VALIDITY_TIME = 1000 * 60 * 60 * 5;
	
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	@Dependency("concentrator.factory")
	private IConcentratorFactory concentratorFactory;
	
	private int validityTime;
	
	@Override
	public void process(IProcessingContext context, Iq stanza, CreateNode xep) {
		Device parent = deviceManager.getByDeviceName(context.getJid().getName());
		if (parent == null)
			throw new ProtocolException(new ItemNotFound(String.format("Device which's device name is %s not be found.",
					context.getJid().getName())));
		
		if (!deviceManager.isConcentrator(parent.getMode()))
			throw new ProtocolException(new NotAcceptable("Device which's device name is %s isn't a concentrator.",
					context.getJid().getName()));
		
		IConcentrator concentrator = concentratorFactory.getConcentrator(parent);
		if (concentrator == null)
			throw new RuntimeException("Can't fetch the concentrator.");
		
		Node node = new Node();
		node.setParent(parent.getDeviceId());
		node.setDeviceId(xep.getDeviceId());
		node.setLanId(xep.getLanId());
		node.setType(CommunicationNet.valueOf(xep.getCommunicationNet()));
		node.setAddress(xep.getAddress().toString());

		if (concentrator.containsNode(node.getDeviceId())) {
			throw new ProtocolException(new Conflict());
		}
		
		NodeConfirmationRequest request = new NodeConfirmationRequest();
		request.setNode(node);
		request.setExpiredTime(getExpiredTime());
		
		concentrator.requestConfirmation(request);
		context.write(new Iq(Iq.Type.RESULT, stanza.getId()));
	}

	private Date getExpiredTime() {
		Calendar expiredTime = Calendar.getInstance();
		expiredTime.setTimeInMillis(expiredTime.getTimeInMillis() + validityTime);
		
		return expiredTime.getTime();
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		validityTime = configuration.getInteger(CONFIGURATION_KEY_NODE_CONFIRMATION_VALIDITY_TIME, DEFAULT_VALIDITY_TIME);
	}

}
