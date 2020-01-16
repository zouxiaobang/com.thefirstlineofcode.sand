package com.firstlinecode.sand.server.core.stream;

import com.firstlinecode.granite.framework.core.annotations.Component;
import com.firstlinecode.granite.stream.standard.SocketMessageReceiver;

@Component("device.socket.message.receiver")
public class DeviceSocketMessageReceiver extends SocketMessageReceiver {
	@Override
	protected int getDefaultPort() {
		return 6222;
	}
}
