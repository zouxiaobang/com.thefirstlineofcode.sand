package com.thefirstlineofcode.sand.server.concentrator;

import java.util.Calendar;
import java.util.Date;

import com.thefirstlinelinecode.sand.protocols.concentrator.CreateNode;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.Conflict;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.ItemNotFound;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.NotAcceptable;
import com.thefirstlineofcode.granite.framework.core.adf.data.IDataObjectFactory;
import com.thefirstlineofcode.granite.framework.core.adf.data.IDataObjectFactoryAware;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.annotations.Dependency;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IXepProcessor;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

public class CreateNodeProcessor implements IXepProcessor<Iq, CreateNode>, IDataObjectFactoryAware {
	@BeanDependency
	private IDeviceManager deviceManager;
	
	@BeanDependency
	private IConcentratorFactory concentratorFactory;
	
	@Dependency("node.confirmation.delegator")
	private NodeConfirmationDelegator nodeConfirmationDelegator;
	
	private IDataObjectFactory dataObjectFactory;
	
	@Override
	public void process(IProcessingContext context, Iq iq, CreateNode xep) {
		String deviceId = deviceManager.getDeviceIdByDeviceName(context.getJid().getNode());
		if (deviceId == null)
			throw new ProtocolException(new ItemNotFound(String.format("Device which's device name is '%s' not be found.",
					context.getJid().getNode())));
		
		if (!deviceManager.isConcentrator(deviceManager.getModel(deviceId)))
			throw new ProtocolException(new NotAcceptable("Device which's device name is '%s' isn't a concentrator.",
					context.getJid().getNode()));
		
		IConcentrator concentrator = concentratorFactory.getConcentrator(deviceId);
		if (concentrator == null)
			throw new RuntimeException("Can't get the concentrator.");
		
		if (concentrator.containsNode(xep.getDeviceId())) {
			throw new ProtocolException(new Conflict(String.format("Reduplicate node which's ID is '%s'.", xep.getDeviceId())));
		}
		
		if (concentrator.containsLanId(xep.getLanId())) {
			throw new ProtocolException(new Conflict(String.format("Reduplicate LAN ID: '%s'.", xep.getLanId())));
			
		}
		
		Node node = new Node();
		node.setDeviceId(xep.getDeviceId());
		node.setLanId(xep.getLanId());
		node.setCommunicationNet(xep.getCommunicationNet());
		node.setAddress(xep.getAddress());
		
		NodeConfirmation confirmation = dataObjectFactory.create(NodeConfirmation.class);
		confirmation.setRequestId(iq.getId());
		confirmation.setConcentratorDeviceName(deviceManager.getDeviceNameByDeviceId(deviceId));
		confirmation.setNode(node);
		Date currentTime = Calendar.getInstance().getTime();
		confirmation.setRequestedTime(currentTime);
		
		nodeConfirmationDelegator.requestToConfirm(confirmation);
	}
	
	@Override
	public void setDataObjectFactory(IDataObjectFactory dataObjectFactory) {
		this.dataObjectFactory = dataObjectFactory;
	}

}
