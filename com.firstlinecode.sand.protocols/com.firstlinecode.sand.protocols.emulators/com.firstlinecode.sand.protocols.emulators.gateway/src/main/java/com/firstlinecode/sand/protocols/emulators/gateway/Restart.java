package com.firstlinecode.sand.protocols.emulators.gateway;

import com.firstlinecode.basalt.oxm.convention.conversion.annotations.String2DateTime;
import com.firstlinecode.basalt.protocol.datetime.DateTime;

public class Restart {
	@String2DateTime
	private DateTime time;

	public DateTime getTime() {
		return time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}
}
