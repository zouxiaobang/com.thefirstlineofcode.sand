package com.thefirstlineofcode.sand.client.things.obm;

public interface IObmFactory {
	byte[] toBinary(Object obj);
	<T> T toObject(Class<T> type, byte[] data);
}
