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
import com.firstlinecode.sand.server.framework.things.Device;
import com.firstlinecode.sand.server.framework.things.DeviceAuthorization;
import com.firstlinecode.sand.server.framework.things.IDeviceIdRuler;
import com.firstlinecode.sand.server.framework.things.IDeviceManager;
import com.firstlinecode.sand.server.framework.things.ModeDescriptor;

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
		
		D_DeviceAuthorization authorization = getDeviceAuthorization(deviceId);
		if (authorization == null) {
			throw new ProtocolException(new NotAuthorized());
		}
		
		D_Device device = new D_Device();
		device.setId(UUID.randomUUID().toString());
		device.setDeviceId(deviceId);
		
		DeviceIdentity identity = new DeviceIdentity();
		identity.setDeviceName(createDeviceName(deviceId));
		identity.setCredentials(createCredentials());
		device.setIdentity(identity);
		
		device.setMode(guessMode(deviceId));
		
		device.setRegistrationTime(Calendar.getInstance().getTime());
		device.setAuthorizationId(authorization.getId());
		
		getDeviceMapper().insert(device);
		
		return device.getIdentity();
	}

	protected String createCredentials() {
		return generateRandomCredentials(8);
	}

	protected String createDeviceName(String deviceId) {
		return deviceId;
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
	public boolean isActionSupported(String mode, Class<?> action) {
		ModeDescriptor modeDescriptor = getModeDescriptor(mode);
		
		if (!modeDescriptor.isActuator())
			return false;
		
		String actionType = action.getClass().getName();
		for (String anActionType : modeDescriptor.getActionTypes()) {
			if (actionType.equals(anActionType))
				return true;
		}
		
		return false;
	}

	@Override
	public boolean isEventSupported(String mode, Class<?> event) {
		ModeDescriptor modeDescriptor = getModeDescriptor(mode);
		
		if (!modeDescriptor.isSensor())
			return false;
		
		String eventType = event.getClass().getName();
		for (String anEventType : modeDescriptor.getEventTypes()) {
			if (eventType.equals(anEventType))
				return true;
		}
		
		return false;
	}

	@Override
	public Device getByDeviceId(String deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device getByDeviceName(String deviceName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid(String deviceId) {
		if (deviceIdRuler != null)
			return deviceIdRuler.isValid(deviceId);
		
		return deviceId.length() == 12;
	}

	@Override
	public String guessMode(String deviceId) {
		if (deviceIdRuler != null)
			return deviceIdRuler.guessMode(deviceId);
		
		return deviceId.substring(0, 4);
	}

}
