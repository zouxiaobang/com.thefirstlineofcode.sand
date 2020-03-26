package com.firstlinecode.sand.server.framework.things;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public interface IDeviceManager extends IDeviceIdRuler {
	void authorize(String deviceId, String authorizer, long validityTime);
	void cancelAuthorization(String deviceId);
	DeviceIdentity register(String deviceId);
	void remove(JabberId jid);
	Device getByDeviceId(String deviceId);
	Device getByDeviceName(String deviceName);
	boolean deviceIdExists(String deviceId);
	boolean deviceNameExists(String deviceName);
	void registerMode(String mode, ModeDescriptor modeDescriptor);
	ModeDescriptor unregisterMode(String mode);
	boolean isRegistered(String deviceId);
	boolean isConcentrator(String mode);
	boolean isActuator(String mode);
	boolean isSensor(String mode);
	boolean isActionSupported(String mode, Class<?> action);
	boolean isEventSupported(String mode, Class<?> event);
}
