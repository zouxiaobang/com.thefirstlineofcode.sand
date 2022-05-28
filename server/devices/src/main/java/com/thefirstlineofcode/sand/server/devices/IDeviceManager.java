package com.thefirstlineofcode.sand.server.devices;

import java.util.Date;

import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.sand.protocols.core.ModelDescriptor;

public interface IDeviceManager {
	void authorize(String deviceId, String authorizer, Date expiredTime);
	DeviceAuthorization getAuthorization(String deviceId);
	void cancelAuthorization(String deviceId);
	DeviceRegistered register(String deviceId);
	void create(Device device);
	void remove(JabberId jid);
	Device getByDeviceId(String deviceId);
	Device getByDeviceName(String deviceName);
	boolean deviceIdExists(String deviceId);
	boolean deviceNameExists(String deviceName);
	String getDeviceNameByDeviceId(String deviceId);
	void registerModel(String model, ModelDescriptor modelDescriptor);
	ModelDescriptor getModelDescriptor(String model);
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
