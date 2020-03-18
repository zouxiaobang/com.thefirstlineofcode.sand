package com.firstlinecode.sand.client.things.obm;

public interface IObmFactory {
	byte[] toBinary(Object obj);
	Object toObject(Class<?> type, byte[] data);
}
