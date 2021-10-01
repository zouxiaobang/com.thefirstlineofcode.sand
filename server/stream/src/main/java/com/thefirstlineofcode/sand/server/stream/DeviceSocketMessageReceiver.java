package com.thefirstlineofcode.sand.server.stream;

import com.thefirstlineofcode.granite.framework.core.annotations.Component;
import com.thefirstlineofcode.granite.stream.standard.SocketMessageReceiver;

@Component("device.socket.message.receiver")
public class DeviceSocketMessageReceiver extends SocketMessageReceiver {
	@Override
	protected int getDefaultPort() {
		return 6222;
	}
}
