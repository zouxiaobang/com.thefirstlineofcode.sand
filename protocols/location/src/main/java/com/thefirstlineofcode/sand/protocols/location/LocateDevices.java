package com.thefirstlineofcode.sand.protocols.location;

import java.util.List;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.Array;
import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.oxm.convention.annotations.TextOnly;
import com.thefirstlineofcode.basalt.oxm.convention.validation.annotations.Validate;
import com.thefirstlineofcode.basalt.oxm.convention.validation.annotations.ValidationClass;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.BadRequest;

@ValidationClass
@ProtocolObject(namespace = "urn:leps:iot:location", localName = "locate-devices")
public class LocateDevices {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:location", "locate-devices");
	
	@Array(value = String.class, elementName = "device-id")
	@TextOnly
	private List<String> deviceIds;
	
	@Array(value = DeviceLocation.class, elementName = "device-location")
	private List<DeviceLocation> deviceLocations;
	
	public LocateDevices() {}

	public List<String> getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(List<String> deviceIds) {
		this.deviceIds = deviceIds;
	}

	public List<DeviceLocation> getDeviceLocations() {
		return deviceLocations;
	}

	public void setDeviceLocations(List<DeviceLocation> deviceLocations) {
		this.deviceLocations = deviceLocations;
	}
	
	@Validate("/")
	public void validateLocateDevices(LocateDevices locateDevices) {
		if (locateDevices.getDeviceIds() != null && locateDevices.getDeviceLocations() != null)
			throw new ProtocolException(new BadRequest("Only one child element('device-ids' or 'device-locations') is allowed."));
	}
}
