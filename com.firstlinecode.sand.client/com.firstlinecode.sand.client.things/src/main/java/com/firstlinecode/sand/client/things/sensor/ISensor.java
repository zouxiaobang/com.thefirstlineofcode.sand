package com.firstlinecode.sand.client.things.sensor;

import java.util.Date;

import com.firstlinecode.sand.client.things.IDevice;
import com.firstlinecode.sand.client.things.IObservable;

public interface ISensor<T> extends IDevice, IObservable {
	T getMomentaryData();
	T[] getData(Date from, Date to);
	<V> V getMomentaryValue(String fieldName);
	<V> V[] getValue(String fieldName, Date from, Date to);
}
