package com.firstlinecode.sand.client.things;

import java.util.Date;

public interface ISensor<T> extends IDevice, IObservable {
	T getCurrentData();
	T[] getData(Date from, Date to);
	<V> V getCurrentValue(String field);
	<V> V[] getValue(String field, Date from, Date to);
}
