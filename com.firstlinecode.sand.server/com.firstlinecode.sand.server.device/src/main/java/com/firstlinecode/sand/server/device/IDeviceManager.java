package com.firstlinecode.sand.server.device;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.protocols.core.ModeDescriptor;

public interface IDeviceManager {
	void authorize(String deviceId, String authorizer, long validityTime);
	DeviceAuthorization getAuthorization(String deviceId);
	void cancelAuthorization(String deviceId);
	DeviceIdentity register(String deviceId);
	void create(Device device);
	void remove(JabberId jid);
	Device getByDeviceId(String deviceId);
	Device getByDeviceName(String deviceName);
	boolean deviceIdExists(String deviceId);
	boolean deviceNameExists(String deviceName);
	String getDeviceNameByDeviceId(String deviceId);
	void registerMode(String mode, ModeDescriptor modeDescriptor);
	ModeDescriptor unregisterMode(String mode);
	boolean isRegistered(String deviceId);
	boolean isConcentrator(String mode);
	boolean isActuator(String mode);
	boolean isSensor(String mode);
	boolean isActionSupported(String mode, Protocol protocol);
	boolean isEventSupported(String mode, Protocol protocol);
	boolean isActionSupported(String mode, Class<?> actionType);
	boolean isEventSupported(String mode, Class<?> eventType);
	Class<?> getActionType(String mode, Protocol protocol);
	Class<?> getEventType(String mode, Protocol protocol);
	boolean isValid(String deviceId);
	String getMode(String deviceId);
}
