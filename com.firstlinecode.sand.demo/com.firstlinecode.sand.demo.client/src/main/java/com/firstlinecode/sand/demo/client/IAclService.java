package com.firstlinecode.sand.demo.client;

import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.StanzaError;
import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList;

public interface IAclService {
	void retrieve();
	void retrieve(int timeout);
	void delete(AccessControlEntry entry);
	void update(AccessControlEntry entry);
	void addListener(Listener listener);
	boolean removeListener(Listener listener);
	Listener[] getListeners();
	void setLocal(AccessControlList local);
	AccessControlList getLocal();
	
	public interface Listener {
		void retrived(AccessControlList acl);
		void updated(AccessControlList acl);
		void timeout(Iq iq);
		void occurred(StanzaError error);
	}
}
