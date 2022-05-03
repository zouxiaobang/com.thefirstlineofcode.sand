package com.thefirstlineofcode.sand.server.lite.devices;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.Conflict;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.NotAuthorized;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;
import com.thefirstlineofcode.sand.server.devices.Device;
import com.thefirstlineofcode.sand.server.devices.DeviceAuthorization;
import com.thefirstlineofcode.sand.server.devices.IDeviceIdRuler;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

@Transactional
@Component
public class DeviceManager implements IDeviceManager {
	@Autowired
	private SqlSession sqlSession;
	
	@Autowired(required = false)
	private IDeviceIdRuler deviceIdRuler;
	
	private Map<String, ModelDescriptor> modelDescriptors;
	
	public DeviceManager() {
		modelDescriptors = new HashMap<>();
	}
	
	@Override
	public void authorize(String deviceId, String authorizer, Date expiredTime) {
		if (!isValid(deviceId))
			throw new RuntimeException(String.format("Invalid device ID '%s'.", deviceId));
		
		Date authorizedTime = Calendar.getInstance().getTime();
		
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
	
	@Override
	public DeviceIdentity register(String deviceId) {
		if (!isValid(deviceId))
			throw new RuntimeException(String.format("Invalid device ID '%s'.", deviceId));
		
		if (isRegistered(deviceId)) {
			throw new ProtocolException(new Conflict());
		}
		
		DeviceAuthorization authorization = getAuthorization(deviceId);
		if (authorization == null || authorization.isCanceled() || isExpired(authorization)) {
			throw new ProtocolException(new NotAuthorized());
		}
		
		D_Device device = new D_Device();
		device.setId(UUID.randomUUID().toString());
		device.setDeviceId(deviceId);		
		device.setModel(getModel(deviceId));
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
	
	private boolean isExpired(DeviceAuthorization authorization) {
		Date current = Calendar.getInstance().getTime();
		
		return current.before(authorization.getExpiredTime());
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
	
	@Override
	public DeviceAuthorization getAuthorization(String deviceId) {
		DeviceAuthorization[] authroizations = getDeviceAuthorizationMapper().selectByDeviceId(deviceId);
		if (authroizations == null || authroizations.length == 0)
			return null;
		
		DeviceAuthorization authorization = authroizations[0];
		if (isAuthorizationExpired(authorization) || authorization.isCanceled()) {
			return null;
		}
		
		return authorization;
	}

	private boolean isAuthorizationExpired(DeviceAuthorization authorization) {
		return Calendar.getInstance().getTime().after(authorization.getExpiredTime());
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
	public void registerModel(String model, ModelDescriptor modelDescriptor) {
		modelDescriptors.put(model, modelDescriptor);
	}

	@Override
	public ModelDescriptor unregisterMode(String model) {
		return modelDescriptors.remove(model);
	}

	@Override
	public boolean isConcentrator(String model) {
		return getModelDescriptor(model).isConcentrator();
	}

	private ModelDescriptor getModelDescriptor(String model) {
		ModelDescriptor modelDescriptor = modelDescriptors.get(model);
		if (modelDescriptor == null)
			throw new IllegalArgumentException(String.format("Unsupported model: %s.", model));
		
		return modelDescriptor;
	}

	@Override
	public boolean isActuator(String model) {
		return getModelDescriptor(model).isActuator();
	}

	@Override
	public boolean isSensor(String model) {
		return getModelDescriptor(model).isSensor();
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
	public String getModel(String deviceId) {
		String model = null;
		if (deviceIdRuler != null)
			model = deviceIdRuler.guessModel(deviceId);
		
		model = deviceId.substring(0, 4);
		if (getModelDescriptor(model) != null)
			return model;
		
		return null;
	}

	@Override
	public boolean isActionSupported(String model, Protocol protocol) {
		ModelDescriptor modelDescriptor = getModelDescriptor(model);
		
		if (!modelDescriptor.isActuator())
			return false;
		
		for (Protocol supportedActionProtocol : modelDescriptor.getSupportedActions().keySet()) {
			if (protocol.equals(supportedActionProtocol))
				return true;
		}
		
		return false;
	}

	@Override
	public boolean isEventSupported(String model, Protocol protocol) {
		ModelDescriptor modelDescriptor = getModelDescriptor(model);
		
		if (!modelDescriptor.isSensor())
			return false;
		
		for (Protocol supportedEventProtocol : modelDescriptor.getSupportedEvents().keySet()) {
			if (protocol.equals(supportedEventProtocol))
				return true;
		}
		
		return false;
	}

	@Override
	public Class<?> getActionType(String model, Protocol protocol) {
		ModelDescriptor modelDescriptor = getModelDescriptor(model);
		
		if (!modelDescriptor.isActuator())
			throw new RuntimeException(String.format("Device which's model is '%s' isn't an actuator.", model));
		
		return modelDescriptor.getSupportedActions().get(protocol);
	}

	@Override
	public Class<?> getEventType(String model, Protocol protocol) {
		ModelDescriptor modelDescriptor = getModelDescriptor(model);
		
		if (!modelDescriptor.isSensor())
			throw new RuntimeException(String.format("Device which's model is '%s' isn't a sensor.", model));
		
		return modelDescriptor.getSupportedEvents().get(protocol);
	}

	@Override
	public String getDeviceNameByDeviceId(String deviceId) {
		DeviceIdentity deviceIdentity = getDeviceIdentityMapper().selectByDeviceId(deviceId);
		if (deviceIdentity != null)
			return deviceIdentity.getDeviceName();
		
		return null;
	}

	@Override
	public boolean isActionSupported(String mode, Class<?> actionType) {
		ModelDescriptor modelDescriptor = getModelDescriptor(mode);
		
		if (!modelDescriptor.isActuator())
			throw new RuntimeException(String.format("Device which's model is '%s' isn't an actuator.", mode));
		
		return modelDescriptor.getSupportedActions().containsValue(actionType);
	}

	@Override
	public boolean isEventSupported(String model, Class<?> eventType) {
		ModelDescriptor modelDescriptor = getModelDescriptor(model);
		
		if (!modelDescriptor.isSensor())
			throw new RuntimeException(String.format("Device which's model is '%s' isn't a sensor.", model));
		
		return modelDescriptor.getSupportedEvents().containsValue(eventType);
	}
}
