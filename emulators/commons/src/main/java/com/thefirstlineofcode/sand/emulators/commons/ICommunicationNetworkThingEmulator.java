package com.thefirstlineofcode.sand.emulators.commons;

import com.thefirstlineofcode.sand.client.core.commuication.ICommunicator;
import com.thefirstlineofcode.sand.protocols.core.Address;

public interface ICommunicationNetworkThingEmulator<OA, PA extends Address, D> extends IThingEmulator {
	void startToReceiveData();
	void stopDataReceving();
	ICommunicator<OA, PA, D> getCommunicator();
}
