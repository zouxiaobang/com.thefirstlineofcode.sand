package com.firstlinecode.sand.server.lite.auth;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.error.Conflict;
import com.firstlinecode.basalt.protocol.core.stanza.error.NotAuthorized;
import com.firstlinecode.granite.framework.core.config.IApplicationConfiguration;
import com.firstlinecode.granite.framework.core.config.IApplicationConfigurationAware;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.framework.auth.DeviceAuthorization;
import com.firstlinecode.sand.server.framework.auth.IDeviceManager;

@Transactional
@Component
public class DeviceManager implements IDeviceManager, IApplicationConfigurationAware {
	@Autowired
	private SqlSession sqlSession;
	
	private String domainName;
	
	@Override
	public void authorize(String deviceId, String authorizer, long validityTime) {
		Date authorizedTime = Calendar.getInstance().getTime();
		Date expiredTime = getExpiredTime(authorizedTime.getTime(), validityTime);
		
		D_DeviceAuthorization authrozation = new D_DeviceAuthorization();
		authrozation.setId(UUID.randomUUID().toString());
		authrozation.setDeviceId(deviceId);
		authrozation.setAuthorizer(authorizer);
		authrozation.setAuthorizedTime(authorizedTime);
		authrozation.setExpiredTime(expiredTime);
		
		getDeviceAuthorizationMapper().insert(authrozation);
	}
	
	@Override
	public void cancelAuthorization(String deviceId) {
		getDeviceAuthorizationMapper().updateCanceled(deviceId, true);
	}

	private Date getExpiredTime(long currentTime, long validityTime) {
		Calendar expiredTime = Calendar.getInstance();
		expiredTime.setTimeInMillis(currentTime + validityTime);
		
		return expiredTime.getTime();
	}

	@Override
	public DeviceIdentity register(String deviceId) {
		if (isRegistered(deviceId)) {
			throw new ProtocolException(new Conflict());
		}
		
		D_DeviceAuthorization authorization = getDeviceAuthorization(deviceId);
		if (authorization == null) {
			throw new ProtocolException(new NotAuthorized());
		}
		
		D_DeviceIdentity deviceIdentity = new D_DeviceIdentity();
		deviceIdentity.setId(UUID.randomUUID().toString());
		deviceIdentity.setDeviceId(deviceId);
		deviceIdentity.setJid(createJid(deviceId));
		deviceIdentity.setCredentials(createCredentials());
		deviceIdentity.setRegisteredTime(Calendar.getInstance().getTime());
		deviceIdentity.setAuthorizationId(authorization.getId());
		
		getDeviceIdentityMapper().insert(deviceIdentity);
		
		return new DeviceIdentity(deviceIdentity.getJid(), deviceIdentity.getCredentials());
	}

	protected String createCredentials() {
		return generateRandomCredentials(8);
	}

	protected JabberId createJid(String deviceId) {
		return JabberId.parse(String.format("%s@%s", deviceId, domainName));
	}

	private D_DeviceAuthorization getDeviceAuthorization(String deviceId) {
		DeviceAuthorization[] authroizations = getDeviceAuthorizationMapper().selectByDeviceId(deviceId);
		if (authroizations == null || authroizations.length == 0)
			return null;
		
		Date currentTime = Calendar.getInstance().getTime();
		for (DeviceAuthorization authorization : authroizations) {
			if (authorization.getExpiredTime().after(currentTime) &&
					!authorization.isCanceled()) {
				return (D_DeviceAuthorization)authorization;
			}
		}
		
		return null;
	}

	protected boolean isRegistered(String deviceId) {
		return getDeviceIdentityMapper().selectByDeviceId(deviceId) != null;
	}

	@Override
	public void remove(JabberId jid) {
		getDeviceIdentityMapper().delete(jid);
	}

	@Override
	public boolean exists(String deviceId) {
		return getDeviceIdentityMapper().selectCountByDeviceId(deviceId) != 0;
	}
	
	@Override
	public boolean exists(JabberId jid) {
		return getDeviceIdentityMapper().selectCountByJid(jid) != 0;
	}
	
	@Override
	public DeviceIdentity get(String deviceId) {
		return getDeviceIdentityMapper().selectByDeviceId(deviceId);
	}
	
	@Override
	public DeviceIdentity get(JabberId jid) {
		return getDeviceIdentityMapper().selectByJid(jid);
	}
	
	private DeviceAuthorizationMapper getDeviceAuthorizationMapper() {
		return (DeviceAuthorizationMapper)sqlSession.getMapper(DeviceAuthorizationMapper.class);
	}
	
	private DeviceIdentityMapper getDeviceIdentityMapper() {
		return (DeviceIdentityMapper)sqlSession.getMapper(DeviceIdentityMapper.class);
	}

	@Override
	public void setApplicationConfiguration(IApplicationConfiguration appConfiguration) {
		domainName = appConfiguration.getDomainName();
	}
	
	private String generateRandomCredentials(int length) {
		if (length <= 16) {
			return String.format("%016X", java.util.UUID.randomUUID().getLeastSignificantBits()).substring(16 - length, 16);
		}
		
		if (length > 32) {
			length = 32;
		}
		
		UUID uuid = UUID.randomUUID();
		String uuidHexString = String.format("%016X%016X", uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
				
		return uuidHexString.substring(32 - length, 32); 
	}

}
