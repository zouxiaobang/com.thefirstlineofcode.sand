package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.client.things.commuication.ICommunicator;
import com.firstlinecode.sand.client.things.commuication.ParamsMap;

public interface IThingEmulatorFactory<T extends IThingEmulator> {
	String getThingName();
	<P extends ParamsMap> T create(ICommunicator<?, ?> communicator, P params);
}
