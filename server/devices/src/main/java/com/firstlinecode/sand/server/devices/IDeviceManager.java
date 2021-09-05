package com.firstlinecode.sand.server.devices;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.protocols.core.ModelDescriptor;

public interface IDeviceManager {
	void authorize(String deviceId, String authorizer, int validityTime);
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
	void registerModel(String model, ModelDescriptor modelDescriptor);
	ModelDescriptor unregisterMode(String model);
	boolean isRegistered(String deviceId);
	boolean isConcentrator(String model);
	boolean isActuator(String model);
	boolean isSensor(String model);
	boolean isActionSupported(String model, Protocol protocol);
	boolean isEventSupported(String model, Protocol protocol);
	boolean isActionSupported(String model, Class<?> actionType);
	boolean isEventSupported(String model, Class<?> eventType);
	Class<?> getActionType(String model, Protocol protocol);
	Class<?> getEventType(String model, Protocol protocol);
	boolean isValid(String deviceId);
	String getModel(String deviceId);
}
