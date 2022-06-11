package com.thefirstlineofcode.sand.demo.app.android;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;

import java.util.Arrays;

public class DevicesAdapter extends BaseAdapter {
	private final MainActivity mainActivity;
	private Device[] devices;
	
	public DevicesAdapter(MainActivity mainActivity, Device[] devices) {
		this.mainActivity = mainActivity;
		this.devices = devices;
	}
	
	@Override
	public int getCount() {
		if (devices == null || devices.length == 0)
			return 0;
		
		return devices.length;
	}
	
	@Override
	public Object getItem(int position) {
		if (devices == null || devices.length == 0)
			return null;
		
		return devices[position];
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Device device = devices[position];
		
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mainActivity).inflate(R.layout.device_view, parent, false);
			
			viewHolder = new ViewHolder();
			viewHolder.tvDeviceId = convertView.findViewById(R.id.tv_device_id);
			viewHolder.tvUserRole = convertView.findViewById(R.id.tv_user_role);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		viewHolder.tvDeviceId.setText(device.getDeviceId());
		viewHolder.tvUserRole.setText(device.getAce().getRole().toString());
		
		Spinner spnControlActions = convertView.findViewById(R.id.spn_control_actions);
		String[] sActions = new String[] {"Take a Photo", "Take a Video", "Open Live Streaming"};
		ArrayAdapter<String> actionsAdapter = new ArrayAdapter<>(mainActivity,
				android.R.layout.simple_spinner_dropdown_item, sActions);
		spnControlActions.setAdapter(actionsAdapter);
		
		if (device.getAce().getRole() != AccessControlList.Role.OWNER &&
				device.getAce().getRole() != AccessControlList.Role.CONTROLLER) {
			TextView tvControl = convertView.findViewById(R.id.tv_control);
			tvControl.setVisibility(View.INVISIBLE);
			spnControlActions.setVisibility(View.INVISIBLE);
		} else {
			spnControlActions.setOnItemSelectedListener(new ControlActionsListener(device.getDeviceId()));
		}
		
		return convertView;
	}
	
	private static class ViewHolder {
		private TextView tvDeviceId;
		private TextView tvUserRole;
	}
	
	public static class ControlSpinner extends androidx.appcompat.widget.AppCompatSpinner {
		private int lastSelection = -1;
		
		public ControlSpinner(Context context) {
			super(context);
		}
		
		public ControlSpinner(Context context, int mode) {
			super(context, mode);
		}
		
		public ControlSpinner(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		
		public ControlSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
			super(context, attrs, defStyleAttr);
		}
		
		public ControlSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
			super(context, attrs, defStyleAttr, mode);
		}
		
		public ControlSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
			super(context, attrs, defStyleAttr, mode, popupTheme);
		}
		
		@Override
		public void setSelection(int position) {
			super.setSelection(position);
			
			if (lastSelection != -1 && position == lastSelection) {
				getOnItemSelectedListener().onItemSelected(this, null, position, 0);
			}
			lastSelection = position;
		}
		
		@Override
		public void setSelection(int position, boolean animate) {
			super.setSelection(position, animate);
			
			if (lastSelection != -1 && position == lastSelection) {
				getOnItemSelectedListener().onItemSelected(this, null, position, 0);
			}
			lastSelection = position;
		}
	}
	
	private class ControlActionsListener implements AdapterView.OnItemSelectedListener {
		private final String deviceId;
		private boolean initialState;
		private int lastSelection;
		
		public ControlActionsListener(String deviceId) {
			this.deviceId = deviceId;
			initialState = true;
			lastSelection = -1;
		}
		
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			if (initialState) {
				initialState = false;
				return;
			}
			
			String selectedItem = parent.getItemAtPosition(pos).toString();
			switch (selectedItem) {
				case "Take a Photo":
					mainActivity.takeAPhoto(deviceId);
					break;
				case "Take a Video":
					mainActivity.takeAVideo(deviceId);
					break;
				case "Open Live Streaming":
					mainActivity.openLiveSteaming(deviceId);
					break;
				default:
					break;
			}
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> parent) {}
	}
	
	public void setDevices(Device[] devices) {
		this.devices = devices;
	}
	
	public void updateDevices(Device[] updatedDevices) {
		if (devices == null) {
			devices = updatedDevices;
		} else {
			if (updatedDevices == null || updatedDevices.length == 0)
				return;
			
			for (Device device : updatedDevices) {
				if (containsDevice(device)) {
					updateDevice(device);
				} else {
					addDevice(device);
				}
			}
		}
	}
	
	private void addDevice(Device device) {
		if (devices == null || devices.length == 0) {
			devices = new Device[]{device};
		} else {
			devices = new Device[devices.length + 1];
			
			devices = Arrays.copyOf(devices, devices.length + 1);
			devices[devices.length - 1] = device;
		}
	}
	
	private void updateDevice(Device device) {
		for (int i = 0; i < devices.length; i++) {
			if (devices[i].getDeviceId().equals(device.getDeviceId())) {
				devices[i] = device;
				return;
			}
		}
		
		throw new RuntimeException("Can't find a device for update.");
	}
	
	private boolean containsDevice(Device device) {
		if (devices == null || devices.length == 0)
			return false;
		
		for (Device aDevice : devices) {
			if (aDevice.equals(device))
				return true;
		}
		
		return false;
	}
}
