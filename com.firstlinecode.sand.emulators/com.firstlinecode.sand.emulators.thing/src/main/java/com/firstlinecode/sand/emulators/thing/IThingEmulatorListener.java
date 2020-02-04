package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.client.things.IThingListener;

public interface IThingEmulatorListener extends IThingListener {
	void powerChanged(PowerEvent event);
}
