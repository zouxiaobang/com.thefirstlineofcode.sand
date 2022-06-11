package com.thefirstlineofcode.sand.server.concentrator;

import java.util.Calendar;
import java.util.Date;

import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.BadRequest;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.ItemNotFound;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.NotAcceptable;
import com.thefirstlineofcode.granite.framework.core.annotations.AppComponent;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.auth.IAccountManager;
import com.thefirstlineofcode.granite.framework.core.config.IConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IConfigurationAware;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfigurationAware;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

@AppComponent("node.confirmation.delegator")
public class NodeConfirmationDelegator implements IServerConfigurationAware, IConfigurationAware {
	private static final String CONFIGURATION_KEY_NODE_CONFIRMATION_VALIDITY_TIME = "node.confirmation.validity.time";	
	private static final int DEFAULT_NODE_CONFIRMATION_VALIDITY_TIME = 60 * 5;
	
	@BeanDependency
	private IDeviceManager deviceManager;
	
	@BeanDependency
	private IAccountManager accountManager;
	
	@BeanDependency
	private IConcentratorFactory concentratorFactory;
	
	private String domainName;
		
	private int nodeConfirmationValidityTime;
	
	public void requestToConfirm(NodeConfirmation confirmation) {
		String concentratorDeviceName = confirmation.getConcentratorDeviceName();
		
		String deviceId = deviceManager.getDeviceIdByDeviceName(concentratorDeviceName);
		if (deviceId == null)
			throw new ProtocolException(new ItemNotFound(String.format("Device which's device name is '%s' not be found.",
					concentratorDeviceName)));
		
		if (!deviceManager.isConcentrator(deviceManager.getModel(deviceId)))
			throw new ProtocolException(new NotAcceptable("Device which's device name is '%s' isn't a concentrator.",
					concentratorDeviceName));
		
		IConcentrator concentrator = concentratorFactory.getConcentrator(deviceId);
		if (concentrator == null)
			throw new RuntimeException("Can't get the concentrator.");
		
		confirmation.setExpiredTime(getExpiredTime(confirmation.getRequestedTime().getTime(),
				nodeConfirmationValidityTime));
		concentrator.requestToConfirm(confirmation);
	}
	
	private Date getExpiredTime(long currentTime, int validityTime) {
		Calendar expiredTime = Calendar.getInstance();
		expiredTime.setTimeInMillis(currentTime + (validityTime * 1000));
		
		return expiredTime.getTime();
	}
	
	public NodeConfirmed confirm(String concentratorDeviceName, String nodeId, String confirmer) {
		String deviceId = deviceManager.getDeviceIdByDeviceName(concentratorDeviceName);
		if (deviceId == null)
			throw new ProtocolException(new ItemNotFound(String.format("Device which's device name is '%s' not be found.",
					concentratorDeviceName)));
		
		if (!deviceManager.isConcentrator(deviceManager.getModel(deviceId)))
			throw new ProtocolException(new NotAcceptable("Device which's device name is '%s' isn't a concentrator.",
					concentratorDeviceName));
		
		IConcentrator concentrator = concentratorFactory.getConcentrator(deviceId);
		if (concentrator == null)
			throw new RuntimeException("Can't get the concentrator.");
		
		if (confirmer != null && !domainName.contains(confirmer) && !accountManager.exists(confirmer)) {
			throw new ProtocolException(new BadRequest(String.format(
					"Can't confirm the concentration. '%s' isn't a valid confirmer.", confirmer)));
		}
		
		return concentrator.confirm(nodeId, confirmer);
	}
	
	@Override
	public void setServerConfiguration(IServerConfiguration serverConfiguration) {
		domainName = serverConfiguration.getDomainName();
	}
	
	@Override
	public void setConfiguration(IConfiguration configuration) {
		nodeConfirmationValidityTime = configuration.getInteger(CONFIGURATION_KEY_NODE_CONFIRMATION_VALIDITY_TIME,
				DEFAULT_NODE_CONFIRMATION_VALIDITY_TIME);
	}
}
