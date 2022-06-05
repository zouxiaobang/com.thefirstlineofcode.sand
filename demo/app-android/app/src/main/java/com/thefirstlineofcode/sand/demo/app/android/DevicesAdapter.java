package com.thefirstlineofcode.sand.demo.app.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlEntry;

public class DevicesAdapter extends BaseAdapter {
	private MainActivity mainActivity;
	private AccessControlList acl;
	
	public DevicesAdapter(MainActivity mainActivity, AccessControlList acl) {
		this.mainActivity = mainActivity;
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
		AccessControlEntry ace = acl.getEntries().get(position);
		convertView = LayoutInflater.from(mainActivity).inflate(R.layout.device_view, parent, false);
		
		TextView tvDeviceId = (TextView)convertView.findViewById(R.id.tv_device_id);
		tvDeviceId.setText(ace.getDeviceId());
		
		TextView tvUserRole = (TextView)convertView.findViewById(R.id.tv_user_role);
		tvUserRole.setText(ace.getRole().toString());
		
		Spinner spnControlActions = (Spinner)convertView.findViewById(R.id.spn_control_actions);
		String[] sActions = new String[] {"Take a Photo", "Take a Video", "Open Live Streaming"};
		ArrayAdapter actionsAdapter = new ArrayAdapter(mainActivity,
				android.R.layout.simple_spinner_dropdown_item, sActions);
		spnControlActions.setAdapter(actionsAdapter);
		
		if (ace.getRole() == AccessControlList.Role.OWNER ||
				ace.getRole() == AccessControlList.Role.CONTROLLER) {
			TextView tvControl = convertView.findViewById(R.id.tv_control);
			tvControl.setVisibility(View.INVISIBLE);
			spnControlActions.setVisibility(View.INVISIBLE);
		} else {
			spnControlActions.setOnItemSelectedListener(new ControlActionsListener(ace.getDeviceId()));
		}
		
		return convertView;
	}
	
	private class ControlActionsListener implements AdapterView.OnItemSelectedListener {
		private String deviceId;
		
		public ControlActionsListener(String deviceId) {
			this.deviceId = deviceId;
		}
		
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			String selectedItem = parent.getItemAtPosition(pos).toString();
			switch (selectedItem) {
				case "Take a Photo":
					mainActivity.takeAPhoto(deviceId);
					break;
				case "Take a Video":
					mainActivity.takeAVideo(deviceId);
					break;
				case "Open Live Steaming":
					mainActivity.openLiveSteaming(deviceId);
					break;
				default:
					break;
			}
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> parent) {}
	}
	
	public void setAcl(AccessControlList acl) {
		this.acl = acl;
	}
	
	public void updateAcl(AccessControlList updatedAcl) {
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
