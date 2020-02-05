package com.firstlinecode.sand.client.things.commuication;

public interface ICommunicationNetworkListener<T> {
	void sent(ICommunicationChip<T> from, T to, byte[] message);
	void received(ICommunicationChip<T> from, T to, byte[] message);
	void collided(ICommunicationChip<T> from, T to, byte[] message);
	void lost(ICommunicationChip<T> from, T to, byte[] message);
	void addressChanged(ICommunicationChip<T> chip, T oldAddress);
}
