package com.thefirstlineofcode.sand.demo.app.android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlEntry;

public class DevicesAdapter extends BaseAdapter {
	private AccessControlList acl;
	
	public DevicesAdapter(AccessControlList acl) {
		this.acl = acl;
	}
	
	@Override
	public int getCount() {
		if (acl == null || acl.getEntries() == null || acl.getEntries().size() == 0)
			return 0;
		
		return acl.getEntries().size();
	}
	
	@Override
	public Object getItem(int position) {
		if (acl == null || acl.getEntries() == null || acl.getEntries().size() == 0)
			return null;
		
		return acl.getEntries().get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}
	void updateAcl(AccessControlList updatedAcl) {
		if (acl == null) {
			acl = updatedAcl;
		} else {
			if (updatedAcl.getEntries() == null || updatedAcl.getEntries().size() == 0)
				return;
			
			for (AccessControlEntry entry : updatedAcl.getEntries()) {
				if (acl.getEntries().contains(entry)) {
					acl.update(entry);
				} else {
					acl.add(entry);
				}
			}
		}
	}
}
