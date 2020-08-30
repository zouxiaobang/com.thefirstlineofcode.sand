package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.client.things.commuication.ICommunicator;

public interface ICommunicationNetworkThingEmulatorFactory<OA, PA, D, T extends IThingEmulator> extends IThingEmulatorFactory<T> {
	T create(ICommunicator<OA, PA, D> communicator);
}
