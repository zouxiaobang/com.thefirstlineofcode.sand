package com.firstlinecode.sand.server.lite.device;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.error.Conflict;
import com.firstlinecode.basalt.protocol.core.stanza.error.NotAuthorized;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.protocols.core.ModeDescriptor;
import com.firstlinecode.sand.server.device.Device;
import com.firstlinecode.sand.server.device.DeviceAuthorization;
import com.firstlinecode.sand.server.device.IDeviceIdRuler;
import com.firstlinecode.sand.server.device.IDeviceManager;

@Transactional
@Component
public class DeviceManager implements IDeviceManager {
	@Autowired
	private SqlSession sqlSession;
	
	@Autowired(required = false)
	private IDeviceIdRuler deviceIdRuler;
	
	private Map<String, ModeDescriptor> modeDescriptors;
	
	public DeviceManager() {
		modeDescriptors = new HashMap<>();
	}
	
	@Override
	public void authorize(String deviceId, String authorizer, long validityTime) {
		if (!isValid(deviceId))
			throw new RuntimeException(String.format("Invalid device ID '%s'.", deviceId));
		
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
		if (!isValid(deviceId))
			throw new RuntimeException(String.format("Invalid device ID '%s'.", deviceId));
		
		if (isRegistered(deviceId)) {
			throw new ProtocolException(new Conflict());
		}
		
		DeviceAuthorization authorization = getDeviceAuthorization(deviceId);
		if (authorization == null) {
			throw new ProtocolException(new NotAuthorized());
		}
		
		D_Device device = new D_Device();
		device.setId(UUID.randomUUID().toString());
		device.setDeviceId(deviceId);		
		device.setMode(getMode(deviceId));
		device.setRegistrationTime(Calendar.getInstance().getTime());
		create(device);
		
		D_DeviceIdentity identity = new D_DeviceIdentity();
		identity.setId(UUID.randomUUID().toString());
		identity.setDeviceId(deviceId);
		identity.setDeviceName(createDeviceName(deviceId));
		identity.setCredentials(createCredentials());
		getDeviceIdentityMapper().insert(identity);
		
		return new DeviceIdentity(identity.getDeviceName(), identity.getCredentials());
	}
	
	@Override
	public void create(Device device) {
		getDeviceMapper().insert(device);
	}

	protected String createCredentials() {
		return generateRandomCredentials(8);
	}

	protected String createDeviceName(String deviceId) {
		return deviceId;
	}

	private DeviceAuthorization getDeviceAuthorization(String deviceId) {
		DeviceAuthorization[] authroizations = getDeviceAuthorizationMapper().selectByDeviceId(deviceId);
		if (authroizations == null || authroizations.length == 0)
			return null;
		
		Date currentTime = Calendar.getInstance().getTime();
		for (DeviceAuthorization authorization : authroizations) {
			if (authorization.getExpiredTime().after(currentTime) &&
					!authorization.isCanceled()) {
				return authorization;
			}
		}
		
		return null;
	}

	@Override
	public boolean isRegistered(String deviceId) {
		return getDeviceMapper().selectByDeviceId(deviceId) != null;
	}

	@Override
	public void remove(JabberId jid) {
		getDeviceMapper().delete(jid);
	}

	@Override
	public boolean deviceIdExists(String deviceId) {
		return getDeviceMapper().selectCountByDeviceId(deviceId) != 0;
	}
	
	@Override
	public boolean deviceNameExists(String deviceName) {
		return getDeviceMapper().selectCountByDeviceName(deviceName) != 0;
	}
	
	private DeviceAuthorizationMapper getDeviceAuthorizationMapper() {
		return (DeviceAuthorizationMapper)sqlSession.getMapper(DeviceAuthorizationMapper.class);
	}
	
	private DeviceMapper getDeviceMapper() {
		return (DeviceMapper)sqlSession.getMapper(DeviceMapper.class);
	}
	
	private DeviceIdentityMapper getDeviceIdentityMapper() {
		return (DeviceIdentityMapper)sqlSession.getMapper(DeviceIdentityMapper.class);
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

	@Override
	public void registerMode(String mode, ModeDescriptor modeDescriptor) {
		modeDescriptors.put(mode, modeDescriptor);
	}

	@Override
	public ModeDescriptor unregisterMode(String mode) {
		return modeDescriptors.remove(mode);
	}

	@Override
	public boolean isConcentrator(String mode) {
		return getModeDescriptor(mode).isConcentrator();
	}

	private ModeDescriptor getModeDescriptor(String mode) {
		ModeDescriptor modeDescriptor = modeDescriptors.get(mode);
		if (modeDescriptor == null)
			throw new IllegalArgumentException(String.format("Unsupported mode: %s.", mode));
		
		return modeDescriptor;
	}

	@Override
	public boolean isActuator(String mode) {
		return getModeDescriptor(mode).isActuator();
	}

	@Override
	public boolean isSensor(String mode) {
		return getModeDescriptor(mode).isSensor();
	}

	@Override
	public Device getByDeviceId(String deviceId) {
		return getDeviceMapper().selectByDeviceId(deviceId);
	}

	@Override
	public Device getByDeviceName(String deviceName) {
		D_DeviceIdentity identity = (D_DeviceIdentity)getDeviceIdentityMapper().selectByDeviceName(deviceName);
		if (identity == null)
			return null;
		
		return getDeviceMapper().selectByDeviceId(identity.getDeviceId());
	}

	@Override
	public boolean isValid(String deviceId) {
		if (deviceIdRuler != null)
			return deviceIdRuler.isValid(deviceId);
		
		return deviceId.length() == 12;
	}

	@Override
	public String getMode(String deviceId) {
		if (deviceIdRuler != null)
			return deviceIdRuler.guessMode(deviceId);
		
		return deviceId.substring(0, 4);
	}

	@Override
	public boolean isActionSupported(String mode, String actionName) {
		ModeDescriptor modeDescriptor = getModeDescriptor(mode);
		
		if (!modeDescriptor.isActuator())
			return false;
		
		for (String supportedActionName : modeDescriptor.getSupportedActions().keySet()) {
			if (actionName.equals(supportedActionName))
				return true;
		}
		
		return false;
	}

	@Override
	public boolean isEventSupported(String mode, String eventName) {
		ModeDescriptor modeDescriptor = getModeDescriptor(mode);
		
		if (!modeDescriptor.isSensor())
			return false;
		
		for (String supportedEventName : modeDescriptor.getSupportedEvents().keySet()) {
			if (eventName.equals(supportedEventName))
				return true;
		}
		
		return false;
	}

	@Override
	public Class<?> getActionType(String mode, String actionName) {
		ModeDescriptor modeDescriptor = getModeDescriptor(mode);
		
		if (!modeDescriptor.isActuator())
			throw new RuntimeException(String.format("Device which's mode is '%s' isn't an actuator.", mode));
		
		return modeDescriptor.getSupportedActions().get(actionName);
	}

	@Override
	public Class<?> getEventType(String mode, String eventName) {
		ModeDescriptor modeDescriptor = getModeDescriptor(mode);
		
		if (!modeDescriptor.isSensor())
			throw new RuntimeException(String.format("Device which's mode is '%s' isn't a sensor.", mode));
		
		return modeDescriptor.getSupportedEvents().get(eventName);
	}
}
