package com.thefirstlineofcode.sand.client.core.commuication;

import com.thefirstlineofcode.sand.protocols.core.Address;

public interface ICommunicationNetwork<A extends Address, D, P extends ParamsMap> {
	ICommunicationChip<A, D> createChip(A address, P params);
	void sendData(ICommunicationChip<A, D> from, A to, byte[] data);
	Data<A, D> receiveData(ICommunicationChip<A, D> target);
	void changeAddress(ICommunicationChip<A, D> chip, A newAddress);
	void addListener(ICommunicationNetworkListener<A, D> listener);
	boolean removeListener(ICommunicationNetworkListener<A, D> listener);
}
