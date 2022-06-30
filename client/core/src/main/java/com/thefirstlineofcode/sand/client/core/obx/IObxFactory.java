package com.thefirstlineofcode.sand.client.core.obx;

import com.thefirstlineofcode.basalt.oxm.binary.IBinaryXmppProtocolConverter;
import com.thefirstlineofcode.basalt.xmpp.core.Protocol;

public interface IObxFactory {
	IBinaryXmppProtocolConverter getBinaryXmppProtocolConverter();
	void registerLanAction(Class<?> lanActionType);
	boolean unregisterLanAction(Class<?> lanActionType);
	Protocol readProtocol(byte[] data);
	byte[] toBinary(Object obj);
	String toXml(byte[] data);
	Object toObject(byte[] data);
	<T> T toObject(Class<T> type, byte[] data);
}
