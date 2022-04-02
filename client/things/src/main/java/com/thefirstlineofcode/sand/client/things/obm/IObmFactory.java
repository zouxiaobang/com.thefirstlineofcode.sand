package com.thefirstlineofcode.sand.client.things.obm;

import com.thefirstlineofcode.basalt.oxm.binary.IBinaryXmppProtocolConverter;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

public interface IObmFactory {
	IBinaryXmppProtocolConverter getBinaryXmppProtocolConverter();
	void registerLanAction(Class<?> lanActionType);
	boolean unregisterLanAction(Class<?> lanActionType);
	Protocol readProtocol(byte[] data);
	byte[] toBinary(Object obj);
	Object toObject(byte[] data);
	<T> T toObject(Class<T> type, byte[] data);
}
