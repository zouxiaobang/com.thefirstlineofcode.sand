package com.firstlinecode.sand.client.things;

import java.util.Date;

public interface ISensor<T> extends IDevice, IObservable {
	T getMomentaryData();
	T[] getData(Date from, Date to);
	<V> V getMomentaryValue(String fieldName);
	<V> V[] getValue(String fieldName, Date from, Date to);
}
