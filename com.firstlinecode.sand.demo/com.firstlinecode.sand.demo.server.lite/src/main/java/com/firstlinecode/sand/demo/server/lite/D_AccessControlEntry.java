package com.firstlinecode.sand.demo.server.lite;

import java.util.Date;

import com.firstlinecode.granite.framework.core.supports.data.IIdProvider;
import com.firstlinecode.sand.demo.protocols.AccessControlEntry;

public class D_AccessControlEntry extends AccessControlEntry implements IIdProvider<String> {
	private String id;
	private Date lastModifiedTime;

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

}
