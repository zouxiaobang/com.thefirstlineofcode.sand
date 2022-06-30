package com.thefirstlineofcode.sand.demo.client;

import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.StanzaError;
import com.thefirstlineofcode.sand.demo.protocols.AuthorizedDevices;

public interface IAuthorizedDevicesService {
	void retrieve();
	void addListener(Listener listener);
	boolean removeListener(Listener listener);
	
	public interface Listener {
		void retrieved(AuthorizedDevices devices);
		void timeout();
		void occurred(StanzaError error);
	}
}
