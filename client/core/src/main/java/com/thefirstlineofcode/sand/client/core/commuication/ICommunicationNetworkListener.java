package com.thefirstlineofcode.sand.client.core.commuication;

public interface ICommunicationNetworkListener<A, D> {
	void sent(A from, A to, D data);
	void received(A from, A to, D data);
	void addressChanged(A newAddress, A oldAddress);
}
