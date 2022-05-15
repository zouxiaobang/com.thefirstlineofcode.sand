package com.thefirstlineofcode.sand.emulators.commons;

import com.thefirstlineofcode.sand.client.core.commuication.ICommunicator;
import com.thefirstlineofcode.sand.protocols.core.Address;

public interface ICommunicationNetworkThingEmulatorFactory<OA, PA extends Address, D, T extends IThingEmulator> extends IThingEmulatorFactory<T> {
	T create(ICommunicator<OA, PA, D> communicator);
}
