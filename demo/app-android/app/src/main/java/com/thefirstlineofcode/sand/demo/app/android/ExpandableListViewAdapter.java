package com.thefirstlineofcode.sand.demo.app.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.thefirstlineofcode.sand.demo.app.android.R;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlEntry;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {
	private AccessControlList acl;
	private Context context;
	private AccessControlEntry[] things;
	private Map<String, AccessControlEntry[]> concentratorAndNodes;

	ExpandableListViewAdapter(Context context, AccessControlList acl) {
		this.context = context;
		updateAcl(acl);
	}

	void updateAcl(AccessControlList updatedAcl) {
		if (acl == null) {
			acl = updatedAcl;
		} else {
			for (AccessControlEntry entry : updatedAcl.getEntries()) {
				if (acl.contains(entry)) {
					acl.update(entry);
				} else {
					acl.add(entry);
				}
			}
		}

		readThingsAndNodes();
	}

	private void readThingsAndNodes() {
		if (acl == null) {
			this.things = new AccessControlEntry[0];
			this.concentratorAndNodes = new HashMap<>();

			return;
		}

		List<AccessControlEntry> thingsList = new ArrayList<>();
		for (AccessControlEntry entry : acl.getEntries()) {
			if (entry.getParent() == null) {
				thingsList.add(entry);
			}
		}
		this.things = thingsList.toArray(new AccessControlEntry[] {});

		concentratorAndNodes = new HashMap<>();
		for (AccessControlEntry thing : thingsList) {
			String thingDeviceId = thing.getDevice();
			List<AccessControlEntry> nodes = new ArrayList<>();
			for (AccessControlEntry entry : acl.getEntries()) {
				if (thingDeviceId.equals(entry.getParent())) {
					 nodes.add(entry);
				}
			}

			this.concentratorAndNodes.put(thingDeviceId, nodes.toArray(new AccessControlEntry[] {}));
		}
	}

	@Override
	public int getGroupCount() {
		if (acl == null)
			return 0;

		return things.length;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (acl == null)
			return 0;


		return Objects.requireNonNull(concentratorAndNodes.get(things[groupPosition].getDevice())).length;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return things[groupPosition];
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return Objects.requireNonNull(concentratorAndNodes.get(things[groupPosition].getDevice()))[childPosition];
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition * 10L + childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = Objects.requireNonNull(inflater).inflate(R.layout.thing, null);
		}

		TextView tvDeviceId = convertView.findViewById(R.id.thing_device_id);
		tvDeviceId.setTypeface(null, Typeface.BOLD);
		tvDeviceId.setText(things[groupPosition].getDevice());

		return convertView;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = Objects.requireNonNull(inflater).inflate(R.layout.node, null);
		}

		TextView tvDeviceId = convertView.findViewById(R.id.node_device_id);
		tvDeviceId.setTypeface(null, Typeface.BOLD);
		tvDeviceId.setText(Objects.requireNonNull(concentratorAndNodes.get(things[groupPosition].getDevice()))[childPosition].getDevice());

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
