package com.firstlinecode.sand.emulators.thing;

import com.firstlinecode.sand.client.things.commuication.ICommunicator;

public interface ICommunicationNetworkThingEmulator<OA, PA, D> extends IThingEmulator {
	void startToReceiveData();
	void stopDataReceving();
	ICommunicator<OA, PA, D> getCommunicator();
}
