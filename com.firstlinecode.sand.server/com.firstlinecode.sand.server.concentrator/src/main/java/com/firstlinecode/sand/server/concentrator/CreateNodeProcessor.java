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
import com.firstlinecode.granite.framework.core.supports.data.IDataObjectFactory;
import com.firstlinecode.granite.framework.core.supports.data.IDataObjectFactoryAware;
import com.firstlinecode.granite.framework.processing.IProcessingContext;
import com.firstlinecode.granite.framework.processing.IXepProcessor;
import com.firstlinecode.sand.protocols.concentrator.CreateNode;
import com.firstlinecode.sand.server.device.Device;
import com.firstlinecode.sand.server.device.IDeviceManager;

public class CreateNodeProcessor implements IXepProcessor<Iq, CreateNode>, IConfigurationAware, IDataObjectFactoryAware {
	private static final String CONFIGURATION_KEY_NODE_CONFIRMATION_VALIDITY_TIME = "node.confirmation.validity.time";
	private static final int DEFAULT_VALIDITY_TIME = 1000 * 60 * 60 * 5;
	
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	@Dependency("concentrator.factory")
	private IConcentratorFactory concentratorFactory;
	
	private IDataObjectFactory dataObjectFactory;
	
	private int validityTime;
	
	@Override
	public void process(IProcessingContext context, Iq stanza, CreateNode xep) {
		Device device = deviceManager.getByDeviceName(context.getJid().getName());
		if (device == null)
			throw new ProtocolException(new ItemNotFound(String.format("Device which's device name is '%s' not be found.",
					context.getJid().getName())));
		
		if (!deviceManager.isConcentrator(device.getMode()))
			throw new ProtocolException(new NotAcceptable("Device which's device name is '%s' isn't a concentrator.",
					context.getJid().getName()));
		
		IConcentrator concentrator = concentratorFactory.getConcentrator(device);
		if (concentrator == null)
			throw new RuntimeException("Can't get the concentrator.");
		
		if (concentrator.containsNode(xep.getDeviceId())) {
			throw new ProtocolException(new Conflict(String.format("Duplicated node which's ID is '%s'.", xep.getDeviceId())));
		}
		
		if (concentrator.containsLanId(xep.getLanId())) {
			throw new ProtocolException(new Conflict(String.format("Duplicated lan id: '%s'.", xep.getLanId())));
			
		}
		
		Node node = new Node();
		node.setDeviceId(xep.getDeviceId());
		node.setLanId(xep.getLanId());
		node.setCommunicationNet(xep.getCommunicationNet());
		node.setAddress(xep.getAddress().toString());
		
		NodeConfirmation confirmation = dataObjectFactory.create(NodeConfirmation.class);
		confirmation.setConcentrator(device.getDeviceId());
		confirmation.setNode(node);
		Date currentTime = Calendar.getInstance().getTime();
		confirmation.setRequestedTime(currentTime);
		confirmation.setExpiredTime(getExpiredTime(currentTime.getTime(), validityTime));
		
		concentrator.requestConfirmation(confirmation);
		context.write(new Iq(Iq.Type.RESULT, stanza.getId()));
	}

	private Date getExpiredTime(long currentTime, long validityTime) {
		Calendar expiredTime = Calendar.getInstance();
		expiredTime.setTimeInMillis(currentTime + validityTime);
		
		return expiredTime.getTime();
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		validityTime = configuration.getInteger(CONFIGURATION_KEY_NODE_CONFIRMATION_VALIDITY_TIME, DEFAULT_VALIDITY_TIME);
	}

	@Override
	public void setDataObjectFactory(IDataObjectFactory dataObjectFactory) {
		this.dataObjectFactory = dataObjectFactory;
	}

}
