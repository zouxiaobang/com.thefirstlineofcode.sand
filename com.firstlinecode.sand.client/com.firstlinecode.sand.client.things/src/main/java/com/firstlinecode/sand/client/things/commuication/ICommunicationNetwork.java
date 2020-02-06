package com.firstlinecode.sand.client.things.commuication;

public interface ICommunicationNetwork<A, D, P extends ParamsMap> {
	ICommunicationChip<A> createChip(A address, P params);
	void sendMessage(ICommunicationChip<A> from, A to, byte[] data);
	Message<A, D> receiveMessage(ICommunicationChip<A> target);
	void changeAddress(ICommunicationChip<A> chip, A newAddress);
	void addListener(ICommunicationNetworkListener<A> listener);
	boolean removeListener(ICommunicationNetworkListener<A> listener);
}
