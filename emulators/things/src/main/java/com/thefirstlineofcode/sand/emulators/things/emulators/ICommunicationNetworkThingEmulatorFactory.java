package com.thefirstlineofcode.sand.emulators.things.emulators;

import com.thefirstlineofcode.sand.client.things.commuication.ICommunicator;

public interface ICommunicationNetworkThingEmulatorFactory<OA, PA, D, T extends IThingEmulator> extends IThingEmulatorFactory<T> {
	T create(ICommunicator<OA, PA, D> communicator);
}
