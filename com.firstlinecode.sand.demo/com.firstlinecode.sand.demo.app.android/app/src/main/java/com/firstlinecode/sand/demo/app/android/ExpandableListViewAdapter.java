package com.firstlinecode.sand.demo.app.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {
	private AccessControlList acl;
	private Context context;
	private AccessControlEntry[] things;
	private Map<String, AccessControlEntry[]> nodes;

	ExpandableListViewAdapter(Context context, AccessControlList acl) {
		this.context = context;
		updateAcl(acl);
	}

	void updateAcl(AccessControlList acl) {
		this.acl = acl;
		if (acl == null) {
			things = new AccessControlEntry[0];
			nodes = new HashMap<>();

			return;
		}

		readThingsAndNodes(acl);
	}

	private void readThingsAndNodes(AccessControlList acl) {
		List<AccessControlEntry> things = new ArrayList<>();
		for (AccessControlEntry entry : acl.getEntries()) {
			if (entry.getParent() == null) {
				things.add(entry);
			}
		}

		this.things = things.toArray(new AccessControlEntry[things.size()]);

		for (AccessControlEntry thing : things) {
			String thingDeviceId = thing.getDevice();
			List<AccessControlEntry> nodes = new ArrayList<AccessControlEntry>();
			for (AccessControlEntry entry : acl.getEntries()) {
				if (thingDeviceId.equals(entry.getParent())) {
					 nodes.add(entry);
				}
			}
			this.nodes.put(thingDeviceId, nodes.toArray(new AccessControlEntry[nodes.size()]));
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


		return Objects.requireNonNull(nodes.get(things[groupPosition].getDevice())).length;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return things[groupPosition];
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return Objects.requireNonNull(nodes.get(things[groupPosition].getDevice()))[childPosition];
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition * 10 + childPosition;
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
		tvDeviceId.setText(Objects.requireNonNull(nodes.get(things[groupPosition].getDevice()))[childPosition].getDevice());

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
