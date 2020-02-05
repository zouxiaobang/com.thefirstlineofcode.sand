package com.firstlinecode.sand.client.things.commuication;

import java.util.Map;

public interface ICommunicationNetwork<A, D> {
	ICommunicationChip<A> createChip(A address, Map<String, Object> params);
	void sendMessage(ICommunicationChip<A> from, A to, byte[] data);
	Message<A, D> receiveMessage(ICommunicationChip<A> target);
	void changeAddress(ICommunicationChip<A> chip, A newAddress);
	void addListener(ICommunicationNetworkListener<A> listener);
	boolean removeListener(ICommunicationNetworkListener<A> listener);
}
