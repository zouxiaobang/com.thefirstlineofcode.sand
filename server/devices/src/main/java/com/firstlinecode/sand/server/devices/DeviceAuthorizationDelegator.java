package com.firstlinecode.sand.server.devices;

import java.util.Calendar;
import java.util.Date;

import com.firstlinecode.granite.framework.core.annotations.AppComponent;
import com.firstlinecode.granite.framework.core.annotations.BeanDependency;
import com.firstlinecode.granite.framework.core.auth.IAccountManager;
import com.firstlinecode.granite.framework.core.config.IConfiguration;
import com.firstlinecode.granite.framework.core.config.IConfigurationAware;
import com.firstlinecode.granite.framework.core.config.IServerConfiguration;
import com.firstlinecode.granite.framework.core.config.IServerConfigurationAware;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.BadRequest;
import com.thefirstlineofcode.basalt.protocol.core.stream.error.Conflict;

@AppComponent("device.authorization.delegator")
public class DeviceAuthorizationDelegator implements IServerConfigurationAware, IConfigurationAware {
	private static final String CONFIGURATION_KEY_DEVICE_AUTHORIZATION_VALIDITY_TIME = "device.authorization.validity.time";	
	private static final int DEFAULT_DEVICE_AUTHORIZATION_VALIDITY_TIME = 60 * 5;
	
	@BeanDependency
	private IDeviceManager deviceManager;
	
	@BeanDependency
	private IAccountManager accountManager;
	
	private String domainName;
	private int deviceAuthorizationValidityTime;
	
	public void authorize(String deviceId, String authorizer) {
		if (!deviceManager.isValid(deviceId)) {
			throw new ProtocolException(new BadRequest(String.format(
					"Can't authorize the device. '%s' isn't a valid device ID.", deviceId)));
		}
		
		if (deviceManager.deviceIdExists(deviceId))
			throw new ProtocolException(new Conflict());
		
		if (authorizer != null && !domainName.contains(authorizer) && !accountManager.exists(authorizer)) {
			throw new ProtocolException(new BadRequest(String.format(
					"Can't authorize the device. '%s' isn't a valid authorizer.", authorizer)));
		}
		
		deviceManager.authorize(deviceId, authorizer, getExpiredTime(Calendar.getInstance().getTime().getTime(),
				deviceAuthorizationValidityTime));
	}
	
	private Date getExpiredTime(long currentTime, int validityTime) {
		Calendar expiredTime = Calendar.getInstance();
		expiredTime.setTimeInMillis(currentTime + validityTime * 1000);
		
		return expiredTime.getTime();
	}
	
	@Override
	public void setServerConfiguration(IServerConfiguration serverConfiguration) {
		domainName = serverConfiguration.getDomainName();
	}
	
	@Override
	public void setConfiguration(IConfiguration configuration) {
		deviceAuthorizationValidityTime = configuration.getInteger(CONFIGURATION_KEY_DEVICE_AUTHORIZATION_VALIDITY_TIME,
				DEFAULT_DEVICE_AUTHORIZATION_VALIDITY_TIME);
	}
}
