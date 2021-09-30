package com.firstlinecode.sand.demo.client;

import java.util.List;

import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.datetime.DateTime;

public interface IAclService {
	void retrieve();
	void retrieve(String deviceId);
	void retrieve(String deviceId, DateTime lastModifiedTime);
	void retrieve(int timeout);
	void retrieve(String deviceId, int timeout);
	void retrieve(String deviceId, DateTime lastModifiedTime, int timeout);
	void delete(AccessControlEntry entry);
	void update(AccessControlEntry entry);
	void addListener(Listener listener);
	boolean removeListener(Listener listener);
	List<Listener> getListeners();
	void setLocal(AccessControlList local);
	AccessControlList getLocal();
	
	public interface Listener {
		void retrived(AccessControlList acl);
		void updated(AccessControlList acl);
		void timeout(Iq iq);
		void occurred(AclError error);
	}
}
