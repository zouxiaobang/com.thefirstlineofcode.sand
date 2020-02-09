package com.firstlinecode.sand.client.things.commuication;

public interface ICommunicationNetwork<A, D, P extends ParamsMap> {
	ICommunicationChip<A, D> createChip(A address, P params);
	void sendMessage(ICommunicationChip<A, D> from, A to, byte[] data);
	Message<A, D> receiveMessage(ICommunicationChip<A, D> target);
	void changeAddress(ICommunicationChip<A, D> chip, A newAddress);
	void addListener(ICommunicationNetworkListener<A, D> listener);
	boolean removeListener(ICommunicationNetworkListener<A, D> listener);
}
