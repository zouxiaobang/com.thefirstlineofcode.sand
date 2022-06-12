package com.thefirstlineofcode.sand.server.location;

import java.util.List;

import com.thefirstlineofcode.sand.protocols.location.DeviceLocation;

public interface ILocationService {
	List<DeviceLocation> locateDevices(List<String> deviceIds);
}
