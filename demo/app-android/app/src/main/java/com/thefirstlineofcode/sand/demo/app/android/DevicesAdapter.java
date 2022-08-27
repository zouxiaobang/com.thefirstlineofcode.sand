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
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;
import com.thefirstlineofcode.sand.demo.protocols.AuthorizedDevice;

import java.util.Arrays;

public class DevicesAdapter extends BaseAdapter {
	private final MainActivity mainActivity;
	private String host;
	private AuthorizedDevice[] devices;
	
	public DevicesAdapter(MainActivity mainActivity, String host, AuthorizedDevice[] devices) {
		this.mainActivity = mainActivity;
		this.host = host;
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
		AuthorizedDevice device = devices[position];
		
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
		viewHolder.tvUserRole.setText(device.getRole().toString());
		
		ControlSpinner spnControlActions = convertView.findViewById(R.id.spn_control_actions);
		String[] sActions = getActions(device.getDeviceId());
		ArrayAdapter<String> actionsAdapter = new ArrayAdapter<>(mainActivity,
				android.R.layout.simple_spinner_dropdown_item, sActions);
		spnControlActions.setAdapter(actionsAdapter);
		
		if (device.getRole() != AccessControlList.Role.OWNER &&
				device.getRole() != AccessControlList.Role.CONTROLLER) {
			TextView tvControl = convertView.findViewById(R.id.tv_control);
			tvControl.setVisibility(View.INVISIBLE);
			spnControlActions.setVisibility(View.INVISIBLE);
		} else {
			spnControlActions.setOnItemSelectedListener(new ControlActionsListener(spnControlActions, device.getDeviceId()));
		}
		
		return convertView;
	}
	
	@NonNull
	private String[] getActions(String deviceId) {
		if (deviceId.startsWith("SL-")) {
			return new String[] {"Flash", "Turn On", "Turn Off"};
		} else if (deviceId.startsWith("SG-")) {
			return new String[] {"Change Mode"};
		} else if (deviceId.startsWith("SC-")) {
			return new String[] {"Take a Photo", "Take a Video", "Open Live Streaming", "Stop", "Restart", "Shutdown System"};
		} else {
			throw new RuntimeException("Unknown model of device which's ID is: " + deviceId);
		}
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
		private ControlSpinner spinner;
		private final String deviceId;
		private boolean initialState;
		private int lastSelection;
		
		public ControlActionsListener(ControlSpinner spinner, String deviceId) {
			this.spinner = spinner;
			this.deviceId = deviceId;
			initialState = true;
			lastSelection = -1;
		}
		
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			if (initialState) {
				initialState = false;
				spinner.lastSelection = pos;
				return;
			}
			
			String selectedItem = parent.getItemAtPosition(pos).toString();
			switch (selectedItem) {
				case "Take a Photo":
					mainActivity.takeAPhoto(getJidTargetByDeviceId(deviceId));
					break;
				case "Take a Video":
					mainActivity.takeAVideo(getJidTargetByDeviceId(deviceId));
					break;
				case "Open Live Streaming":
					mainActivity.openLiveSteaming(getJidTargetByDeviceId(deviceId));
					break;
				case "Stop":
					mainActivity.stop(getJidTargetByDeviceId(deviceId));
					break;
				case "Restart":
					mainActivity.restart(getJidTargetByDeviceId(deviceId));
					break;
				case "Shutdown System":
					mainActivity.shutdownSystem(getJidTargetByDeviceId(deviceId));
					break;
				case "Flash":
					mainActivity.flash(getJidTargetByDeviceId(deviceId));
					break;
				case "Turn On":
					mainActivity.turnOn(getJidTargetByDeviceId(deviceId));
					break;
				case "Turn Off":
					mainActivity.turnOff(getJidTargetByDeviceId(deviceId));
					break;
				case "Change Mode":
					mainActivity.changeMode(getJidTargetByDeviceId(deviceId));
					break;
				default:
					throw new RuntimeException("Unknown command: " + selectedItem);
			}
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> parent) {}
	}
	
	private JabberId getJidTargetByDeviceId(String deviceId) {
		for (AuthorizedDevice device : devices) {
			if (device.getDeviceId().equals(deviceId)) {
				return getJidTargetByDeviceLocation(device.getDeviceLocation());
			}
		}
		
		throw new RuntimeException(String.format("Unknown device ID: %s.", deviceId));
	}
	
	private JabberId getJidTargetByDeviceLocation(String deviceLocation) {
		int slashIndex = deviceLocation.indexOf('/');
		if (slashIndex == -1) {
			return new JabberId(deviceLocation, host);
		} else {
			String node = deviceLocation.substring(0, slashIndex);
			String resource = deviceLocation.substring(slashIndex + 1, deviceLocation.length());
			
			return new JabberId(node, host, resource);
		}
	}
	
	public void setDevices(AuthorizedDevice[] devices) {
		this.devices = devices;
	}
	
	public void updateDevices(AuthorizedDevice[] updatedDevices) {
		if (devices == null) {
			devices = updatedDevices;
		} else {
			if (updatedDevices == null || updatedDevices.length == 0)
				return;
			
			for (AuthorizedDevice device : updatedDevices) {
				if (containsDevice(device)) {
					updateDevice(device);
				} else {
					addDevice(device);
				}
			}
		}
	}
	
	private void addDevice(AuthorizedDevice device) {
		if (devices == null || devices.length == 0) {
			devices = new AuthorizedDevice[]{device};
		} else {
			AuthorizedDevice[] newDevices = new AuthorizedDevice[devices.length + 1];
			
			newDevices = Arrays.copyOf(devices, devices.length + 1);
			newDevices[newDevices.length - 1] = device;
			
			devices = newDevices;
		}
	}
	
	private void updateDevice(AuthorizedDevice device) {
		for (int i = 0; i < devices.length; i++) {
			if (devices[i].getDeviceId().equals(device.getDeviceId())) {
				devices[i] = device;
				return;
			}
		}
		
		throw new RuntimeException("Can't find a device for update.");
	}
	
	private boolean containsDevice(AuthorizedDevice device) {
		if (devices == null || devices.length == 0)
			return false;
		
		for (AuthorizedDevice aDevice : devices) {
			if (aDevice.equals(device))
				return true;
		}
		
		return false;
	}
}
