package com.firstlinecode.sand.emulators.things.emulators;

import com.firstlinecode.sand.client.things.IThingListener;
import com.firstlinecode.sand.emulators.things.PowerEvent;

public interface IThingEmulatorListener extends IThingListener {
	void powerChanged(PowerEvent event);
}
